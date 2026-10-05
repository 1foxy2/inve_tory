package net.foxy.inve_tory.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.foxy.inve_tory.InveTory;
import net.foxy.inve_tory.base.ITAttachments;
import net.foxy.inve_tory.config.InveToryClientConfig;
import net.foxy.inve_tory.config.SlotPreset;
import net.foxy.inve_tory.network.c2s.SyncPresetPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen {
    protected AbstractContainerScreenMixin(Component title) {
        super(title);
    }

    @Shadow
    protected abstract boolean isHovering(Slot slot, double mouseX, double mouseY);

    @Shadow
    @Final
    protected T menu;

    @Shadow
    protected abstract void renderSlot(GuiGraphics guiGraphics, Slot slot);

    @Shadow
    public static void renderSlotHighlight(GuiGraphics guiGraphics, int x, int y, int blitOffset, int color) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Unique
    private Slot inve_tory$hoveredDisabledSlot = null;

    @Inject(
            method = "render",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V", ordinal = 0)
    )
    private void resetHovered(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        inve_tory$hoveredDisabledSlot = null;
    }

    @WrapOperation(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;isActive()Z", ordinal = 0)
    )
    private boolean renderDisabledSlot(Slot instance, Operation<Boolean> original,
                                       @Local(argsOnly = true) GuiGraphics guiGraphics,
                                       @Local(argsOnly = true, ordinal = 0) int mX,
                                       @Local(argsOnly = true, ordinal = 1) int mY
    ) {
        if (original.call(instance)) {
            return true;
        }

        if (instance.container.inve_tory$isDisabled(instance.getSlotIndex())) {
            if (instance.hasItem()) {
                renderSlotHighlight(guiGraphics, instance.x, instance.y, 0, 0xFFab7f7f);
                renderSlot(guiGraphics, instance);
            } else {
                guiGraphics.blitSprite(InveTory.INV_DISABLED_SLOT_LOCATION_SPRITE, instance.x - 1, instance.y - 1, 18, 18);
            }
            if (InveToryClientConfig.CONFIG.preset.get() == SlotPreset.CUSTOM && isHovering(instance, mX, mY)) {
                inve_tory$hoveredDisabledSlot = instance;
            }
        }
        return false;
    }

    @Inject(
            method = "renderTooltip",
            at = @At(value = "RETURN")
    )
    private void renderDisabledSlotTooltip(GuiGraphics guiGraphics, int x, int y, CallbackInfo ci
    ) {
        if (this.menu.getCarried().isEmpty() && this.inve_tory$hoveredDisabledSlot != null) {
            Player player = minecraft.player;
            if (player != null) {
                int slotsAvailable = player.getData(ITAttachments.UNLOCKED_SLOTS) - player.getData(ITAttachments.SLOTS_PRESET.get()).size();
                if (slotsAvailable > 0) {
                    guiGraphics.renderTooltip(this.font, List.of(
                            Component.translatable("gui.inve_tory.open_slot"),
                            Component.translatable("gui.inve_tory.available_slots", slotsAvailable)
                    ), Optional.empty(), x, y);
                }
            }
        }
    }

    @WrapOperation(
            method = "mouseClicked",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;findSlot(DD)Lnet/minecraft/world/inventory/Slot;")
    )
    private Slot unlockSlot(AbstractContainerScreen<T> instance, double mouseX, double mouseY, Operation<Slot> original) {
        Slot originalSlot = original.call(instance, mouseX, mouseY);
        if (originalSlot == null && InveToryClientConfig.CONFIG.preset.get() == SlotPreset.CUSTOM) {
            Slot slot = null;
            for (int i = 0; i < this.menu.slots.size(); i++) {
                Slot s = this.menu.slots.get(i);
                if (this.isHovering(s, mouseX, mouseY) && s.container.inve_tory$isDisabled(s.getSlotIndex())) {
                    slot = s;
                    break;
                }
            }

            if (slot != null && slot.container.inve_tory$isDisabled(slot.getSlotIndex()) && minecraft != null) {
                Player player = minecraft.player;
                if (player != null) {
                    int slotsAvailable = player.getData(ITAttachments.UNLOCKED_SLOTS) - player.getData(ITAttachments.SLOTS_PRESET.get()).size();
                    if (slotsAvailable > 0) {
                        player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.4F, 1.0f);
                        List<Integer> newList = new ArrayList<>(InveToryClientConfig.CONFIG.custom_preset.get());
                        newList.add(slot.getSlotIndex());
                        InveToryClientConfig.CONFIG.custom_preset.set(newList);
                        InveToryClientConfig.CONFIG_SPEC.save();
                        PacketDistributor.sendToServer(SyncPresetPayload.create());
                    }
                }
            }
        }
        return originalSlot;
    }
}

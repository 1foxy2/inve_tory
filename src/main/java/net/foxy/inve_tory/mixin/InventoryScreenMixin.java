package net.foxy.inve_tory.mixin;

import net.foxy.inve_tory.InveToryClient;
import net.foxy.inve_tory.config.InveToryClientConfig;
import net.foxy.inve_tory.config.SlotPreset;
import net.foxy.inve_tory.network.c2s.SyncPresetPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends EffectRenderingInventoryScreen<InventoryMenu> {
    @Shadow
    private boolean buttonClicked;

    public InventoryScreenMixin(InventoryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Unique
    private boolean inve_tory$isDisablingSlots = false;

    @Inject(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/inventory/InventoryScreen;addRenderableWidget(" +
                            "Lnet/minecraft/client/gui/components/events/GuiEventListener;" +
                            ")Lnet/minecraft/client/gui/components/events/GuiEventListener;"
            )
    )
    private void addSlotResetWidget(CallbackInfo ci) {
        if (InveToryClientConfig.CONFIG.preset.get() != SlotPreset.CUSTOM || InveToryClientConfig.CONFIG.add_toggle_button.isFalse()) {
            return;
        }
        addRenderableWidget(new ImageButton(this.leftPos + 148, this.height / 2 - 22, 20, 18, InveToryClient.RESET_BUTTON_SPRITES, button -> {
            this.buttonClicked = true;
            this.inve_tory$isDisablingSlots = !inve_tory$isDisablingSlots;
        }) {
            @Override
            public int getX() {
                return leftPos + 148;
            }

            @Override
            public boolean isFocused() {
                return inve_tory$isDisablingSlots;
            }
        });
    }

    @Inject(
            method = "slotClicked",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void disableSlot(Slot slot, int slotId, int mouseButton, ClickType type, CallbackInfo ci) {
        if (slot != null && InveToryClientConfig.CONFIG.preset.get() == SlotPreset.CUSTOM && inve_tory$isDisablingSlots &&
                !slot.hasItem() && 8 < slot.getSlotIndex() && slot.getSlotIndex() < 36) {
            Player player = minecraft.player;
            if (player != null) {
                player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.4F, 0.75f);
                List<Integer> newList = new ArrayList<>(InveToryClientConfig.CONFIG.custom_preset.get());
                if (newList.remove((Object) slot.getSlotIndex())) {
                    InveToryClientConfig.CONFIG.custom_preset.set(newList);
                    InveToryClientConfig.CONFIG_SPEC.save();
                    PacketDistributor.sendToServer(SyncPresetPayload.create());
                }
            }

            ci.cancel();
        }
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/inventory/InventoryScreen;" +
                            "renderTooltip(Lnet/minecraft/client/gui/GuiGraphics;II)V"
            )
    )
    private void disableSlotTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (InveToryClientConfig.CONFIG.preset.get() == SlotPreset.CUSTOM &&
                inve_tory$isDisablingSlots && hoveredSlot != null && !hoveredSlot.hasItem() &&
                !hoveredSlot.container.inve_tory$isDisabled(hoveredSlot.getSlotIndex()) &&
                8 < hoveredSlot.getSlotIndex() && hoveredSlot.getSlotIndex() < 36) {
            guiGraphics.renderTooltip(this.font, Component.translatable("gui.inve_tory.close_slot"), mouseX, mouseY);
        }
    }
}

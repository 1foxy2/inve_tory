package net.foxy.inve_tory.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin {
    @ModifyExpressionValue(
            method = "moveItemStackTo",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isSameItemSameComponents(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z", ordinal = 0)
    )
    private boolean disableStackableSlot(boolean original, @Local Slot slot) {
        return original && !slot.container.inve_tory$isDisabled(slot.getSlotIndex());
    }

    @ModifyExpressionValue(
            method = "moveItemStackTo",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;mayPlace(Lnet/minecraft/world/item/ItemStack;)Z", ordinal = 0)
    )
    private boolean disableSlot(boolean original, @Local Slot slot) {
        return original && !slot.container.inve_tory$isDisabled(slot.getSlotIndex());
    }
}

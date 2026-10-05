package net.foxy.inve_tory.mixin;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(
            method = "handleEquipmentChanges",
            at = @At("HEAD")
    )
    private void dropItems(Map<EquipmentSlot, ItemStack> equipments, CallbackInfo ci) {
        if ((Object) this instanceof Player player) {
            Inventory inventory = player.getInventory();
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack stack = inventory.getItem(i);
                if (!stack.isEmpty() && inventory.inve_tory$isDisabled(i)) {
                    player.drop(stack, true, false);
                    inventory.setItem(i, ItemStack.EMPTY);
                }
            }
        }
    }
}

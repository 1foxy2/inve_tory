package net.foxy.inve_tory.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.foxy.inve_tory.InveTory;
import net.foxy.inve_tory.base.ITAttachments;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(Inventory.class)
public abstract class InventoryMixin implements Container {
    @Shadow
    @Final
    public Player player;

    @Override
    public boolean inve_tory$isDisabled(int index) {
        if (8 < index && index < 36) {
            List<Integer> preset = player.getData(ITAttachments.SLOTS_PRESET);
            int unlocked = Math.min(InveTory.getUnlockedSlots(player), preset.size());
            for (int i = 0; i < unlocked; i++) {
                if (preset.get(i) == index) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @ModifyExpressionValue(
            method = "getFreeSlot",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z")
    )
    private boolean disableSlot(boolean original, @Local int i) {
        return original && !inve_tory$isDisabled(i);
    }
}

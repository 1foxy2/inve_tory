package net.foxy.inve_tory.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Slot.class)
public abstract class SlotMixin {
    @Shadow
    @Final
    public Container container;

    @Shadow
    public abstract int getSlotIndex();

    @ModifyReturnValue(
            method = "isActive",
            at = @At("RETURN")
    )
    private boolean disable(boolean original) {
        return original && !container.inve_tory$isDisabled(getSlotIndex());
    }
}

package net.foxy.inve_tory.mixin;

import net.foxy.inve_tory.api.ExtendedContainer;
import net.minecraft.world.Container;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Container.class)
public interface ContainerMixin extends ExtendedContainer {
}

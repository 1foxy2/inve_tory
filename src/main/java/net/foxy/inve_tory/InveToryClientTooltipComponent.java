package net.foxy.inve_tory;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public record InveToryClientTooltipComponent(int value) implements ClientTooltipComponent {
    @Override
    public int getHeight() {
        return 12 * (1 + Mth.floor(Mth.abs(value - 1) / 9f));
    }

    @Override
    public int getWidth(Font font) {
        return 12 * Math.min(Mth.abs(value), 9) ;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        ResourceLocation sprite = value < 0 ? InveTory.DISABLED_SLOT_LOCATION_SPRITE : InveTory.ENABLED_SLOT_LOCATION_SPRITE;
        for (int i = 0; i < Mth.abs(value); i++) {
            guiGraphics.blitSprite(sprite, x + 12 * (i % 9), y + 12 * Mth.floor(i / 9f), 10, 10);
        }
    }

    @Override
    public void renderText(Font font, int mouseX, int mouseY, Matrix4f matrix, MultiBufferSource.BufferSource bufferSource) {

    }
}

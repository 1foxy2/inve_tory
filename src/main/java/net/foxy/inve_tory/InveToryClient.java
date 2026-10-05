package net.foxy.inve_tory;

import com.mojang.logging.LogUtils;
import net.foxy.inve_tory.config.InveToryClientConfig;
import net.minecraft.client.gui.components.WidgetSprites;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(value = InveToryClient.MODID, dist = Dist.CLIENT)
public class InveToryClient {
    public static final String MODID = "inve_tory";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final WidgetSprites RESET_BUTTON_SPRITES = new WidgetSprites(
            InveTory.rl("slot_reset"), InveTory.rl("slot_reset_highlighted")
    );

    public InveToryClient(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, InveToryClientConfig.CONFIG_SPEC);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}

package net.foxy.inve_tory.event;

import com.mojang.datafixers.util.Either;
import net.foxy.inve_tory.InveTory;
import net.foxy.inve_tory.InveToryClientTooltipComponent;
import net.foxy.inve_tory.InveToryTooltipComponent;
import net.foxy.inve_tory.config.InveToryClientConfig;
import net.foxy.inve_tory.data.SlotData;
import net.foxy.inve_tory.network.c2s.SyncPresetPayload;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

@EventBusSubscriber(Dist.CLIENT)
public class InvetoryClientEvents {
    @SubscribeEvent
    public static void addTooltips(RenderTooltipEvent.GatherComponents event) {
        int slots = InveTory.getUnlockedSlotsForItem(event.getItemStack());
        if (slots != 0) {
            event.getTooltipElements().add(Either.right(new InveToryTooltipComponent(slots)));
        }
    }

    @SubscribeEvent
    public static void registerTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(InveToryTooltipComponent.class, c -> new InveToryClientTooltipComponent(c.value()));
    }

    @SubscribeEvent // on the mod event bus
    public static void registerDataMapTypes(RegisterDataMapTypesEvent event) {
        event.register(SlotData.DATA_MAP);
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(ClientPlayerNetworkEvent.LoggingIn event) {
        PacketDistributor.sendToServer(SyncPresetPayload.create());
    }

    @SubscribeEvent
    public static void onReloadConfig(ModConfigEvent.Reloading event) {
        if (Minecraft.getInstance().getConnection() != null) {
            PacketDistributor.sendToServer(SyncPresetPayload.create());
        }
    }
}

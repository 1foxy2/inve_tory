package net.foxy.inve_tory.network;

import net.foxy.inve_tory.network.c2s.SyncPresetPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber
public class NetworkHandler {
    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1")
                // to server
                .playToServer(
                        SyncPresetPayload.TYPE,
                        SyncPresetPayload.STREAM_CODEC,
                        SyncPresetPayload::handle
                );
    }
}

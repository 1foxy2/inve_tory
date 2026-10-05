package net.foxy.inve_tory.datagen;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber
public class ITDataProviders {
    @SubscribeEvent
    public static void registerProviders(GatherDataEvent event) {
        event.addProvider(new SlotDataMapProvider(event.getGenerator().getPackOutput(), event.getLookupProvider()));
    }
}

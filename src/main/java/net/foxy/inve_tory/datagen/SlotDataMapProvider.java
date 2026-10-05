package net.foxy.inve_tory.datagen;

import net.foxy.inve_tory.data.SlotData;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.concurrent.CompletableFuture;

public class SlotDataMapProvider extends DataMapProvider {
    protected SlotDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        builder(SlotData.DATA_MAP).add(Items.ELYTRA.builtInRegistryHolder(), new SlotData(-4), false);
    }
}

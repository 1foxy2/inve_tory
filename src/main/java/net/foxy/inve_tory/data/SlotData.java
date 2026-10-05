package net.foxy.inve_tory.data;

import com.mojang.serialization.Codec;
import net.foxy.inve_tory.InveTory;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

public record SlotData(int slots) {
    public static final Codec<SlotData> CODEC = Codec.INT.xmap(SlotData::new, SlotData::slots);

    public static final DataMapType<Item, SlotData> DATA_MAP = DataMapType.builder(
            // The ID of the data map. Data map files for this data map will be located at
            // <yourmodid>:examplemod/data_maps/item/example_data.json.
            ResourceLocation.fromNamespaceAndPath(InveTory.MODID, "slots"),
            Registries.ITEM,
            SlotData.CODEC
    ).synced(CODEC, true).build();
}

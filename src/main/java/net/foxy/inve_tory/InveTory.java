package net.foxy.inve_tory;

import net.foxy.inve_tory.base.ITAttachments;
import net.foxy.inve_tory.config.InveToryConfig;
import net.foxy.inve_tory.config.InveToryServerConfig;
import net.foxy.inve_tory.data.SlotData;
import net.foxy.inve_tory.event.InveToryCuriosEvents;
import net.foxy.inve_tory.event.InvetoryEvents;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.fml.ModList;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

import java.lang.reflect.Type;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(InveTory.MODID)
public class InveTory {
    public static final String MODID = "inve_tory";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final ResourceLocation INV_DISABLED_SLOT_LOCATION_SPRITE = ResourceLocation.withDefaultNamespace("container/crafter/disabled_slot");
    public static final ResourceLocation DISABLED_SLOT_LOCATION_SPRITE = rl("disabled_slot");
    public static final ResourceLocation ENABLED_SLOT_LOCATION_SPRITE = rl("enabled_slot");

    public InveTory(IEventBus modEventBus, ModContainer modContainer) {
        ITAttachments.ATTACHMENTS.register(modEventBus);
        if (ModList.get().isLoaded("curios")) {
            NeoForge.EVENT_BUS.register(InveToryCuriosEvents.class);
        }
        modContainer.registerConfig(ModConfig.Type.COMMON, InveToryConfig.CONFIG_SPEC);
        modContainer.registerConfig(ModConfig.Type.SERVER, InveToryServerConfig.CONFIG_SPEC);
    }

    public static ResourceLocation rl(String id) {
        return ResourceLocation.fromNamespaceAndPath(MODID, id);
    }

    public static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> type(String id) {
        return new CustomPacketPayload.Type<>(rl(id));
    }

    public static int getUnlockedSlots(Player player) {
        return Math.max(player.getData(ITAttachments.UNLOCKED_SLOTS), 0);
    }

    public static void handleItemChange(ServerPlayer player, ItemStack from, ItemStack to) {
        int slots = player.getData(ITAttachments.UNLOCKED_SLOTS);
        int oldSlots = slots;
        slots -= InveTory.getUnlockedSlotsForItem(from);
        slots += InveTory.getUnlockedSlotsForItem(to);

        player.setData(ITAttachments.UNLOCKED_SLOTS, slots);

        if (oldSlots != slots && player.tickCount > 20) {
            player.connection.send(new ClientboundSoundEntityPacket(SoundEvents.UI_BUTTON_CLICK, SoundSource.PLAYERS, player, 0.4f, oldSlots < slots ? 1.0f : 0.75f, 0));
        }
    }

    public static int getUnlockedSlotsForItem(ItemStack itemStack) {
        SlotData slotData = itemStack.getItemHolder().getData(SlotData.DATA_MAP);
        if (slotData != null) {
            return slotData.slots();
        }
        int slots = 0;
        for (ItemAttributeModifiers.Entry entry : itemStack.getAttributeModifiers().modifiers()) {
            if (entry.attribute() == Attributes.ARMOR &&
                    entry.modifier().operation() == AttributeModifier.Operation.ADD_VALUE && (
                            entry.slot() == EquipmentSlotGroup.FEET || entry.slot() == EquipmentSlotGroup.LEGS ||
                            entry.slot() == EquipmentSlotGroup.CHEST || entry.slot() == EquipmentSlotGroup.HEAD
                    )
            ) {
                slots += Mth.ceil(entry.modifier().amount() * InveToryServerConfig.CONFIG.slots_per_armor_attribute.get());
            }
        }
        return slots;
    }
}

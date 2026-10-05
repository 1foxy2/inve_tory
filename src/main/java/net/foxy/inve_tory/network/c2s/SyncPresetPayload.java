package net.foxy.inve_tory.network.c2s;

import io.netty.buffer.ByteBuf;
import net.foxy.inve_tory.InveTory;
import net.foxy.inve_tory.base.ITAttachments;
import net.foxy.inve_tory.config.InveToryClientConfig;
import net.foxy.inve_tory.config.InveToryConfig;
import net.foxy.inve_tory.config.SlotPreset;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public record SyncPresetPayload(List<Integer> preset, boolean custom) implements CustomPacketPayload {
    public static final Type<SyncPresetPayload> TYPE = InveTory.type("sync_preset");
    public static final StreamCodec<ByteBuf, SyncPresetPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT.apply(ByteBufCodecs.list()).map(list -> {
                List<Integer> integers = new ArrayList<>();
                for (Integer integer : list) {
                    int clamped = Mth.clamp(integer, 9, 35);
                    if (!integers.contains(clamped)) {
                        integers.add(clamped);
                    }
                }
                return integers;
            }, Function.identity()),
            SyncPresetPayload::preset,
            ByteBufCodecs.BOOL,
            SyncPresetPayload::custom,
            SyncPresetPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        if (InveToryConfig.CONFIG.force_preset.get()) {
            return;
        }
        Player player = ctx.player();
        List<Integer> oldPreset = player.getData(ITAttachments.SLOTS_PRESET);
        player.setData(ITAttachments.SLOTS_PRESET, preset());
        Inventory inventory = ctx.player().getInventory();
        int unlocked = Math.min(InveTory.getUnlockedSlots(player), oldPreset.size());
        for (int i = 0; i < unlocked; i++) {
            int index = oldPreset.get(i);
            if (inventory.inve_tory$isDisabled(index)) {
                ItemStack stack = inventory.getItem(index);
                if (stack.isEmpty()) {
                    continue;
                }
                if (i < preset.size()) {
                    if (inventory.add(preset.get(i), stack)) {
                        continue;
                    }
                }

                if (custom) {
                    int empty = inventory.getFreeSlot();
                    if (empty != -1) {
                        inventory.add(empty, stack);
                    }
                } else {
                    inventory.placeItemBackInInventory(stack);
                }
            }
        }

        if (custom && preset.size() > oldPreset.size()) {
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                if (inventory.inve_tory$isDisabled(i)) {
                    ItemStack stack = inventory.getItem(i);
                    if (!stack.isEmpty()) {
                        int empty = inventory.getFreeSlot();
                        if (empty == -1 || !inventory.add(empty, stack)) {
                            break;
                        }
                    }
                }
            }
        }
    }

    public static SyncPresetPayload create() {
        return new SyncPresetPayload(
                InveToryClientConfig.CONFIG.preset.get().getPreset(),
                InveToryClientConfig.CONFIG.preset.get() == SlotPreset.CUSTOM
        );
    }
}

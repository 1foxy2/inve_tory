package net.foxy.inve_tory.base;

import com.mojang.serialization.Codec;
import net.foxy.inve_tory.InveTory;
import net.foxy.inve_tory.config.InveToryConfig;
import net.foxy.inve_tory.config.SlotPreset;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;

public class ITAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, InveTory.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> UNLOCKED_SLOTS = ATTACHMENTS
            .register("unlocked_slots", () -> AttachmentType.builder(InveToryConfig.CONFIG.initial_slots).sync(ByteBufCodecs.INT).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<List<Integer>>> SLOTS_PRESET = ATTACHMENTS
            .register("slots_preset", () -> AttachmentType.builder(() -> InveToryConfig.CONFIG.force_preset_type.get().getServerPreset())
                    .serialize(Codec.INT.listOf()).copyOnDeath()
                    .sync(ByteBufCodecs.INT.apply(ByteBufCodecs.list(100))).build());
}

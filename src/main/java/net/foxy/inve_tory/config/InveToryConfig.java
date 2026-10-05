package net.foxy.inve_tory.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class InveToryConfig {

    public static final InveToryConfig CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;

    static {
        Pair<InveToryConfig, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(InveToryConfig::new);

        //Store the resulting values
        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }

    public final ModConfigSpec.IntValue initial_slots;
    public final ModConfigSpec.BooleanValue force_preset;
    public final ModConfigSpec.EnumValue<SlotPreset> force_preset_type;
    public final ModConfigSpec.ConfigValue<List<? extends Integer>> custom_force_preset;

    private InveToryConfig(ModConfigSpec.Builder builder) {
        initial_slots = builder.worldRestart().comment("Number of initial unlocked slots").defineInRange("initial_slots", 0, 0, 35);
        force_preset = builder.worldRestart()
                .comment("if enabled will use server's preset instead of client's one")
                .define("force_preset", false);
        force_preset_type = builder.worldRestart()
                .comment("defines in which order will the slot open")
                .defineEnum("force_preset_type", SlotPreset.HORIZONTAL);
        custom_force_preset = builder.worldRestart()
                .comment("if custom preset is selected, this will be the order of slot unlocking")
                .defineListAllowEmpty("force_custom_preset", ArrayList::new, () -> 0,
                o -> o instanceof Integer integer && 8 < integer && integer < 36);

    }
}

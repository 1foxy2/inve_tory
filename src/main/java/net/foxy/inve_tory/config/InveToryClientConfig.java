package net.foxy.inve_tory.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class InveToryClientConfig {

    public static final InveToryClientConfig CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;

    //CONFIG and CONFIG_SPEC are both built from the same builder, so we use a static block to seperate the properties
    static {
        Pair<InveToryClientConfig, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(InveToryClientConfig::new);

        //Store the resulting values
        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }

    public final ModConfigSpec.BooleanValue add_toggle_button;
    public final ModConfigSpec.ConfigValue<SlotPreset> preset;
    public final ModConfigSpec.ConfigValue<List<? extends Integer>> custom_preset;

    private InveToryClientConfig(ModConfigSpec.Builder builder) {
        add_toggle_button = builder
                .comment("if custom preset is selected, will show button to toggle between enabling/disabling slot mode in inventory")
                .define("add_toggle_button", true);
        preset = builder
                .comment("defines in which order will the slot open")
                .defineEnum("preset", SlotPreset.CUSTOM);
        custom_preset = builder
                .comment("if custom preset is selected, this will be the order of slot unlocking")
                .defineListAllowEmpty("custom_preset", ArrayList::new, () -> 0,
                o -> o instanceof Integer integer && 8 < integer && integer < 36);
    }
}

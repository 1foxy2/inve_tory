package net.foxy.inve_tory.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class InveToryServerConfig {

    public static final InveToryServerConfig CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;

    static {
        Pair<InveToryServerConfig, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(InveToryServerConfig::new);

        //Store the resulting values
        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }

    public final ModConfigSpec.DoubleValue slots_per_armor_attribute;

    private InveToryServerConfig(ModConfigSpec.Builder builder) {
        slots_per_armor_attribute = builder.worldRestart()
                .comment("Amount of slots given per armor attribute on the item")
                .defineInRange("slots_per_armor_attribute", 1.5, -40, 40);

    }
}

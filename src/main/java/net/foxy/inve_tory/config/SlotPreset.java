package net.foxy.inve_tory.config;

import net.minecraft.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum SlotPreset {
    HORIZONTAL {
        final List<Integer> preset = Util.make(new ArrayList<>(), list -> {
            for (int i = 9; i < 36; i++) {
                list.add(i);
            }
        });

        @Override
        public List<Integer> getPreset() {
            return preset;
        }
    },
    HORIZONTAL_REVERSED {
        final List<Integer> preset = Util.make(new ArrayList<>(), list -> {
            for (int i = 35; i > 8; i--) {
                list.add(i);
            }
        });

        @Override
        public List<Integer> getPreset() {
            return preset;
        }
    },
    VERTICAL {
        final List<Integer> preset = Util.make(new ArrayList<>(), list -> {
            for (int i = 0; i < 9; i++) {
                list.add(9 + i);
                list.add(18 + i);
                list.add(27 + i);
            }
        });

        @Override
        public List<Integer> getPreset() {
            return preset;
        }
    },
    VERTICAL_REVERSED {
        final List<Integer> preset = Util.make(new ArrayList<>(), list -> {
            for (int i = 0; i < 9; i++) {
                list.add(35 - i);
                list.add(26 - i);
                list.add(17 - i);
            }
        });

        @Override
        public List<Integer> getPreset() {
            return preset;
        }
    },
    CUSTOM {
        @Override
        public List<Integer> getPreset() {
            return (List<Integer>) InveToryClientConfig.CONFIG.custom_preset.get();
        }

        @Override
        public List<Integer> getServerPreset() {
            return (List<Integer>) InveToryConfig.CONFIG.custom_force_preset.get();
        }
    }
    ;

    public List<Integer> getPreset() {
        return Collections.emptyList();
    }

    public List<Integer> getServerPreset() {
        return getPreset();
    }
}

package net.foxy.inve_tory.event;

import net.foxy.inve_tory.InveTory;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

public class InveToryCuriosEvents {
    @SubscribeEvent
    private static void onCuriosChanged(CurioChangeEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            InveTory.handleItemChange(player, event.getFrom(), event.getTo());
        }
    }
}

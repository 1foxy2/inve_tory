package net.foxy.inve_tory.event;

import net.foxy.inve_tory.InveTory;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;

@EventBusSubscriber
public class InvetoryEvents {
    @SubscribeEvent
    public static void onEquipmentChanged(LivingEquipmentChangeEvent event) {
        if (event.getSlot().isArmor() && event.getEntity() instanceof ServerPlayer player) {
            InveTory.handleItemChange(player, event.getFrom(), event.getTo());
        }
    }
}

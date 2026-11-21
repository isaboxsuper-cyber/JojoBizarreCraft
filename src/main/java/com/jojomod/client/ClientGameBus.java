package com.jojomod.client;

import com.jojomod.JojoMod;
import com.jojomod.network.AbilityPayload;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = JojoMod.MOD_ID, value = Dist.CLIENT)
public class ClientGameBus {
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        while (ClientModBus.TOGGLE_STAND.consumeClick()) {
            PacketDistributor.sendToServer(new AbilityPayload(0));
        }
        while (ClientModBus.ABILITY_1.consumeClick()) {
            PacketDistributor.sendToServer(new AbilityPayload(1));
        }
        while (ClientModBus.ABILITY_2.consumeClick()) {
            PacketDistributor.sendToServer(new AbilityPayload(2));
        }
    }
}

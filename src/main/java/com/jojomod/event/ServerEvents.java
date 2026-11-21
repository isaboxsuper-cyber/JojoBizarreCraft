package com.jojomod.event;

import com.jojomod.JojoMod;
import com.jojomod.Stands;
import com.jojomod.util.Cooldowns;
import com.jojomod.util.Scheduler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = JojoMod.MOD_ID)
public class ServerEvents {
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        Scheduler.tick();
    }

    @SubscribeEvent
    public static void onStopping(ServerStoppingEvent event) {
        Scheduler.clear();
        Cooldowns.clear();
        Stands.clearState();
    }
}

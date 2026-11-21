package com.jojomod.client;

import com.jojomod.JojoMod;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = JojoMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModBus {
    public static final String CATEGORY = "key.categories.jojomod";

    public static final KeyMapping TOGGLE_STAND = new KeyMapping("key.jojomod.toggle_stand",
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Z, CATEGORY);
    public static final KeyMapping ABILITY_1 = new KeyMapping("key.jojomod.ability1",
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, CATEGORY);
    public static final KeyMapping ABILITY_2 = new KeyMapping("key.jojomod.ability2",
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_X, CATEGORY);

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_STAND);
        event.register(ABILITY_1);
        event.register(ABILITY_2);
    }
}

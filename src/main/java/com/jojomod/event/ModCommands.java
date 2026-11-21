package com.jojomod.event;

import com.jojomod.JojoMod;
import com.jojomod.ModAttachments;
import com.jojomod.Stands;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/** Comandos de teste (precisam de OP): /jojo stand <id>, /jojo hamon, /jojo vampire, /jojo reset */
@EventBusSubscriber(modid = JojoMod.MOD_ID)
public class ModCommands {
    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("jojo")
                .requires(s -> s.hasPermission(2))
                .then(Commands.literal("stand")
                        .then(Commands.argument("id", StringArgumentType.word())
                                .suggests((ctx, b) -> SharedSuggestionProvider.suggest(Stands.ALL.keySet(), b))
                                .executes(ctx -> setStand(ctx.getSource(), StringArgumentType.getString(ctx, "id")))))
                .then(Commands.literal("hamon").executes(ctx -> setHamon(ctx.getSource())))
                .then(Commands.literal("vampire").executes(ctx -> setVampire(ctx.getSource())))
                .then(Commands.literal("reset").executes(ctx -> reset(ctx.getSource()))));
    }

    private static int setStand(CommandSourceStack src, String id) throws CommandSyntaxException {
        ServerPlayer p = src.getPlayerOrException();
        if (Stands.get(id) == null) {
            src.sendFailure(Component.literal("Stand desconhecido: " + id));
            return 0;
        }
        p.setData(ModAttachments.STAND, id);
        p.setData(ModAttachments.STAND_ACTIVE, false);
        src.sendSuccess(() -> Component.literal("Stand definido: " + Stands.get(id).name()), true);
        return 1;
    }

    private static int setHamon(CommandSourceStack src) throws CommandSyntaxException {
        ServerPlayer p = src.getPlayerOrException();
        p.setData(ModAttachments.VAMPIRE, false);
        p.setData(ModAttachments.HAMON, true);
        p.setData(ModAttachments.HAMON_ENERGY, 100);
        src.sendSuccess(() -> Component.literal("Você agora é usuário de Hamon."), true);
        return 1;
    }

    private static int setVampire(CommandSourceStack src) throws CommandSyntaxException {
        ServerPlayer p = src.getPlayerOrException();
        p.setData(ModAttachments.HAMON, false);
        p.setData(ModAttachments.VAMPIRE, true);
        src.sendSuccess(() -> Component.literal("Você agora é um vampiro."), true);
        return 1;
    }

    private static int reset(CommandSourceStack src) throws CommandSyntaxException {
        ServerPlayer p = src.getPlayerOrException();
        p.setData(ModAttachments.STAND, "");
        p.setData(ModAttachments.STAND_ACTIVE, false);
        p.setData(ModAttachments.HAMON, false);
        p.setData(ModAttachments.HAMON_ENERGY, 0);
        p.setData(ModAttachments.VAMPIRE, false);
        src.sendSuccess(() -> Component.literal("Poderes JoJo resetados."), true);
        return 1;
    }
}

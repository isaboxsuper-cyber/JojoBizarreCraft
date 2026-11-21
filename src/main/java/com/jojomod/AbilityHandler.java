package com.jojomod;

import com.jojomod.util.Combat;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

/** Recebe as teclas (0 = invocar stand, 1 = habilidade 1, 2 = habilidade 2). */
public final class AbilityHandler {
    private AbilityHandler() {}

    public static void handle(ServerPlayer p, int action) {
        if (action == 0) {
            toggleStand(p);
        } else if (action == 1 || action == 2) {
            ability(p, action);
        }
    }

    private static void toggleStand(ServerPlayer p) {
        String id = p.getData(ModAttachments.STAND);
        if (id.isEmpty() || Stands.get(id) == null) {
            Combat.msg(p, "§cVocê não tem um Stand. Use uma Flecha de Stand!");
            return;
        }
        boolean now = !p.getData(ModAttachments.STAND_ACTIVE);
        p.setData(ModAttachments.STAND_ACTIVE, now);
        ServerLevel lvl = (ServerLevel) p.level();
        lvl.playSound(null, p.blockPosition(), now ? SoundEvents.BEACON_ACTIVATE : SoundEvents.BEACON_DEACTIVATE,
                SoundSource.PLAYERS, 0.8F, 1.5F);
        Combat.msg(p, now ? "§d" + Stands.get(id).name() + " invocado!" : "§7Stand recolhido.");
    }

    private static void ability(ServerPlayer p, int n) {
        String id = p.getData(ModAttachments.STAND);
        boolean active = p.getData(ModAttachments.STAND_ACTIVE);

        if (active && Stands.get(id) != null) {
            Stands.use(p, Stands.get(id), n);
        } else if (p.getData(ModAttachments.HAMON)) {
            HamonAbilities.use(p, n);
        } else if (p.getData(ModAttachments.VAMPIRE)) {
            VampireAbilities.use(p, n);
        } else if (!id.isEmpty()) {
            Combat.msg(p, "§7Invoque seu Stand com Z.");
        } else {
            Combat.msg(p, "§7Você não possui nenhum poder ainda.");
        }
    }
}

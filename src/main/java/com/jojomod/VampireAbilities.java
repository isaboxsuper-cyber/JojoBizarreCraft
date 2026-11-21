package com.jojomod;

import com.jojomod.util.Combat;
import com.jojomod.util.Cooldowns;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;

public final class VampireAbilities {
    private VampireAbilities() {}

    public static void use(ServerPlayer p, int n) {
        ServerLevel lvl = (ServerLevel) p.level();
        if (n == 1) {
            LivingEntity t = Combat.target(p, 4);
            if (t == null) {
                Combat.msg(p, "§cNenhuma vítima ao alcance.");
                return;
            }
            if (!Cooldowns.ready(p, "vamp1", 120)) return;
            Combat.hit(p, t, 6.0F);
            p.heal(6.0F);
            p.getFoodData().eat(4, 0.5F);
            lvl.sendParticles(ParticleTypes.DAMAGE_INDICATOR, t.getX(), t.getY() + 1.2, t.getZ(), 12, 0.3, 0.4, 0.3, 0.1);
            lvl.playSound(null, p.blockPosition(), SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 1.0F, 0.6F);
            Combat.msg(p, "§4Você drena o sangue de sua vítima...");
        } else {
            if (!Cooldowns.ready(p, "vamp2", 280)) return;
            Combat.line(p, ParticleTypes.SOUL_FIRE_FLAME, 24);
            LivingEntity t = Combat.target(p, 24);
            if (t != null) {
                Combat.hit(p, t, 14.0F);
                lvl.sendParticles(ParticleTypes.CRIT, t.getX(), t.getY() + 1, t.getZ(), 20, 0.3, 0.5, 0.3, 0.3);
            }
            lvl.playSound(null, p.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 0.5F, 1.8F);
            Combat.msg(p, "§4§lSPACE RIPPER STINGY EYES!");
        }
    }
}

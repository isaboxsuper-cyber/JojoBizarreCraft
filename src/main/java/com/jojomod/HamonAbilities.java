package com.jojomod;

import com.jojomod.util.Combat;
import com.jojomod.util.Cooldowns;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class HamonAbilities {
    private HamonAbilities() {}

    public static String bar(int energy) {
        int filled = energy / 10;
        return "§6Hamon §e" + "█".repeat(filled) + "§8" + "▒".repeat(10 - filled) + " §f" + energy + "/100";
    }

    public static void use(ServerPlayer p, int n) {
        int energy = p.getData(ModAttachments.HAMON_ENERGY);
        ServerLevel lvl = (ServerLevel) p.level();

        if (n == 1) {
            int cost = 25;
            if (energy < cost && !p.isCreative()) {
                Combat.msg(p, "§cSem energia! Segure Shift para respirar. " + bar(energy));
                return;
            }
            LivingEntity t = Combat.target(p, 5);
            if (t == null) {
                Combat.msg(p, "§cNenhum alvo ao alcance.");
                return;
            }
            if (!Cooldowns.ready(p, "hamon1", 20)) return;
            if (!p.isCreative()) p.setData(ModAttachments.HAMON_ENERGY, energy - cost);

            boolean evil = Combat.isEvil(t);
            Combat.hit(p, t, evil ? 16.0F : 7.0F);
            if (evil) t.setRemainingFireTicks(120);
            t.knockback(0.8, p.getX() - t.getX(), p.getZ() - t.getZ());
            lvl.sendParticles(ParticleTypes.ELECTRIC_SPARK, t.getX(), t.getY() + 1, t.getZ(), 30, 0.4, 0.6, 0.4, 0.3);
            lvl.sendParticles(ParticleTypes.GLOW, t.getX(), t.getY() + 1, t.getZ(), 15, 0.4, 0.6, 0.4, 0.1);
            lvl.playSound(null, p.blockPosition(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 0.4F, 1.6F);
            Combat.msg(p, "§6§lSUNLIGHT YELLOW OVERDRIVE!");
        } else {
            int cost = 50;
            if (energy < cost && !p.isCreative()) {
                Combat.msg(p, "§cSem energia! Segure Shift para respirar. " + bar(energy));
                return;
            }
            if (!Cooldowns.ready(p, "hamon2", 200)) return;
            if (!p.isCreative()) p.setData(ModAttachments.HAMON_ENERGY, energy - cost);

            for (LivingEntity e : Combat.around(p, 7)) {
                if (Combat.isEvil(e)) {
                    Combat.hit(p, e, 14.0F);
                    e.setRemainingFireTicks(160);
                } else if (e instanceof Player ally) {
                    ally.heal(4.0F);
                }
            }
            p.heal(4.0F);
            p.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 80, 1));
            for (int i = 0; i < 48; i++) {
                double a = i * Math.PI * 2 / 48;
                lvl.sendParticles(ParticleTypes.END_ROD, p.getX() + Math.cos(a) * 6.5, p.getY() + 0.5, p.getZ() + Math.sin(a) * 6.5, 2, 0.1, 0.6, 0.1, 0.02);
            }
            lvl.playSound(null, p.blockPosition(), SoundEvents.BEACON_POWER_SELECT, SoundSource.PLAYERS, 1.5F, 1.4F);
            Combat.msg(p, "§e§lAURA DE SOL!");
        }
    }
}

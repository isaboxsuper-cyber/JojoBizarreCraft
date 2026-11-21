package com.jojomod.util;

import com.jojomod.ModAttachments;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class Combat {
    /** true enquanto uma habilidade causa dano (para os passivos nao se acumularem). */
    public static boolean abilityHit = false;

    private Combat() {}

    // ---------- mensagens ----------
    public static void msg(ServerPlayer p, String text) {
        p.displayClientMessage(Component.literal(text), true);
    }

    public static void chat(ServerPlayer p, String text) {
        p.sendSystemMessage(Component.literal(text));
    }

    // ---------- alvos ----------
    public static LivingEntity target(ServerPlayer p, double range) {
        Vec3 eye = p.getEyePosition();
        Vec3 look = p.getLookAngle();
        Vec3 end = eye.add(look.scale(range));
        AABB area = p.getBoundingBox().expandTowards(look.scale(range)).inflate(1.5);
        LivingEntity best = null;
        double bestDist = Double.MAX_VALUE;
        for (LivingEntity e : p.level().getEntitiesOfClass(LivingEntity.class, area,
                x -> x != p && x.isAlive() && !x.isSpectator())) {
            Optional<Vec3> hit = e.getBoundingBox().inflate(0.35).clip(eye, end);
            if (hit.isPresent()) {
                double d = eye.distanceToSqr(hit.get());
                if (d < bestDist) {
                    bestDist = d;
                    best = e;
                }
            }
        }
        return best;
    }

    public static List<LivingEntity> cone(ServerPlayer p, double range, double minDot) {
        Vec3 eye = p.getEyePosition();
        Vec3 look = p.getLookAngle();
        return p.level().getEntitiesOfClass(LivingEntity.class, p.getBoundingBox().inflate(range), e -> {
            if (e == p || !e.isAlive() || e.isSpectator()) return false;
            Vec3 to = e.getBoundingBox().getCenter().subtract(eye);
            double d = to.length();
            return d > 0.01 && d <= range && to.scale(1.0 / d).dot(look) >= minDot;
        });
    }

    public static List<LivingEntity> around(ServerPlayer p, double radius) {
        return p.level().getEntitiesOfClass(LivingEntity.class, p.getBoundingBox().inflate(radius),
                e -> e != p && e.isAlive() && !e.isSpectator() && e.distanceToSqr(p) <= radius * radius);
    }

    public static boolean isEvil(LivingEntity e) {
        if (e.getType().is(EntityTypeTags.UNDEAD)) return true;
        return e instanceof Player pl && pl.getData(ModAttachments.VAMPIRE);
    }

    // ---------- dano ----------
    public static void hit(ServerPlayer p, LivingEntity t, float dmg) {
        abilityHit = true;
        try {
            t.hurt(p.damageSources().playerAttack(p), dmg);
        } finally {
            abilityHit = false;
        }
    }

    public static void eff(LivingEntity e, Holder<net.minecraft.world.effect.MobEffect> eff, int ticks, int amp) {
        e.addEffect(new MobEffectInstance(eff, ticks, amp, false, true));
    }

    /** Rajada de socos (ORA ORA / MUDA MUDA). */
    public static void barrage(ServerPlayer p, int hits, float dmg, double range, String shout) {
        msg(p, shout);
        for (int i = 0; i < hits; i++) {
            Scheduler.later(1 + i * 2, () -> {
                if (!p.isAlive()) return;
                ServerLevel lvl = (ServerLevel) p.level();
                Vec3 look = p.getLookAngle();
                Vec3 front = p.getEyePosition().add(look.scale(1.8));
                double ox = (lvl.random.nextDouble() - 0.5) * 0.9;
                double oy = (lvl.random.nextDouble() - 0.5) * 0.9;
                double oz = (lvl.random.nextDouble() - 0.5) * 0.9;
                lvl.sendParticles(ParticleTypes.CRIT, front.x + ox, front.y + oy, front.z + oz, 4, 0.2, 0.2, 0.2, 0.2);
                lvl.playSound(null, p.blockPosition(), SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 0.8F, 0.8F + lvl.random.nextFloat() * 0.4F);
                LivingEntity t = target(p, range);
                if (t != null) {
                    t.invulnerableTime = 0;
                    hit(p, t, dmg);
                }
            });
        }
    }

    private record FrozenProjectile(Projectile proj, Vec3 vel, boolean noGravity) {}

    /** Para o tempo ao redor do jogador (mobs, projeteis e outros jogadores). */
    public static void timeStop(ServerPlayer p, int ticks) {
        ServerLevel lvl = (ServerLevel) p.level();
        AABB box = p.getBoundingBox().inflate(48);

        List<Mob> mobs = lvl.getEntitiesOfClass(Mob.class, box, m -> !m.isNoAi());
        for (Mob m : mobs) m.setNoAi(true);

        List<FrozenProjectile> projs = new ArrayList<>();
        for (Projectile pr : lvl.getEntitiesOfClass(Projectile.class, box)) {
            projs.add(new FrozenProjectile(pr, pr.getDeltaMovement(), pr.isNoGravity()));
            pr.setDeltaMovement(Vec3.ZERO);
            pr.setNoGravity(true);
        }

        for (ServerPlayer o : lvl.players()) {
            if (o != p && o.distanceToSqr(p) < 48 * 48) {
                o.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, ticks, 9, false, false));
                o.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, ticks, 4, false, false));
                o.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, ticks, 4, false, false));
                chat(o, "§8§oO tempo parou...");
            }
        }

        lvl.playSound(null, p.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 2.0F, 0.5F);
        lvl.sendParticles(ParticleTypes.END_ROD, p.getX(), p.getY() + 1, p.getZ(), 80, 3, 1.5, 3, 0.05);

        Scheduler.later(ticks, () -> {
            for (Mob m : mobs) if (m.isAlive()) m.setNoAi(false);
            for (FrozenProjectile fp : projs) {
                if (fp.proj().isAlive()) {
                    fp.proj().setNoGravity(fp.noGravity());
                    fp.proj().setDeltaMovement(fp.vel());
                    fp.proj().hurtMarked = true;
                }
            }
            lvl.playSound(null, p.blockPosition(), SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 2.0F, 0.5F);
            if (p.isAlive()) msg(p, "§bO tempo voltou a fluir.");
        });
    }

    /** Linha de particulas na direcao do olhar. */
    public static void line(ServerPlayer p, ParticleOptions particle, double length) {
        ServerLevel lvl = (ServerLevel) p.level();
        Vec3 eye = p.getEyePosition();
        Vec3 look = p.getLookAngle();
        for (double d = 0.8; d <= length; d += 0.5) {
            Vec3 pt = eye.add(look.scale(d));
            lvl.sendParticles(particle, pt.x, pt.y - 0.15, pt.z, 1, 0, 0, 0, 0);
        }
    }
}

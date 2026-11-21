package com.jojomod;

import com.jojomod.util.Combat;
import com.jojomod.util.Cooldowns;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** Definicao e habilidades de todos os Stands. Para adicionar um novo: registre em static{} e crie o case. */
public final class Stands {
    public record StandDef(String id, String name, String user, String ab1, String ab2, int weight) {}

    public static final Map<String, StandDef> ALL = new LinkedHashMap<>();
    private static final Map<UUID, Set<UUID>> BOMBS = new HashMap<>();

    static {
        add(new StandDef("star_platinum", "Star Platinum", "Jotaro Kujo", "ORA ORA (rajada)", "Za Warudo (para o tempo 5s)", 10));
        add(new StandDef("the_world", "The World", "DIO", "MUDA MUDA (rajada)", "ZA WARUDO (para o tempo 8s)", 5));
        add(new StandDef("crazy_diamond", "Crazy Diamond", "Josuke Higashikata", "Restaurar (cura/conserta)", "DORARARA (rajada)", 10));
        add(new StandDef("killer_queen", "Killer Queen", "Yoshikage Kira", "Primeira Bomba (marcar)", "Detonar bombas", 8));
        add(new StandDef("gold_experience", "Gold Experience", "Giorno Giovanna", "Vida (cresce plantas/cura)", "Dor da Vida (debuffs)", 8));
        add(new StandDef("magicians_red", "Magician's Red", "Muhammad Avdol", "Crossfire Hurricane (cone de fogo)", "Red Bind (anel de fogo)", 10));
    }

    private Stands() {}

    private static void add(StandDef d) {
        ALL.put(d.id(), d);
    }

    public static StandDef get(String id) {
        return ALL.get(id);
    }

    public static StandDef random(RandomSource r) {
        int total = 0;
        for (StandDef d : ALL.values()) total += d.weight();
        int roll = r.nextInt(total);
        for (StandDef d : ALL.values()) {
            roll -= d.weight();
            if (roll < 0) return d;
        }
        return ALL.values().iterator().next();
    }

    public static void clearState() {
        BOMBS.clear();
    }

    // ---------- passivos (chamado a cada 1s com o stand invocado) ----------
    private static void eff(ServerPlayer p, Holder<MobEffect> e, int amp) {
        p.addEffect(new MobEffectInstance(e, 60, amp, true, false));
    }

    public static void passive(ServerPlayer p, StandDef d) {
        switch (d.id()) {
            case "star_platinum", "the_world" -> {
                eff(p, MobEffects.DAMAGE_BOOST, 1);
                eff(p, MobEffects.MOVEMENT_SPEED, 0);
            }
            case "crazy_diamond" -> eff(p, MobEffects.DAMAGE_BOOST, 0);
            case "gold_experience" -> eff(p, MobEffects.REGENERATION, 0);
            case "magicians_red" -> eff(p, MobEffects.FIRE_RESISTANCE, 0);
            default -> { }
        }
    }

    // ---------- habilidades ----------
    public static void use(ServerPlayer p, StandDef d, int n) {
        switch (d.id()) {
            case "star_platinum" -> starPlatinum(p, n);
            case "the_world" -> theWorld(p, n);
            case "crazy_diamond" -> crazyDiamond(p, n);
            case "killer_queen" -> killerQueen(p, n);
            case "gold_experience" -> goldExperience(p, n);
            case "magicians_red" -> magiciansRed(p, n);
            default -> { }
        }
    }

    private static void starPlatinum(ServerPlayer p, int n) {
        if (n == 1) {
            if (!Cooldowns.ready(p, "sp1", 240)) return;
            Combat.barrage(p, 12, 4.0F, 4.5, "§dORA ORA ORA ORA ORA!");
        } else {
            if (!Cooldowns.ready(p, "sp2", 900)) return;
            Combat.msg(p, "§bStar Platinum: Za Warudo!");
            Combat.timeStop(p, 100);
        }
    }

    private static void theWorld(ServerPlayer p, int n) {
        if (n == 1) {
            if (!Cooldowns.ready(p, "tw1", 240)) return;
            Combat.barrage(p, 14, 4.5F, 4.5, "§eMUDA MUDA MUDA MUDA MUDA!");
        } else {
            if (!Cooldowns.ready(p, "tw2", 1200)) return;
            Combat.msg(p, "§e§lZA WARUDO!");
            Combat.timeStop(p, 160);
        }
    }

    private static void crazyDiamond(ServerPlayer p, int n) {
        if (n == 1) {
            if (!Cooldowns.ready(p, "cd1", 200)) return;
            LivingEntity t = Combat.target(p, 5);
            LivingEntity who = t != null ? t : p;
            who.heal(who.getMaxHealth());
            for (MobEffectInstance i : new ArrayList<>(who.getActiveEffects())) {
                if (i.getEffect().value().getCategory() == MobEffectCategory.HARMFUL) {
                    who.removeEffect(i.getEffect());
                }
            }
            // conserta itens do jogador
            for (ItemStack s : p.getArmorSlots()) if (s.isDamageableItem()) s.setDamageValue(0);
            if (p.getMainHandItem().isDamageableItem()) p.getMainHandItem().setDamageValue(0);
            if (p.getOffhandItem().isDamageableItem()) p.getOffhandItem().setDamageValue(0);
            ((ServerLevel) p.level()).sendParticles(ParticleTypes.HEART, who.getX(), who.getY() + 1.2, who.getZ(), 10, 0.4, 0.5, 0.4, 0.05);
            Combat.msg(p, "§dCrazy Diamond restaurou tudo!");
        } else {
            if (!Cooldowns.ready(p, "cd2", 220)) return;
            Combat.barrage(p, 10, 4.0F, 4.5, "§dDORARARARARARARA!");
        }
    }

    private static void killerQueen(ServerPlayer p, int n) {
        ServerLevel lvl = (ServerLevel) p.level();
        if (n == 1) {
            LivingEntity t = Combat.target(p, 6);
            if (t == null) {
                Combat.msg(p, "§cNenhum alvo para tocar.");
                return;
            }
            Set<UUID> set = BOMBS.computeIfAbsent(p.getUUID(), k -> new HashSet<>());
            if (set.size() >= 5) {
                Combat.msg(p, "§cMáximo de 5 bombas. Detone (X) primeiro.");
                return;
            }
            if (!Cooldowns.ready(p, "kq1", 30)) return;
            set.add(t.getUUID());
            t.addEffect(new MobEffectInstance(MobEffects.GLOWING, 1200, 0, false, false));
            lvl.sendParticles(ParticleTypes.ELECTRIC_SPARK, t.getX(), t.getY() + 1, t.getZ(), 20, 0.4, 0.6, 0.4, 0.1);
            Combat.msg(p, "§cPrimeira Bomba armada! (" + set.size() + "/5)");
        } else {
            Set<UUID> set = BOMBS.remove(p.getUUID());
            if (set == null || set.isEmpty()) {
                Combat.msg(p, "§cNenhuma bomba marcada.");
                return;
            }
            if (!Cooldowns.ready(p, "kq2", 20)) {
                BOMBS.put(p.getUUID(), set);
                return;
            }
            for (UUID id : set) {
                Entity e = lvl.getEntity(id);
                if (e != null && e.isAlive()) {
                    lvl.explode(p, e.getX(), e.getY(0.5), e.getZ(), 3.2F, Level.ExplosionInteraction.NONE);
                    if (e instanceof LivingEntity le) Combat.hit(p, le, 8.0F);
                }
            }
            Combat.msg(p, "§4§l*CLICK*");
        }
    }

    private static void goldExperience(ServerPlayer p, int n) {
        ServerLevel lvl = (ServerLevel) p.level();
        if (n == 1) {
            if (!Cooldowns.ready(p, "ge1", 160)) return;
            BlockPos c = p.blockPosition();
            int grown = 0;
            for (int dx = -6; dx <= 6; dx++) {
                for (int dy = -2; dy <= 2; dy++) {
                    for (int dz = -6; dz <= 6; dz++) {
                        BlockPos pos = c.offset(dx, dy, dz);
                        if (BoneMealItem.growCrop(new ItemStack(Items.BONE_MEAL), lvl, pos)) {
                            grown++;
                            lvl.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 3, 0.3, 0.3, 0.3, 0.0);
                        }
                    }
                }
            }
            p.heal(6.0F);
            p.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1));
            Combat.msg(p, "§aGold Experience deu vida ao redor! (" + grown + " plantas)");
        } else {
            LivingEntity t = Combat.target(p, 4);
            if (t == null) {
                Combat.msg(p, "§cNenhum alvo.");
                return;
            }
            if (!Cooldowns.ready(p, "ge2", 120)) return;
            Combat.hit(p, t, 6.0F);
            t.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1));
            t.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 120, 0));
            lvl.sendParticles(ParticleTypes.HAPPY_VILLAGER, t.getX(), t.getY() + 1, t.getZ(), 15, 0.4, 0.6, 0.4, 0.1);
            Combat.msg(p, "§6Dor da Vida!");
        }
    }

    private static void magiciansRed(ServerPlayer p, int n) {
        ServerLevel lvl = (ServerLevel) p.level();
        if (n == 1) {
            if (!Cooldowns.ready(p, "mr1", 140)) return;
            Vec3 eye = p.getEyePosition();
            Vec3 look = p.getLookAngle();
            for (double d = 1; d <= 10; d += 0.7) {
                Vec3 pt = eye.add(look.scale(d));
                lvl.sendParticles(ParticleTypes.FLAME, pt.x, pt.y - 0.2, pt.z, 6, d * 0.06, d * 0.06, d * 0.06, 0.02);
            }
            for (LivingEntity e : Combat.cone(p, 10, 0.88)) {
                e.setRemainingFireTicks(160);
                Combat.hit(p, e, 4.0F);
            }
            Combat.msg(p, "§6CROSSFIRE HURRICANE!");
        } else {
            if (!Cooldowns.ready(p, "mr2", 300)) return;
            for (LivingEntity e : Combat.around(p, 6)) {
                e.setRemainingFireTicks(100);
                Combat.hit(p, e, 3.0F);
            }
            p.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200, 0));
            for (int i = 0; i < 40; i++) {
                double a = i * Math.PI * 2 / 40;
                lvl.sendParticles(ParticleTypes.FLAME, p.getX() + Math.cos(a) * 5.5, p.getY() + 0.3, p.getZ() + Math.sin(a) * 5.5, 2, 0.1, 0.4, 0.1, 0.02);
            }
            Combat.msg(p, "§cRED BIND!");
        }
    }
}

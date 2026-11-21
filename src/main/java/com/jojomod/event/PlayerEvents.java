package com.jojomod.event;

import com.jojomod.Config;
import com.jojomod.HamonAbilities;
import com.jojomod.JojoMod;
import com.jojomod.ModAttachments;
import com.jojomod.Stands;
import com.jojomod.util.Combat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = JojoMod.MOD_ID)
public class PlayerEvents {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer p)) return;
        ServerLevel lvl = (ServerLevel) p.level();
        int t = p.tickCount;

        String sid = p.getData(ModAttachments.STAND);
        boolean standOut = p.getData(ModAttachments.STAND_ACTIVE);
        Stands.StandDef def = sid.isEmpty() ? null : Stands.get(sid);

        // Stand: passivos + particulas
        if (standOut && def == null) {
            p.setData(ModAttachments.STAND_ACTIVE, false);
        } else if (standOut) {
            if (t % 20 == 0) Stands.passive(p, def);
            if (t % 4 == 0) {
                Vec3 back = p.getLookAngle().multiply(1, 0, 1).normalize().scale(-0.9);
                ParticleOptions part = switch (def.id()) {
                    case "magicians_red" -> ParticleTypes.FLAME;
                    case "gold_experience" -> ParticleTypes.HAPPY_VILLAGER;
                    case "killer_queen" -> ParticleTypes.ELECTRIC_SPARK;
                    case "crazy_diamond" -> ParticleTypes.CHERRY_LEAVES;
                    default -> ParticleTypes.END_ROD;
                };
                lvl.sendParticles(part, p.getX() + back.x, p.getY() + 1.2, p.getZ() + back.z, 3, 0.25, 0.5, 0.25, 0.01);
            }
        }

        // Hamon: respiracao
        if (p.getData(ModAttachments.HAMON)) {
            int en = p.getData(ModAttachments.HAMON_ENERGY);
            if (p.isShiftKeyDown()) {
                if (t % 5 == 0 && en < 100) {
                    en = Math.min(100, en + 2);
                    p.setData(ModAttachments.HAMON_ENERGY, en);
                    lvl.sendParticles(ParticleTypes.GLOW, p.getX(), p.getY() + 1.0, p.getZ(), 2, 0.4, 0.6, 0.4, 0.02);
                }
                if (t % 5 == 0) Combat.msg(p, HamonAbilities.bar(en));
            } else if (t % 40 == 0 && en < 100) {
                p.setData(ModAttachments.HAMON_ENERGY, en + 1);
            }
        }

        // Vampiro
        if (p.getData(ModAttachments.VAMPIRE) && t % 20 == 0) {
            p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60, 1, true, false));
            p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, 0, true, false));
            p.addEffect(new MobEffectInstance(MobEffects.JUMP, 60, 1, true, false));
            p.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 260, 0, true, false));
            p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 0, true, false));

            if (Config.VAMPIRES_BURN.get() && lvl.isDay() && !p.isInWaterRainOrBubble()
                    && lvl.canSeeSky(BlockPos.containing(p.getX(), p.getEyeY(), p.getZ()))) {
                if (p.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
                    p.setRemainingFireTicks(Math.max(p.getRemainingFireTicks(), 100));
                    Combat.msg(p, "§cO sol queima a sua pele! Procure sombra ou use um capacete.");
                } else {
                    p.getItemBySlot(EquipmentSlot.HEAD).hurtAndBreak(1, p, EquipmentSlot.HEAD);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (Combat.abilityHit) return;
        if (!(event.getSource().getEntity() instanceof ServerPlayer attacker)) return;
        if (event.getSource().getDirectEntity() != attacker) return; // so corpo a corpo

        LivingEntity victim = event.getEntity();

        // Hamon: bonus contra mortos-vivos e vampiros
        if (attacker.getData(ModAttachments.HAMON) && Combat.isEvil(victim)) {
            int en = attacker.getData(ModAttachments.HAMON_ENERGY);
            if (en >= 4) {
                attacker.setData(ModAttachments.HAMON_ENERGY, en - 4);
                event.setAmount(event.getAmount() * 1.5F);
                victim.setRemainingFireTicks(60);
                ((ServerLevel) victim.level()).sendParticles(ParticleTypes.ELECTRIC_SPARK,
                        victim.getX(), victim.getY() + 1, victim.getZ(), 10, 0.3, 0.5, 0.3, 0.2);
            }
        }

        // Vampiro: rouba vida
        if (attacker.getData(ModAttachments.VAMPIRE)) {
            attacker.heal(event.getAmount() * 0.25F);
        }
    }
}

package com.jojomod.item;

import com.jojomod.Config;
import com.jojomod.ModAttachments;
import com.jojomod.Stands;
import com.jojomod.util.Combat;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class StandArrowItem extends Item {
    public StandArrowItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }
        if (!(player instanceof ServerPlayer sp)) {
            return InteractionResultHolder.pass(stack);
        }

        if (!sp.getData(ModAttachments.STAND).isEmpty()) {
            Combat.msg(sp, "§eVocê já despertou um Stand: " + Stands.get(sp.getData(ModAttachments.STAND)).name());
            return InteractionResultHolder.fail(stack);
        }
        if (sp.getHealth() <= 8.0F && !sp.isCreative()) {
            Combat.msg(sp, "§cVocê está fraco demais para sobreviver à flecha!");
            return InteractionResultHolder.fail(stack);
        }

        // A flecha perfura o jogador
        sp.hurt(sp.damageSources().generic(), 6.0F);
        level.playSound(null, sp.blockPosition(), SoundEvents.TRIDENT_HIT, SoundSource.PLAYERS, 1.0F, 0.6F);
        if (!sp.isCreative()) stack.shrink(1);

        if (!sp.isCreative() && level.random.nextDouble() < Config.ARROW_DEATH_CHANCE.get()) {
            Combat.chat(sp, "§4A flecha te rejeitou. Você não era digno...");
            sp.kill();
            return InteractionResultHolder.consume(stack);
        }

        Stands.StandDef def = Stands.random(level.random);
        sp.setData(ModAttachments.STAND, def.id());
        sp.setData(ModAttachments.STAND_ACTIVE, false);
        level.playSound(null, sp.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 0.7F);
        Combat.chat(sp, "§d§l★ Seu Stand despertou: §f§l" + def.name() + " §r§d(" + def.user() + ")");
        Combat.chat(sp, "§7Habilidade 1: §f" + def.ab1() + " §7| Habilidade 2: §f" + def.ab2());
        Combat.chat(sp, "§7Aperte §fZ§7 para invocar, §fR§7 e §fX§7 para as habilidades.");
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.jojomod.stand_arrow.tooltip").withStyle(ChatFormatting.GRAY));
    }
}

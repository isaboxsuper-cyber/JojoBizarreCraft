package com.jojomod.item;

import com.jojomod.ModAttachments;
import com.jojomod.util.Combat;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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

public class HamonScrollItem extends Item {
    public HamonScrollItem(Properties props) {
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
        if (sp.getData(ModAttachments.VAMPIRE)) {
            Combat.chat(sp, "§eO Hamon rejeita o seu corpo morto-vivo!");
            sp.hurt(sp.damageSources().magic(), 4.0F);
            return InteractionResultHolder.fail(stack);
        }
        if (sp.getData(ModAttachments.HAMON)) {
            Combat.msg(sp, "§eVocê já domina o Hamon.");
            return InteractionResultHolder.fail(stack);
        }

        sp.setData(ModAttachments.HAMON, true);
        sp.setData(ModAttachments.HAMON_ENERGY, 100);
        if (!sp.isCreative()) stack.shrink(1);

        ServerLevel sl = (ServerLevel) level;
        sl.sendParticles(ParticleTypes.END_ROD, sp.getX(), sp.getY() + 1.0, sp.getZ(), 40, 0.5, 0.8, 0.5, 0.1);
        level.playSound(null, sp.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0F, 1.2F);
        Combat.chat(sp, "§6§lVocê dominou a respiração do Hamon!");
        Combat.chat(sp, "§7Segure §fShift§7 para respirar (energia). §fR§7 = Sunlight Yellow Overdrive | §fX§7 = Aura de Sol");
        Combat.chat(sp, "§7Com o Stand recolhido, as teclas R/X usam o Hamon.");
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.jojomod.hamon_scroll.tooltip").withStyle(ChatFormatting.GOLD));
    }
}

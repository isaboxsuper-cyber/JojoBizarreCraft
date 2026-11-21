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

public class StoneMaskItem extends Item {
    public StoneMaskItem(Properties props) {
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
            Combat.msg(sp, "§cVocê já é um vampiro.");
            return InteractionResultHolder.fail(stack);
        }

        // Os espinhos perfuram o cranio
        if (sp.getHealth() > 5.0F) {
            sp.hurt(sp.damageSources().generic(), 4.0F);
        }
        sp.setData(ModAttachments.VAMPIRE, true);
        sp.setData(ModAttachments.HAMON, false);
        sp.setData(ModAttachments.HAMON_ENERGY, 0);
        if (!sp.isCreative()) stack.shrink(1);

        ServerLevel sl = (ServerLevel) level;
        sl.sendParticles(ParticleTypes.SMOKE, sp.getX(), sp.getY() + 1.0, sp.getZ(), 40, 0.5, 0.8, 0.5, 0.05);
        sl.sendParticles(ParticleTypes.DAMAGE_INDICATOR, sp.getX(), sp.getY() + 1.5, sp.getZ(), 15, 0.3, 0.3, 0.3, 0.1);
        level.playSound(null, sp.blockPosition(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.PLAYERS, 0.8F, 0.6F);

        Combat.chat(sp, "§4§lOs espinhos da Máscara de Pedra se cravam em seu crânio...");
        Combat.chat(sp, "§4§lVocê abandonou a humanidade! §r§c(Vampiro)");
        Combat.chat(sp, "§7Cuidado com o sol e com o Hamon. §fR§7 = Drenar sangue | §fX§7 = Space Ripper Stingy Eyes");
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.jojomod.stone_mask.tooltip").withStyle(ChatFormatting.DARK_RED));
    }
}

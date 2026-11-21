package com.jojomod.event;

import com.jojomod.JojoMod;
import com.jojomod.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.village.WandererTradesEvent;

/** Comerciante ambulante vende a Flecha de Stand (e raramente a Mascara de Pedra). */
@EventBusSubscriber(modid = JojoMod.MOD_ID)
public class TradeEvents {
    @SubscribeEvent
    public static void onWandererTrades(WandererTradesEvent event) {
        // Troca comum
        event.getGenericTrades().add((trader, random) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 32), new ItemStack(ModItems.STAND_ARROW.get()), 1, 10, 0.05F));

        // Trocas raras
        event.getRareTrades().add((trader, random) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 24), new ItemStack(ModItems.STAND_ARROW.get()), 1, 10, 0.05F));
        event.getRareTrades().add((trader, random) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 48), new ItemStack(ModItems.STONE_MASK.get()), 1, 20, 0.05F));
    }
}

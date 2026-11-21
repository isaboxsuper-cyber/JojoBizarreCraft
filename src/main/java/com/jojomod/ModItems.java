package com.jojomod;

import com.jojomod.item.HamonScrollItem;
import com.jojomod.item.StandArrowItem;
import com.jojomod.item.StoneMaskItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(JojoMod.MOD_ID);

    public static final DeferredItem<Item> STAND_ARROW = ITEMS.register("stand_arrow",
            () -> new StandArrowItem(new Item.Properties().stacksTo(16).rarity(Rarity.RARE)));

    public static final DeferredItem<Item> STONE_MASK = ITEMS.register("stone_mask",
            () -> new StoneMaskItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    public static final DeferredItem<Item> HAMON_SCROLL = ITEMS.register("hamon_scroll",
            () -> new HamonScrollItem(new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON)));
}

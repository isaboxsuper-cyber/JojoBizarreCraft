package com.jojomod.event;

import com.jojomod.JojoMod;
import com.jojomod.ModItems;
import java.util.Set;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.LootTableLoadEvent;

/** Coloca Flecha de Stand e Mascara de Pedra nos baus de estruturas. */
@EventBusSubscriber(modid = JojoMod.MOD_ID)
public class LootEvents {
    private static final Set<String> ARROW_TABLES = Set.of(
            "chests/simple_dungeon", "chests/abandoned_mineshaft", "chests/desert_pyramid",
            "chests/jungle_temple", "chests/stronghold_corridor", "chests/stronghold_crossing",
            "chests/stronghold_library", "chests/buried_treasure", "chests/woodland_mansion",
            "chests/pillager_outpost", "chests/shipwreck_treasure", "chests/underwater_ruin_big",
            "chests/ancient_city", "chests/bastion_treasure", "chests/nether_bridge",
            "chests/end_city_treasure", "chests/ruined_portal");

    private static final Set<String> MASK_TABLES = Set.of(
            "chests/desert_pyramid", "chests/stronghold_library", "chests/ancient_city",
            "chests/jungle_temple", "chests/woodland_mansion", "chests/end_city_treasure",
            "chests/buried_treasure");

    @SubscribeEvent
    public static void onLootLoad(LootTableLoadEvent event) {
        if (!"minecraft".equals(event.getName().getNamespace())) return;
        String path = event.getName().getPath();

        if (ARROW_TABLES.contains(path)) {
            event.getTable().addPool(pool(ModItems.STAND_ARROW.get(), 0.15F));
        }
        if (MASK_TABLES.contains(path)) {
            event.getTable().addPool(pool(ModItems.STONE_MASK.get(), 0.10F));
        }
    }

    private static LootPool pool(Item item, float chance) {
        return LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .when(LootItemRandomChanceCondition.randomChance(chance))
                .add(LootItem.lootTableItem(item))
                .build();
    }
}

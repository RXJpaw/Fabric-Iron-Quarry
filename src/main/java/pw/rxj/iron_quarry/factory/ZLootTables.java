package pw.rxj.iron_quarry.factory;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import pw.rxj.iron_quarry.Main;
import pw.rxj.iron_quarry.resource.config.server.ChestLootingAugmentConfig;

public class ZLootTables {
    private static final ChestLootingAugmentConfig.Handler CHEST_LOOTING_AUGMENT_CONFIG = Main.CONFIG.getChestLootingAugmentConfig();
    public static void register() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            LootPool.Builder poolBuilder = CHEST_LOOTING_AUGMENT_CONFIG.getLootPoolBuilder(id);

            if(poolBuilder != null) tableBuilder.pool(poolBuilder);
        });
    }
}

package pw.rxj.iron_quarry.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import pw.rxj.iron_quarry.Main;
import pw.rxj.iron_quarry.resource.ConfigHandler;
import pw.rxj.iron_quarry.types.AugmentType;
import pw.rxj.iron_quarry.types.DynamicText;
import pw.rxj.iron_quarry.util.ItemEntry;

import java.util.List;

public class ZItems {
    private static final ConfigHandler.QuarryDrillStatsConfigHandler QuarryDrillConfig = Main.CONFIG.getQuarryDrillStatsConfig();

    public static final ItemEntry<BlueprintItem> BLUEPRINT = ItemEntry.<BlueprintItem>build()
            .withItem(new BlueprintItem(new FabricItemSettings().group(Main.ITEM_GROUP).maxCount(1)))
            .withId(Main.MOD_ID, "blueprint");

    public static final ItemEntry<Item> CONDUCTIVE_AMETHYST = ItemEntry.build()
            .withItem(new Item(new FabricItemSettings().group(Main.ITEM_GROUP).maxCount(64)))
            .withId(Main.MOD_ID, "conductive_amethyst");
    public static final ItemEntry<Item> ADVANCED_AMETHYST = ItemEntry.build()
            .withItem(new Item(new FabricItemSettings().group(Main.ITEM_GROUP).maxCount(64)))
            .withId(Main.MOD_ID, "advanced_amethyst");
    public static final ItemEntry<Item> ULTIMATE_AMETHYST = ItemEntry.build()
            .withItem(new Item(new FabricItemSettings().group(Main.ITEM_GROUP).maxCount(64)))
            .withId(Main.MOD_ID, "ultimate_amethyst");

    public static final ItemEntry<AugmentItem> AUGMENT = ItemEntry.<AugmentItem>build()
            .withItem(new AugmentItem(new FabricItemSettings().group(Main.ITEM_GROUP).maxCount(1)))
            .withId(Main.MOD_ID, "augment");
    public static final ItemEntry<AugmentItem> CHEST_LOOTING_AUGMENT = ItemEntry.<AugmentItem>build()
            .withItem(new AugmentItem(new FabricItemSettings().group(Main.ITEM_GROUP).maxCount(1))
                    .dynamicName(DynamicText.GOLD)
                    .unique(AugmentType.CHEST_LOOTING)
            )
            .withId(Main.MOD_ID, "chest_looting_augment");
    public static final ItemEntry<AugmentItem> SILK_TOUCH_AUGMENT = ItemEntry.<AugmentItem>build()
            .withItem(new AugmentItem(new FabricItemSettings().group(Main.ITEM_GROUP).maxCount(1))
                    .dynamicName(DynamicText.EMERALD)
                    .unique(AugmentType.SILK_TOUCH)
            )
            .withId(Main.MOD_ID, "silk_touch_augment");


    public static final ItemEntry<DrillItem> COPPER_DRILL = ItemEntry.<DrillItem>build()
            .withItem(new DrillItem(new FabricItemSettings().group(Main.ITEM_GROUP).maxCount(1), QuarryDrillConfig.getCopperDrill()))
            .withId(Main.MOD_ID, "copper_drill");
    public static final ItemEntry<DrillItem> IRON_DRILL = ItemEntry.<DrillItem>build()
            .withItem(new DrillItem(new FabricItemSettings().group(Main.ITEM_GROUP).maxCount(1), QuarryDrillConfig.getIronDrill()))
            .withId(Main.MOD_ID, "iron_drill");
    public static final ItemEntry<DrillItem> DIAMOND_DRILL = ItemEntry.<DrillItem>build()
            .withItem(new DrillItem(new FabricItemSettings().group(Main.ITEM_GROUP).maxCount(1), QuarryDrillConfig.getDiamondDrill()))
            .withId(Main.MOD_ID, "diamond_drill");
    public static final ItemEntry<DrillItem> NETHERITE_DRILL = ItemEntry.<DrillItem>build()
            .withItem(new DrillItem(new FabricItemSettings().group(Main.ITEM_GROUP).maxCount(1), QuarryDrillConfig.getNetheriteDrill()))
            .withId(Main.MOD_ID, "netherite_drill");
    public static final ItemEntry<DrillItem> SHULKER_DRILL = ItemEntry.<DrillItem>build()
            .withItem(new DrillItem(new FabricItemSettings().group(Main.ITEM_GROUP).maxCount(1), QuarryDrillConfig.getShulkerDrill()))
            .withId(Main.MOD_ID, "shulker_drill");
    public static final ItemEntry<DrillItem> NETHER_STAR_DRILL = ItemEntry.<DrillItem>build()
            .withItem(new DrillItem(new FabricItemSettings().group(Main.ITEM_GROUP).maxCount(1).rarity(Rarity.UNCOMMON), QuarryDrillConfig.getNetherStarDrill()))
            .withId(Main.MOD_ID, "nether_star_drill");

    public static final ItemEntry<QuarryMonitorItem> IRON_QUARRY_MONITOR = ItemEntry.<QuarryMonitorItem>build()
            .withItem(new QuarryMonitorItem(new FabricItemSettings().group(Main.ITEM_GROUP).maxCount(1)))
            .withId(Main.MOD_ID, "iron_quarry_monitor");
    public static final ItemEntry<QuarryMonitorItem> DIAMOND_QUARRY_MONITOR = ItemEntry.<QuarryMonitorItem>build()
            .withItem(new QuarryMonitorItem(new FabricItemSettings().group(Main.ITEM_GROUP).maxCount(1)))
            .withId(Main.MOD_ID, "diamond_quarry_monitor");
    public static final ItemEntry<QuarryMonitorItem> NETHERITE_QUARRY_MONITOR = ItemEntry.<QuarryMonitorItem>build()
            .withItem(new QuarryMonitorItem(new FabricItemSettings().group(Main.ITEM_GROUP).maxCount(1).fireproof()))
            .withId(Main.MOD_ID, "netherite_quarry_monitor");
    public static final ItemEntry<QuarryMonitorItem> SCULK_QUARRY_MONITOR = ItemEntry.<QuarryMonitorItem>build()
            .withItem(new QuarryMonitorItem(new FabricItemSettings().group(Main.ITEM_GROUP).maxCount(1).fireproof()))
            .withId(Main.MOD_ID, "sculk_quarry_monitor");

    private static final List<ItemEntry<?>> itemEntryList = List.of(
            BLUEPRINT,

            CONDUCTIVE_AMETHYST,
            ADVANCED_AMETHYST,
            ULTIMATE_AMETHYST,

            AUGMENT,
            CHEST_LOOTING_AUGMENT,
            SILK_TOUCH_AUGMENT,

            COPPER_DRILL,
            IRON_DRILL,
            DIAMOND_DRILL,
            NETHERITE_DRILL,
            SHULKER_DRILL,
            NETHER_STAR_DRILL,

            IRON_QUARRY_MONITOR,
            DIAMOND_QUARRY_MONITOR,
            NETHERITE_QUARRY_MONITOR,
            SCULK_QUARRY_MONITOR
    );

    public static void register(){
        for (ItemEntry<?> itemEntry : itemEntryList) {
            Registry.register(Registry.ITEM, itemEntry.getId(), itemEntry.getItem());
        }
    }
}

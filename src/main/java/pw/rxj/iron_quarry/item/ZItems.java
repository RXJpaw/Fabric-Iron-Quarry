package pw.rxj.iron_quarry.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import pw.rxj.iron_quarry.Main;
import pw.rxj.iron_quarry.resource.ConfigHandler;
import pw.rxj.iron_quarry.types.AugmentType;
import pw.rxj.iron_quarry.types.DynamicText;
import pw.rxj.iron_quarry.util.ItemEntryBuilder;

import java.util.List;

public class ZItems {
    private static final ConfigHandler.QuarryDrillStatsConfigHandler QuarryDrillConfig = Main.CONFIG.getQuarryDrillStatsConfig();

    public static final ItemEntryBuilder BLUEPRINT = new ItemEntryBuilder()
            .setItem(new BlueprintItem(new FabricItemSettings().group(Main.ITEM_GROUP).maxCount(1)))
            .setIdentifier(Main.MOD_ID, "blueprint");

    public static final ItemEntryBuilder CONDUCTIVE_AMETHYST = new ItemEntryBuilder()
            .setItem(new Item(new FabricItemSettings().group(Main.ITEM_GROUP).maxCount(64)))
            .setIdentifier(Main.MOD_ID, "conductive_amethyst");
    public static final ItemEntryBuilder SUPERCONDUCTIVE_AMETHYST = new ItemEntryBuilder()
            .setItem(new Item(new FabricItemSettings().group(Main.ITEM_GROUP).maxCount(64)) {
                @Override
                public boolean hasGlint(ItemStack stack) {
                    return true;
                }
            })
            .setIdentifier(Main.MOD_ID, "superconductive_amethyst");

    public static final ItemEntryBuilder AUGMENT = new ItemEntryBuilder()
            .setItem(new AugmentItem(new FabricItemSettings().group(Main.ITEM_GROUP).maxCount(1)))
            .setIdentifier(Main.MOD_ID, "augment");
    public static final ItemEntryBuilder CHEST_LOOTING_AUGMENT = new ItemEntryBuilder()
            .setItem(new AugmentItem(new FabricItemSettings().group(Main.ITEM_GROUP).maxCount(1))
                    .dynamicName(DynamicText.GOLD)
                    .unique(AugmentType.CHEST_LOOTING)
            )
            .setIdentifier(Main.MOD_ID, "chest_looting_augment");
    public static final ItemEntryBuilder SILK_TOUCH_AUGMENT = new ItemEntryBuilder()
            .setItem(new AugmentItem(new FabricItemSettings().group(Main.ITEM_GROUP).maxCount(1))
                    .dynamicName(DynamicText.EMERALD)
                    .unique(AugmentType.SILK_TOUCH)
            )
            .setIdentifier(Main.MOD_ID, "silk_touch_augment");


    public static final ItemEntryBuilder COPPER_DRILL = new ItemEntryBuilder()
            .setItem(new DrillItem(new FabricItemSettings().group(Main.ITEM_GROUP).maxCount(1), QuarryDrillConfig.getCopperDrill()))
            .setIdentifier(Main.MOD_ID, "copper_drill");
    public static final ItemEntryBuilder IRON_DRILL = new ItemEntryBuilder()
            .setItem(new DrillItem(new FabricItemSettings().group(Main.ITEM_GROUP).maxCount(1), QuarryDrillConfig.getIronDrill()))
            .setIdentifier(Main.MOD_ID, "iron_drill");
    public static final ItemEntryBuilder DIAMOND_DRILL = new ItemEntryBuilder()
            .setItem(new DrillItem(new FabricItemSettings().group(Main.ITEM_GROUP).maxCount(1), QuarryDrillConfig.getDiamondDrill()))
            .setIdentifier(Main.MOD_ID, "diamond_drill");
    public static final ItemEntryBuilder NETHERITE_DRILL = new ItemEntryBuilder()
            .setItem(new DrillItem(new FabricItemSettings().group(Main.ITEM_GROUP).maxCount(1), QuarryDrillConfig.getNetheriteDrill()))
            .setIdentifier(Main.MOD_ID, "netherite_drill");
    public static final ItemEntryBuilder SHULKER_DRILL = new ItemEntryBuilder()
            .setItem(new DrillItem(new FabricItemSettings().group(Main.ITEM_GROUP).maxCount(1), QuarryDrillConfig.getShulkerDrill()))
            .setIdentifier(Main.MOD_ID, "shulker_drill");
    public static final ItemEntryBuilder NETHER_STAR_DRILL = new ItemEntryBuilder()
            .setItem(new DrillItem(new FabricItemSettings().group(Main.ITEM_GROUP).maxCount(1).rarity(Rarity.UNCOMMON), QuarryDrillConfig.getNetherStarDrill()))
            .setIdentifier(Main.MOD_ID, "nether_star_drill");

    public static final ItemEntryBuilder IRON_QUARRY_MONITOR = new ItemEntryBuilder()
            .setItem(new QuarryMonitorItem(new FabricItemSettings().group(Main.ITEM_GROUP).maxCount(1)))
            .setIdentifier(Main.MOD_ID, "iron_quarry_monitor");
    public static final ItemEntryBuilder DIAMOND_QUARRY_MONITOR = new ItemEntryBuilder()
            .setItem(new QuarryMonitorItem(new FabricItemSettings().group(Main.ITEM_GROUP).maxCount(1)))
            .setIdentifier(Main.MOD_ID, "diamond_quarry_monitor");
    public static final ItemEntryBuilder NETHERITE_QUARRY_MONITOR = new ItemEntryBuilder()
            .setItem(new QuarryMonitorItem(new FabricItemSettings().group(Main.ITEM_GROUP).maxCount(1).fireproof()))
            .setIdentifier(Main.MOD_ID, "netherite_quarry_monitor");
    public static final ItemEntryBuilder SCULK_QUARRY_MONITOR = new ItemEntryBuilder()
            .setItem(new QuarryMonitorItem(new FabricItemSettings().group(Main.ITEM_GROUP).maxCount(1).fireproof()))
            .setIdentifier(Main.MOD_ID, "sculk_quarry_monitor");

    private static final List<ItemEntryBuilder> itemEntryList = List.of(
            BLUEPRINT,

            CONDUCTIVE_AMETHYST,
            SUPERCONDUCTIVE_AMETHYST,

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
        for (ItemEntryBuilder itemEntry : itemEntryList) {
            Registry.register(Registry.ITEM, itemEntry.getIdentifier(), itemEntry.getItem());
        }
    }
}

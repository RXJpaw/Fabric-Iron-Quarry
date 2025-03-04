package pw.rxj.iron_quarry.block;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Rarity;
import pw.rxj.iron_quarry.Main;
import pw.rxj.iron_quarry.resource.config.server.QuarryStatsConfig;
import pw.rxj.iron_quarry.util.BlockEntry;

import java.util.List;

public class ZBlocks {
    private static final QuarryStatsConfig.Handler QUARRY_STATS_CONFIG = Main.CONFIG.getQuarryStatsConfig();

    public static final BlockEntry<QuarryBlock, BlockItem> COPPER_QUARRY = BlockEntry.buildQuarryBlock()
            .withBlock(new QuarryBlock(Blocks.COPPER_BLOCK, "block/copper_quarry", QUARRY_STATS_CONFIG.getCopperQuarry()))
            .withBlockItemSettings(new Item.Settings())
            .withId(Main.MOD_ID, "copper_quarry");
    public static final BlockEntry<QuarryBlock, BlockItem> IRON_QUARRY = BlockEntry.buildQuarryBlock()
            .withBlock(new QuarryBlock(Blocks.IRON_BLOCK, "block/iron_quarry", QUARRY_STATS_CONFIG.getIronQuarry()))
            .withBlockItemSettings(new Item.Settings())
            .withId(Main.MOD_ID, "iron_quarry");
    public static final BlockEntry<QuarryBlock, BlockItem> GOLD_QUARRY = BlockEntry.buildQuarryBlock()
            .withBlock(new QuarryBlock(Blocks.GOLD_BLOCK, "block/gold_quarry", QUARRY_STATS_CONFIG.getGoldQuarry()))
            .withBlockItemSettings(new Item.Settings())
            .withId(Main.MOD_ID, "gold_quarry");
    public static final BlockEntry<QuarryBlock, BlockItem> DIAMOND_QUARRY = BlockEntry.buildQuarryBlock()
            .withBlock(new QuarryBlock(Blocks.DIAMOND_BLOCK, "block/diamond_quarry", QUARRY_STATS_CONFIG.getDiamondQuarry()))
            .withBlockItemSettings(new Item.Settings())
            .withId(Main.MOD_ID, "diamond_quarry");
    public static final BlockEntry<QuarryBlock, BlockItem> NETHERITE_QUARRY = BlockEntry.buildQuarryBlock()
            .withBlock(new QuarryBlock(Blocks.NETHERITE_BLOCK, "block/netherite_quarry", QUARRY_STATS_CONFIG.getNetheriteQuarry()))
            .withBlockItemSettings(new Item.Settings().rarity(Rarity.UNCOMMON).fireproof())
            .withId(Main.MOD_ID, "netherite_quarry");
    public static final BlockEntry<QuarryBlock, BlockItem> NETHER_STAR_QUARRY = BlockEntry.buildQuarryBlock()
            .withBlock(new QuarryBlock(Blocks.NETHERITE_BLOCK, "block/nether_star_quarry", QUARRY_STATS_CONFIG.getNetherStarQuarry()))
            .withBlockItemSettings(new Item.Settings().rarity(Rarity.RARE).fireproof())
            .withId(Main.MOD_ID, "nether_star_quarry");
    public static final List<QuarryBlock> quarryBlockList = List.of(
            COPPER_QUARRY.getBlock(),
            IRON_QUARRY.getBlock(),
            GOLD_QUARRY.getBlock(),
            DIAMOND_QUARRY.getBlock(),
            NETHERITE_QUARRY.getBlock(),
            NETHER_STAR_QUARRY.getBlock()
    );

    public static final BlockEntry<RedstoneBlock, BlockItem> REINFORCED_REDSTONE_BLOCK = BlockEntry.<RedstoneBlock>buildInherit()
            .withBlock(new RedstoneBlock(AbstractBlock.Settings.copy(Blocks.REDSTONE_BLOCK)))
            .withBlockItemSettings(new Item.Settings())
            .withId(Main.MOD_ID, "reinforced_redstone_block");
    public static final BlockEntry<Block, BlockItem> REINFORCED_LAPIS_BLOCK = BlockEntry.build()
            .withBlock(new Block(AbstractBlock.Settings.copy(Blocks.LAPIS_BLOCK)))
            .withBlockItemSettings(new Item.Settings())
            .withId(Main.MOD_ID, "reinforced_lapis_block");

    private static final List<BlockEntry<? extends Block, BlockItem>> blockEntryList = List.of(
            COPPER_QUARRY,
            IRON_QUARRY,
            GOLD_QUARRY,
            DIAMOND_QUARRY,
            NETHERITE_QUARRY,
            NETHER_STAR_QUARRY,
            REINFORCED_REDSTONE_BLOCK,
            REINFORCED_LAPIS_BLOCK
    );

    public static void register(){
        for (BlockEntry<? extends Block, BlockItem> blockEntry : blockEntryList) {
            Registry.register(Registries.BLOCK, blockEntry.getId(), blockEntry.getBlock());
            Registry.register(Registries.ITEM, blockEntry.getId(), blockEntry.getBlockItem());
        }

        ItemGroupEvents.modifyEntriesEvent(Main.ITEM_GROUP).register(entries -> {
            blockEntryList.forEach(blockItemBlockEntry -> entries.add(blockItemBlockEntry.getBlock()));
        });
    }
}

package pw.rxj.iron_quarry.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import pw.rxj.iron_quarry.Main;
import pw.rxj.iron_quarry.resource.ConfigHandler;
import pw.rxj.iron_quarry.util.BlockEntry;

import java.util.List;

public class ZBlocks {
    private static final ConfigHandler.QuarryStatsConfigHandler QuarryStatsConfig = Main.CONFIG.getQuarryStatsConfig();

    public static final BlockEntry<QuarryBlock, BlockItem> COPPER_QUARRY = BlockEntry.buildQuarryBlock()
            .withBlock(new QuarryBlock(Blocks.COPPER_BLOCK, "block/copper_quarry", QuarryStatsConfig.getCopperQuarry()))
            .withBlockItemSettings(new FabricItemSettings().group(Main.ITEM_GROUP))
            .withId(Main.MOD_ID, "copper_quarry");
    public static final BlockEntry<QuarryBlock, BlockItem> IRON_QUARRY = BlockEntry.buildQuarryBlock()
            .withBlock(new QuarryBlock(Blocks.IRON_BLOCK, "block/iron_quarry", QuarryStatsConfig.getIronQuarry()))
            .withBlockItemSettings(new FabricItemSettings().group(Main.ITEM_GROUP))
            .withId(Main.MOD_ID, "iron_quarry");
    public static final BlockEntry<QuarryBlock, BlockItem> GOLD_QUARRY = BlockEntry.buildQuarryBlock()
            .withBlock(new QuarryBlock(Blocks.GOLD_BLOCK, "block/gold_quarry", QuarryStatsConfig.getGoldQuarry()))
            .withBlockItemSettings(new FabricItemSettings().group(Main.ITEM_GROUP))
            .withId(Main.MOD_ID, "gold_quarry");
    public static final BlockEntry<QuarryBlock, BlockItem> DIAMOND_QUARRY = BlockEntry.buildQuarryBlock()
            .withBlock(new QuarryBlock(Blocks.DIAMOND_BLOCK, "block/diamond_quarry", QuarryStatsConfig.getDiamondQuarry()))
            .withBlockItemSettings(new FabricItemSettings().group(Main.ITEM_GROUP))
            .withId(Main.MOD_ID, "diamond_quarry");
    public static final BlockEntry<QuarryBlock, BlockItem> NETHERITE_QUARRY = BlockEntry.buildQuarryBlock()
            .withBlock(new QuarryBlock(Blocks.NETHERITE_BLOCK, "block/netherite_quarry", QuarryStatsConfig.getNetheriteQuarry()))
            .withBlockItemSettings(new FabricItemSettings().group(Main.ITEM_GROUP).rarity(Rarity.UNCOMMON).fireproof())
            .withId(Main.MOD_ID, "netherite_quarry");
    public static final BlockEntry<QuarryBlock, BlockItem> NETHER_STAR_QUARRY = BlockEntry.buildQuarryBlock()
            .withBlock(new QuarryBlock(Blocks.NETHERITE_BLOCK, "block/nether_star_quarry", QuarryStatsConfig.getNetherStarQuarry()))
            .withBlockItemSettings(new FabricItemSettings().group(Main.ITEM_GROUP).rarity(Rarity.RARE).fireproof())
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
            .withBlockItemSettings(new FabricItemSettings().group(Main.ITEM_GROUP))
            .withId(Main.MOD_ID, "reinforced_redstone_block");
    public static final BlockEntry<Block, BlockItem> REINFORCED_LAPIS_BLOCK = BlockEntry.build()
            .withBlock(new Block(AbstractBlock.Settings.copy(Blocks.LAPIS_BLOCK)))
            .withBlockItemSettings(new FabricItemSettings().group(Main.ITEM_GROUP))
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
            Registry.register(Registry.BLOCK, blockEntry.getId(), blockEntry.getBlock());
            Registry.register(Registry.ITEM, blockEntry.getId(), blockEntry.getBlockItem());
        }
    }
}

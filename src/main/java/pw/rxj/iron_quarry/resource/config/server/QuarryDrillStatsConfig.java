package pw.rxj.iron_quarry.resource.config.server;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import pw.rxj.iron_quarry.item.ZItems;
import pw.rxj.iron_quarry.resource.config.AbstractInnerConfigHandler;
import pw.rxj.iron_quarry.resource.config.ConfigHandler;

@EqualsAndHashCode
public class QuarryDrillStatsConfig {
    public final Entry copperDrill = new Entry(649, 1, 2);
    public final Entry ironDrill = new Entry(1_284, 2, 5);
    public final Entry diamondDrill = new Entry(6_649, 3, 4);
    public final Entry netheriteDrill = new Entry(93_750, 4, 5);
    public final Entry shulkerDrill = new Entry(159_375, 5, 7);
    public final Entry netherStarDrill = new Entry(796_875, 6, 9);

    @EqualsAndHashCode
    @AllArgsConstructor
    public static class Entry {
        public final int durability;
        public final int miningLevel;
        public final int enchantability;
    }

    public static class Handler extends AbstractInnerConfigHandler {
        private Handler(ConfigHandler handler) {
            super(handler);
        }
        public static Handler of(ConfigHandler handler) {
            return new Handler(handler);
        }

        public QuarryDrillStatsConfig.Entry getCopperDrill() {
            return this.server.quarryDrillStats.copperDrill;
        }
        public QuarryDrillStatsConfig.Entry getIronDrill() {
            return this.server.quarryDrillStats.ironDrill;
        }
        public QuarryDrillStatsConfig.Entry getDiamondDrill() {
            return this.server.quarryDrillStats.diamondDrill;
        }
        public QuarryDrillStatsConfig.Entry getNetheriteDrill() {
            return this.server.quarryDrillStats.netheriteDrill;
        }
        public QuarryDrillStatsConfig.Entry getShulkerDrill() {
            return this.server.quarryDrillStats.shulkerDrill;
        }
        public QuarryDrillStatsConfig.Entry getNetherStarDrill() {
            return this.server.quarryDrillStats.netherStarDrill;
        }

        @Override
        public void applyChanges() {
            ZItems.COPPER_DRILL.getItem().override(this.getCopperDrill());
            ZItems.IRON_DRILL.getItem().override(this.getIronDrill());
            ZItems.DIAMOND_DRILL.getItem().override(this.getDiamondDrill());
            ZItems.NETHERITE_DRILL.getItem().override(this.getNetheriteDrill());
            ZItems.SHULKER_DRILL.getItem().override(this.getShulkerDrill());
            ZItems.NETHER_STAR_DRILL.getItem().override(this.getNetherStarDrill());
        }
    }
}

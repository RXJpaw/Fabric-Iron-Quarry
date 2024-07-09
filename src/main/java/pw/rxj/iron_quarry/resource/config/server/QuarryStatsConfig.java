package pw.rxj.iron_quarry.resource.config.server;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import pw.rxj.iron_quarry.block.ZBlocks;
import pw.rxj.iron_quarry.resource.config.AbstractInnerConfigHandler;
import pw.rxj.iron_quarry.resource.config.ConfigHandler;

@EqualsAndHashCode
public class QuarryStatsConfig {
    public final Entry copperQuarry = new Entry(0, 40, 6_000, 16); //640 RF ~ 50% Coal Generator
    public final Entry ironQuarry = new Entry(1, 30, 40_000, 48); //1.440 RF ~ 12 Advanced Solar Panels (day/night average)
    public final Entry goldQuarry = new Entry(2, 20, 260_000, 160); //3.200 RF ~ 16 Industrial Solar Panels (day/night average)
    public final Entry diamondQuarry = new Entry(3, 10, 1_500_000, 640); //6.400 RF ~ 32 Ultimate Solar Panels (day/night average)
    public final Entry netheriteQuarry = new Entry(4, 5, 7_000_000, 2_500); //12.500 RF ~ Nitro Reactor with Packed Ice
    public final Entry netherStarQuarry = new Entry(6, 2, 50_000_000, 12_300); //24.600 RF ~ Nitro Reactor with Blue Ice

    @EqualsAndHashCode
    @AllArgsConstructor
    public static class Entry {
        public final int augmentLimit;
        public final int ticksPerOperation;
        public final int energyCapacity;
        public final int baseConsumption;
    }

    public static class Handler extends AbstractInnerConfigHandler {
        private Handler(ConfigHandler handler) {
            super(handler);
        }
        public static Handler of(ConfigHandler handler) {
            return new Handler(handler);
        }

        public QuarryStatsConfig.Entry getCopperQuarry() {
            return this.server.quarryStats.copperQuarry;
        }
        public QuarryStatsConfig.Entry getIronQuarry() {
            return this.server.quarryStats.ironQuarry;
        }
        public QuarryStatsConfig.Entry getGoldQuarry() {
            return this.server.quarryStats.goldQuarry;
        }
        public QuarryStatsConfig.Entry getDiamondQuarry() {
            return this.server.quarryStats.diamondQuarry;
        }
        public QuarryStatsConfig.Entry getNetheriteQuarry() {
            return this.server.quarryStats.netheriteQuarry;
        }
        public QuarryStatsConfig.Entry getNetherStarQuarry() {
            return this.server.quarryStats.netherStarQuarry;
        }

        @Override
        public void applyChanges() {
            ZBlocks.COPPER_QUARRY.getBlock().override(this.getCopperQuarry());
            ZBlocks.IRON_QUARRY.getBlock().override(this.getIronQuarry());
            ZBlocks.GOLD_QUARRY.getBlock().override(this.getGoldQuarry());
            ZBlocks.DIAMOND_QUARRY.getBlock().override(this.getDiamondQuarry());
            ZBlocks.NETHERITE_QUARRY.getBlock().override(this.getNetheriteQuarry());
            ZBlocks.NETHER_STAR_QUARRY.getBlock().override(this.getNetherStarQuarry());
        }
    }
}

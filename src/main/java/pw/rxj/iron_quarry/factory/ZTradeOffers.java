package pw.rxj.iron_quarry.factory;

import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import pw.rxj.iron_quarry.Main;
import pw.rxj.iron_quarry.item.ZItems;
import pw.rxj.iron_quarry.resource.config.server.SilkTouchAugmentConfig;

public class ZTradeOffers {
    private static final SilkTouchAugmentConfig.Handler SILK_TOUCH_AUGMENT_CONFIG = Main.CONFIG.getSilkTouchAugmentConfig();

    public static TradeOffer SILK_TOUCH_AUGMENT(Entity entity, Random random) {
        ItemStack base = SILK_TOUCH_AUGMENT_CONFIG.getRandomizedBase(random);
        ItemStack addition = SILK_TOUCH_AUGMENT_CONFIG.getRandomizedAddition(random);
        ItemStack result = new ItemStack(ZItems.SILK_TOUCH_AUGMENT.getItem(), 1);

        return new TradeOffer(base, addition, result, 3, 50, 0.25F);
    }

    public static void register() {
        if(SILK_TOUCH_AUGMENT_CONFIG.isVillagerEnabled()) {
            TradeOfferHelper.registerVillagerOffers(SILK_TOUCH_AUGMENT_CONFIG.getVillagerProfession(), SILK_TOUCH_AUGMENT_CONFIG.getVillagerLevel(), factories -> {
                factories.add(ZTradeOffers::SILK_TOUCH_AUGMENT);
            });
        }

        if(SILK_TOUCH_AUGMENT_CONFIG.isWanderingVillagerEnabled()) {
            TradeOfferHelper.registerWanderingTraderOffers(SILK_TOUCH_AUGMENT_CONFIG.getWanderingVillagerLevel(), factories -> {
                factories.add(ZTradeOffers::SILK_TOUCH_AUGMENT);
            });
        }
    }
}

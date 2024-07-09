package pw.rxj.iron_quarry.interfaces;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemUsageContext;

public interface IHandledKeyedAction {
    default void keyedAttackOnBlock(String keyName, ItemUsageContext context, BlockState blockState) {}
    default void keyedUseOnBlock(String keyName, ItemUsageContext context, BlockState blockState) {}
    default void keyedUse(String keyName, ItemUsageContext context) {}
}

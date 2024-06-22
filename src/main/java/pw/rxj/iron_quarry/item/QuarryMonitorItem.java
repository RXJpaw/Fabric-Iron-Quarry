package pw.rxj.iron_quarry.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import pw.rxj.iron_quarry.util.ZUtil;

public class QuarryMonitorItem extends Item {
    public QuarryMonitorItem(Settings settings) {
        super(settings);
    }

    public static boolean isOf(ItemStack stack){
        return ZUtil.getBlockOrItem(stack) instanceof QuarryMonitorItem;
    }
    public static boolean isNotOf(ItemStack stack){
        return !isOf(stack);
    }


}

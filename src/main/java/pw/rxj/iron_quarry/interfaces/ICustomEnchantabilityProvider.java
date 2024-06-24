package pw.rxj.iron_quarry.interfaces;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;

import java.util.HashSet;

public interface ICustomEnchantabilityProvider {
    HashSet<Enchantment> getViableEnchantments(ItemStack stack);
}

package pw.rxj.iron_quarry.item;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Rarity;
import pw.rxj.iron_quarry.interfaces.ICustomEnchantabilityProvider;
import pw.rxj.iron_quarry.resource.Config;

import java.util.HashSet;

public class DrillItem extends Item implements ICustomEnchantabilityProvider {
    private int enchantability;
    private int miningLevel;

    public DrillItem(Item.Settings settings, Config.Server.QuarryDrillStatsConfig.Entry entry) {
        super(settings);

        this.enchantability = entry.enchantability;
        this.miningLevel = entry.miningLevel;
        this.maxDamage = entry.durability;
    }
    public void override(Config.Server.QuarryDrillStatsConfig.Entry quarryDrillStats) {
        this.enchantability = quarryDrillStats.enchantability;
        this.miningLevel = quarryDrillStats.miningLevel;
        this.maxDamage = quarryDrillStats.durability;
    }

    @Override
    public HashSet<Enchantment> getViableEnchantments(ItemStack stack) {
        HashSet<Enchantment> enchantments = new HashSet<>();
        //Unbreaking is automatically added since it always works on damageable items, but idc
        enchantments.add(Enchantments.UNBREAKING);
        enchantments.add(Enchantments.EFFICIENCY);

        return enchantments;
    }
    @Override
    public int getEnchantability() {
        return this.enchantability;
    }
    public int getMiningLevel() {
        return this.miningLevel;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return this.getRarity(stack).equals(Rarity.UNCOMMON) || super.hasGlint(stack);
    }
}

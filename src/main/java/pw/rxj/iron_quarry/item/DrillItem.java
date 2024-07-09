package pw.rxj.iron_quarry.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import pw.rxj.iron_quarry.interfaces.ICustomDurability;
import pw.rxj.iron_quarry.interfaces.ICustomEnchantabilityProvider;
import pw.rxj.iron_quarry.resource.Config;
import pw.rxj.iron_quarry.util.ReadableString;
import pw.rxj.iron_quarry.util.ZUtil;

import java.util.HashSet;
import java.util.List;

public class DrillItem extends Item implements ICustomEnchantabilityProvider, ICustomDurability {
    private int enchantability;
    private int miningLevel;
    private int maxCDamage;

    public DrillItem(Item.Settings settings, Config.Server.QuarryDrillStatsConfig.Entry entry) {
        super(settings);

        this.enchantability = entry.enchantability;
        this.miningLevel = entry.miningLevel;
        this.maxCDamage = entry.durability;
    }
    public void override(Config.Server.QuarryDrillStatsConfig.Entry quarryDrillStats) {
        this.enchantability = quarryDrillStats.enchantability;
        this.miningLevel = quarryDrillStats.miningLevel;
        this.maxCDamage = quarryDrillStats.durability;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        int maxDamage = this.getMaxCDamage();
        int durability = maxDamage - this.getCDamage(stack);
        String durabilityPercent = ZUtil.expandableFixedFloat((float) durability / maxDamage * 100);

        MutableText LORE_MINING_LEVEL = ReadableString.translatable("item.iron_quarry.lore.mining_level", ReadableString.translatable("mining_level.iron_quarry." + this.getMiningLevel()));
        MutableText LORE_DURABILITY = ReadableString.translatable("item.iron_quarry.lore.custom_durability", ZUtil.expandableFixedInt(durability), ZUtil.expandableFixedInt(maxDamage), durabilityPercent);

        tooltip.add(LORE_MINING_LEVEL);
        tooltip.add(LORE_DURABILITY);

        if(stack.hasEnchantments()) {
            tooltip.add(ReadableString.empty());
        }
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }
    @Override
    public HashSet<Enchantment> getViableEnchantments(ItemStack stack) {
        HashSet<Enchantment> enchantments = new HashSet<>();
        enchantments.add(Enchantments.UNBREAKING);
        enchantments.add(Enchantments.EFFICIENCY);

        return enchantments;
    }
    @Override
    public int getMaxCDamage() {
        return this.maxCDamage;
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

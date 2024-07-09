package pw.rxj.iron_quarry.interfaces;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public interface ICustomDurability {
    int getMaxCDamage();

    default boolean isCDamaged(ItemStack stack) {
        return this.getCDamage(stack) > 0;
    }
    default int getCDamage(ItemStack stack) {
        return stack.getNbt() == null ? 0 : stack.getNbt().getInt("CDamage");
    }
    default void setCDamage(ItemStack stack, int damage) {
        stack.getOrCreateNbt().putInt("CDamage", Math.max(0, damage));
    }

    default boolean cdamage(ItemStack stack, int amount, Random random, @Nullable ServerPlayerEntity player) {
        int i;
        if (amount > 0) {
            i = EnchantmentHelper.getLevel(Enchantments.UNBREAKING, stack);
            int j = 0;

            for(int k = 0; i > 0 && k < amount; ++k) {
                if (UnbreakingEnchantment.shouldPreventDamage(stack, i, random)) {
                    ++j;
                }
            }

            amount -= j;
            if (amount <= 0) {
                return false;
            }
        }

        i = this.getCDamage(stack) + amount;
        this.setCDamage(stack, i);
        return i >= this.getMaxCDamage();
    }

    default int getCItemBarStep(ItemStack stack) {
        return Math.round(13.0F - (float)this.getCDamage(stack) * 13.0F / (float)this.getMaxCDamage());
    }
    default int getCItemBarColor(ItemStack stack) {
        float f = Math.max(0.0F, ((float)this.getMaxCDamage() - (float)this.getCDamage(stack)) / (float)this.getMaxCDamage());
        return MathHelper.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }
}

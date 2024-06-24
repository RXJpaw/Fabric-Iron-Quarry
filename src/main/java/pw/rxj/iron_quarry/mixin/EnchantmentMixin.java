package pw.rxj.iron_quarry.mixin;


import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pw.rxj.iron_quarry.interfaces.ICustomEnchantabilityProvider;
import pw.rxj.iron_quarry.util.ZUtil;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {
    @Inject(method = "isAcceptableItem", at = @At(value = "HEAD"), cancellable = true)
    private void isAcceptableItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if(ZUtil.getBlockOrItem(stack) instanceof ICustomEnchantabilityProvider enchantabilityProvider) {
            cir.setReturnValue(enchantabilityProvider.getViableEnchantments(stack).contains((Enchantment) (Object) this));
        }
    }
}

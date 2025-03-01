package pw.rxj.iron_quarry.mixin;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.rxj.iron_quarry.render.EnergyBarRenderer;


@Mixin(DrawContext.class)
public abstract class EnergyBarMixin {
    @Inject(method="drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isItemBarVisible()Z", shift = At.Shift.AFTER))
    private void renderGuiItemOverlay(TextRenderer textRenderer, ItemStack stack, int x, int y, String countOverride, CallbackInfo ci) {
        EnergyBarRenderer.renderGuiItemOverlay((DrawContext) (Object) this, textRenderer, stack, x, y, countOverride, ci);
    }
}

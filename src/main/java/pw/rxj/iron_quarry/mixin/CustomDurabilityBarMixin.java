package pw.rxj.iron_quarry.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.rxj.iron_quarry.interfaces.ICustomDurability;

@Mixin(DrawContext.class)
public abstract class CustomDurabilityBarMixin {

    @Shadow public abstract void fill(RenderLayer layer, int x1, int x2, int y1, int y2, int color);

    @Inject(method="drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isItemBarVisible()Z", shift = At.Shift.AFTER))
    private void renderGuiItemOverlay(TextRenderer textRenderer, ItemStack stack, int x, int y, String countOverride, CallbackInfo ci) {
        if (stack.getItem() instanceof ICustomDurability customDurability && customDurability.getCDamage(stack) > 0) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableBlend();

            int step = customDurability.getCItemBarStep(stack);
            int color = customDurability.getCItemBarColor(stack);
            int fillX = x + 2;
            int fillY = y + 13;

            this.fill(RenderLayer.getGuiOverlay(), fillX, fillY, fillX + 13,   fillY + 2, -16777216);
            this.fill(RenderLayer.getGuiOverlay(), fillX, fillY, fillX + step, fillY + 1, color | -16777216);

            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
        }
    }
}
package pw.rxj.iron_quarry.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.rxj.iron_quarry.interfaces.ICustomDurability;

@Mixin(ItemRenderer.class)
public abstract class CustomDurabilityBarMixin {
    @Inject(method="renderGuiItemOverlay(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "TAIL"))
    private void renderGuiItemOverlay(MatrixStack matrices, TextRenderer textRenderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo ci) {
        if (stack.getItem() instanceof ICustomDurability customDurability && customDurability.getCDamage(stack) > 0) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableBlend();

            int step = customDurability.getCItemBarStep(stack);
            int color = customDurability.getCItemBarColor(stack);
            int fillX = x + 2;
            int fillY = y + 13;

            DrawableHelper.fill(matrices, fillX, fillY, fillX + 13,   fillY + 2, -16777216);
            DrawableHelper.fill(matrices, fillX, fillY, fillX + step, fillY + 1, color | -16777216);

            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
        }
    }
}
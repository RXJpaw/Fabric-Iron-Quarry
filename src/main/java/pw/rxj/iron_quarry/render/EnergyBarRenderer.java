package pw.rxj.iron_quarry.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.rxj.iron_quarry.interfaces.IEnergyContainer;

public class EnergyBarRenderer {
    public static void renderGuiItemOverlay(MatrixStack matrices, TextRenderer textRenderer, ItemStack stack, int x, int y, @Nullable String countLabel, CallbackInfo ci) {
        if (getItemBarStep(stack) > 0) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableBlend();

            int step = (int) Math.ceil(getItemBarStep(stack));
            int color = 0xB76CEA;
            int fillX = x + 2;
            int fillY = y + 13;

            DrawableHelper.fill(matrices, fillX, fillY, fillX + 13,   fillY + 2, -16777216);
            DrawableHelper.fill(matrices, fillX, fillY, fillX + step, fillY + 1, color | -16777216);

            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
        }
    }

    public static float getItemBarStep(ItemStack stack) {
        IEnergyContainer energyContainer = IEnergyContainer.instanceOf(stack);
        if(energyContainer == null) return 0;

        long stored = energyContainer.getEnergyStored(stack);
        long capacity = energyContainer.getEnergyCapacity();

        return (float) Math.max(Math.min((double) stored / (double) capacity * 13.0F, 13.0F), 0.0F);
    }
}

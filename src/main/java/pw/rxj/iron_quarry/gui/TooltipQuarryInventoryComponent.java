package pw.rxj.iron_quarry.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import pw.rxj.iron_quarry.screen.QuarryBlockScreen;
import pw.rxj.iron_quarry.util.ComplexInventory;

public class TooltipQuarryInventoryComponent implements CustomTooltipComponent {
    private static final Identifier AUGMENTATION_CONFIGURATION_TEXTURE = QuarryBlockScreen.AUGMENTATION_CONFIGURATION_TEXTURE;
    private static final Identifier QUARRY_SCREEN_BACKGROUND_TEXTURE = QuarryBlockScreen.BACKGROUND_TEXTURE;

    public final TooltipQuarryInventoryData quarryInventoryData;
    public final ItemStack blueprintStack;
    public final ItemStack drillStack;
    public final ComplexInventory MachineUpgradesInventory;
    public final int augmentLimit;

    private TooltipQuarryInventoryComponent(TooltipQuarryInventoryData tooltipData) {
        this.quarryInventoryData = tooltipData;
        this.blueprintStack = tooltipData.getBlueprintStack();
        this.drillStack = tooltipData.getDrillStack();
        this.MachineUpgradesInventory = tooltipData.getMachineUpgradesInventory();
        this.augmentLimit = tooltipData.getAugmentLimit();
    }

    @Override
    public CustomTooltipData getCustomTooltipData() {
        return this.quarryInventoryData;
    }

    public static TooltipQuarryInventoryComponent of(TooltipQuarryInventoryData tooltipData) {
        return new TooltipQuarryInventoryComponent(tooltipData);
    }

    @Override
    public int getHeight() {
        return 22;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return 40 + 108;
    }

    @Override
    public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {

    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer, int z) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, QUARRY_SCREEN_BACKGROUND_TEXTURE);

        DrawableHelper.drawTexture(matrices, x, y + 2, z, 79, 26, 18, 18, 256, 256);
        this.drawItemStack(x, y + 2, this.blueprintStack, textRenderer, matrices, itemRenderer, z);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, QUARRY_SCREEN_BACKGROUND_TEXTURE);

        DrawableHelper.drawTexture(matrices, x + 20, y + 2, z, 79, 47, 18, 18, 256, 256);
        this.drawItemStack(x + 20, y + 2, this.drillStack, textRenderer, matrices, itemRenderer, z);

        for(int index = 0; index < 6; ++index) {
            int slotX = x + 40 + index * 18;
            int slotY = y + 2;

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, AUGMENTATION_CONFIGURATION_TEXTURE);

            if(index >= this.augmentLimit) {
                DrawableHelper.drawTexture(matrices, slotX, slotY, z, 100, 0, 18, 18, 256, 256);
            } else {
                DrawableHelper.drawTexture(matrices, slotX, slotY, z, 22, 25, 18, 18, 256, 256);
                this.drawAugmentSlot(slotX, slotY, index, textRenderer, matrices, itemRenderer, z);
            }

        }
    }

    private void drawItemStack(int x, int y, ItemStack stack, TextRenderer textRenderer, MatrixStack matrices, ItemRenderer itemRenderer, int z) {
        itemRenderer.renderInGuiWithOverrides(stack, x + 1, y + 1);
        itemRenderer.renderGuiItemOverlay(textRenderer, stack, x + 1, y + 1);
    }

    private void drawAugmentSlot(int x, int y, int index, TextRenderer textRenderer, MatrixStack matrices, ItemRenderer itemRenderer, int z) {
        if(index >= this.MachineUpgradesInventory.size()) return;

        ItemStack stack = this.MachineUpgradesInventory.getStack(index);
        this.drawItemStack(x, y, stack, textRenderer, matrices, itemRenderer, z);
    }
}

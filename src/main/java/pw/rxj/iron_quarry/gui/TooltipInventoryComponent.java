package pw.rxj.iron_quarry.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import oshi.util.tuples.Pair;
import pw.rxj.iron_quarry.Main;

import java.util.List;

public class TooltipInventoryComponent implements CustomTooltipComponent {
    private static final Identifier CUSTOM_SLOTS_TEXTURE = Identifier.of(Main.MOD_ID, "textures/gui/custom_slots.png");;

    public final TooltipInventoryData augmentInventoryData;
    public final List<Pair<ItemStack, Boolean>> disableableInventory;

    private TooltipInventoryComponent(TooltipInventoryData tooltipData) {
        this.augmentInventoryData = tooltipData;
        this.disableableInventory = tooltipData.getDisableableInventory();
    }

    @Override
    public CustomTooltipData getCustomTooltipData() {
        return this.augmentInventoryData;
    }

    public static TooltipInventoryComponent of(TooltipInventoryData tooltipData) {
        return new TooltipInventoryComponent(tooltipData);
    }

    @Override
    public int getHeight() {
        return 22;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return this.disableableInventory.size() * 18;
    }

    @Override
    public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {

    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        for(int index = 0; index < this.disableableInventory.size(); ++index) {
            int slotX = x + index * 18;
            int slotY = y + 2;

            context.drawTexture(CUSTOM_SLOTS_TEXTURE, slotX, slotY, 0, 0, 18, 18, 36, 36);

            this.drawSlot(slotX, slotY, index, textRenderer, context);
        }
    }

    private void drawSlot(int x, int y, int index, TextRenderer textRenderer, DrawContext context) {
        Pair<ItemStack, Boolean> slot = this.disableableInventory.get(index);
        ItemStack itemStack = slot.getA();
        Boolean disabled = slot.getB();

        context.drawItem(itemStack, x + 1, y + 1, index);
        context.drawItemInSlot(textRenderer, itemStack, x + 1, y + 1);

        if(disabled) {
            RenderSystem.disableDepthTest();
            RenderSystem.colorMask(true, true, true, false);
            context.fillGradient(x, y, x + 18, y + 18, 160, -1073741824, -1073741824);
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.enableDepthTest();
        }
    }
}

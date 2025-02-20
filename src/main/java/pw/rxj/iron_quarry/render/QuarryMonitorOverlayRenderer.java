package pw.rxj.iron_quarry.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import pw.rxj.iron_quarry.Main;
import pw.rxj.iron_quarry.event.InGameHudRenderCallback;
import pw.rxj.iron_quarry.item.BlueprintItem;
import pw.rxj.iron_quarry.item.DrillItem;
import pw.rxj.iron_quarry.item.QuarryMonitorItem;
import pw.rxj.iron_quarry.resource.ResourceReloadListener;
import pw.rxj.iron_quarry.util.ZUtil;

public class QuarryMonitorOverlayRenderer {
    public static final Identifier OVERLAY_TEXTURE = Identifier.of(Main.MOD_ID, "textures/gui/quarry_monitor_overlay.png");

    private static void renderOnScreen(MatrixStack matrices, double tickDelta) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        ClientPlayerEntity player = minecraftClient.player;
        if(player == null) return;

        PlayerInventory inventory = player.getInventory();

        QuarryMonitorItem quarryMonitorItem = null;
        ItemStack quarryMonitorStack = null;

        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack stack = inventory.getStack(slot);

            if(ZUtil.getBlockOrItem(stack) instanceof QuarryMonitorItem item) {
                quarryMonitorStack = stack;
                quarryMonitorItem = item;
                break;
            }
        }

        if(quarryMonitorStack == null) return;
        if(!quarryMonitorItem.exists(quarryMonitorStack)) return;

        float energyPct = quarryMonitorItem.getCachedEnergyPct(quarryMonitorStack);
        float outputInvPct = quarryMonitorItem.getCachedOutputInvPct(quarryMonitorStack);
        ItemStack blueprintStack = quarryMonitorItem.getCachedBlueprintStack(quarryMonitorStack);
        ItemStack drillStack = quarryMonitorItem.getCachedDrillStack(quarryMonitorStack);

        int x = 8;
        int y = minecraftClient.getWindow().getScaledHeight() - 45 - 8;

        //texture render

        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, OVERLAY_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        matrices.push();
        matrices.translate(x, y, 90.0);

        DrawableHelper.drawTexture(matrices, 0, 0, 0, 0, 0, 42, 45, 128, 128);

        if(energyPct > 0.0F) {
            int chargedPixels = Math.max(0, Math.min(39, (int) (energyPct * 39)));
            DrawableHelper.drawTexture(matrices, 3, 3 + 39 - chargedPixels, 0, 42, 3 + 39 - chargedPixels, 5, chargedPixels, 128, 128);
        }
        if(outputInvPct > 0.0F) {
            int chargedPixels = Math.max(0, Math.min(39, (int) (outputInvPct * 39)));
            DrawableHelper.drawTexture(matrices, 34, 3 + 39 - chargedPixels, 0, 47, 3 + 39 - chargedPixels, 5, chargedPixels, 128, 128);
        }

        matrices.pop();

        RenderSystem.disableBlend();

        //item render

        ItemRenderer itemRenderer = minecraftClient.getItemRenderer();
        TextRenderer textRenderer = minecraftClient.textRenderer;

        MatrixStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.push();
        modelViewStack.translate(x, y, -1.0);
        RenderSystem.applyModelViewMatrix();

        if(blueprintStack.getItem() instanceof BlueprintItem) {
            itemRenderer.renderGuiItemIcon(blueprintStack, 13, 4);
            itemRenderer.renderGuiItemOverlay(textRenderer, blueprintStack, 13, 4);
        }
        if(drillStack.getItem() instanceof DrillItem) {
            itemRenderer.renderGuiItemIcon(drillStack, 13, 25);
            itemRenderer.renderGuiItemOverlay(textRenderer, drillStack, 13, 25);
        }

        modelViewStack.pop();
        RenderSystem.applyModelViewMatrix();
    }

    public static void register() {
        ResourceReloadListener.include(OVERLAY_TEXTURE);

        InGameHudRenderCallback.START.register(QuarryMonitorOverlayRenderer::renderOnScreen);
    }
}

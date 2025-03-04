package pw.rxj.iron_quarry.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
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
import pw.rxj.iron_quarry.resource.config.client.QuarryMonitorOverlayConfig;
import pw.rxj.iron_quarry.util.ZUtil;

public class QuarryMonitorOverlayRenderer {
    private static final QuarryMonitorOverlayConfig.Handler QUARRY_MONITOR_OVERLAY_CONFIG = Main.CONFIG.getQuarryMonitorOverlayConfig();
    public static final Identifier OVERLAY_TEXTURE = Identifier.of(Main.MOD_ID, "textures/gui/quarry_monitor_overlay.png");

    private static void renderOnScreen(DrawContext context, double tickDelta) {
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

        int x = QUARRY_MONITOR_OVERLAY_CONFIG.getComputedWindowX(minecraftClient.getWindow(), 42);
        int y = QUARRY_MONITOR_OVERLAY_CONFIG.getComputedWindowY(minecraftClient.getWindow(), 45);

        //texture render

        MatrixStack matrices = context.getMatrices();

        RenderSystem.enableBlend();

        matrices.push();
        matrices.translate(x, y, 90.0);

        context.drawTexture(OVERLAY_TEXTURE, 0, 0, 0, 0, 0, 42, 45, 128, 128);

        if(energyPct > 0.0F) {
            int chargedPixels = Math.max(0, Math.min(39, (int) (energyPct * 39)));
            context.drawTexture(OVERLAY_TEXTURE, 3, 3 + 39 - chargedPixels, 0, 42, 3 + 39 - chargedPixels, 5, chargedPixels, 128, 128);
        }
        if(outputInvPct > 0.0F) {
            int chargedPixels = Math.max(0, Math.min(39, (int) (outputInvPct * 39)));
            context.drawTexture(OVERLAY_TEXTURE, 34, 3 + 39 - chargedPixels, 0, 47, 3 + 39 - chargedPixels, 5, chargedPixels, 128, 128);
        }

        matrices.pop();

        RenderSystem.disableBlend();

        //item render
        //Renders over subtitles. In versions prior to 1.20 this could be fixed by messing with the ModelViewStack,
        //but since 1.20 DrawContext#fill (f.e in BlueprintPreviewRenderer) manipulates the way
        //DrawContext#drawItem works, which could potentially mess with other mods.

        TextRenderer textRenderer = minecraftClient.textRenderer;

        if(blueprintStack.getItem() instanceof BlueprintItem) {
            context.drawItem(blueprintStack, x + 13, y + 4);
            context.drawItemInSlot(textRenderer, blueprintStack, x + 13, y + 4);
        }
        if(drillStack.getItem() instanceof DrillItem) {
            context.drawItem(drillStack, x + 13, y + 25);
            context.drawItemInSlot(textRenderer, drillStack, x + 13, y + 25);
        }
    }

    public static void register() {
        ResourceReloadListener.include(OVERLAY_TEXTURE);

        InGameHudRenderCallback.START.register(QuarryMonitorOverlayRenderer::renderOnScreen);
    }
}

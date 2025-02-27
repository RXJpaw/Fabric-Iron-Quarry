package pw.rxj.iron_quarry.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import oshi.util.tuples.Quartet;
import pw.rxj.iron_quarry.Main;
import pw.rxj.iron_quarry.block.QuarryBlock;
import pw.rxj.iron_quarry.event.InGameHudRenderCallback;
import pw.rxj.iron_quarry.item.BlueprintItem;
import pw.rxj.iron_quarry.resource.ResourceReloadListener;
import pw.rxj.iron_quarry.util.ZUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class BlueprintPreviewRenderer {
    public static final Identifier BLUEPRINT_POSITIONS_TEXTURE = Identifier.of(Main.MOD_ID, "textures/gui/blueprint_positions.png");

    private static class ScreenPosData extends Quartet<ScreenPos, Double, Vec2f, Float> {
        public ScreenPosData(ScreenPos screenPos, Double distance, Vec2f texUV, Float alpha) {
            super(screenPos, distance, texUV, alpha);
        }
    }
    private static final List<ScreenPosData> screenPosList = new ArrayList<>();

    private static void renderInWorld(WorldRenderContext context) {
        screenPosList.clear();

        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if(minecraftClient.options.hudHidden) return;
        if(minecraftClient.world == null) return;
        ClientPlayerEntity player = minecraftClient.player;
        if(player == null) return;

        final ItemStack blueprintStack;

        ItemStack stackInMainHand = player.getStackInHand(Hand.MAIN_HAND);
        ItemStack stackInOffHand = player.getStackInHand(Hand.OFF_HAND);

        if(BlueprintItem.isOf(stackInMainHand)) {
            blueprintStack = stackInMainHand;
        } else if(BlueprintItem.isOf(stackInOffHand)) {
            blueprintStack = stackInOffHand;
        } else if(ZUtil.getBlockOrItem(stackInMainHand) instanceof QuarryBlock quarryBlock) {
            ItemStack stack = quarryBlock.getBlueprintStack(stackInMainHand);
            if(stack.isEmpty()) return;
            blueprintStack = stack;
        } else if(ZUtil.getBlockOrItem(stackInOffHand) instanceof QuarryBlock quarryBlock) {
            ItemStack stack = quarryBlock.getBlueprintStack(stackInOffHand);
            if(stack.isEmpty()) return;
            blueprintStack = stack;
        } else return;

        final BlueprintItem blueprintItem = (BlueprintItem) blueprintStack.getItem();

        RegistryKey<World> worldRegistryKey = blueprintItem.getWorldRegistryKey(blueprintStack).orElse(null);
        if(!minecraftClient.world.getRegistryKey().equals(worldRegistryKey)) return;

        Camera camera = context.camera();
        MatrixStack matrices = context.matrixStack();
        Matrix4f projectionMatrix = context.projectionMatrix();

        BlockPos firstPos = blueprintItem.getFirstPos(blueprintStack).orElse(null);
        BlockPos secondPos = blueprintItem.getSecondPos(blueprintStack).orElse(null);
        prepareRenderOnScreen(matrices, firstPos, secondPos, camera, projectionMatrix);
        if(firstPos == null || secondPos == null) return;

        float tickDelta = context.tickDelta();
        int viewDistance = Math.min(minecraftClient.options.getClampedViewDistance() * 16 * 3, RenderUtil.MAX_VIEW_DISTANCE * 16 * 3);

        Vec3d lerpedPlayerPos = player.getLerpedPos(tickDelta);

        Cuboid originalCuboid = Cuboid.from(firstPos, secondPos).subtract(lerpedPlayerPos).fullblock();
        Cuboid viewDistanceCuboid = Cuboid.from(viewDistance, -viewDistance);
        Cuboid limitedCuboid = originalCuboid.limitInside(viewDistanceCuboid);
        if(limitedCuboid.isFlat()) return;

        matrices.push();

        Vec3d playerToCameraPos = lerpedPlayerPos.subtract(camera.getPos());
        matrices.translate(playerToCameraPos.x, playerToCameraPos.y, playerToCameraPos.z);

        // WorldRenderEvents.END doesn't call this when the world border is rendered.
        // Resulting in color, transparency and brightness being way off.
        RenderSystem.setShaderColor(0.976F, 0.718F, 0.192F, 0.667F);

        RenderUtil.drawZBufferedCuboid(limitedCuboid.inflate(0.02), matrices, viewDistance);

        matrices.pop();
    }

    private static void prepareRenderOnScreen(MatrixStack matrices, @Nullable BlockPos firstPos, @Nullable BlockPos secondPos, Camera camera, Matrix4f projectionMatrix) {
        if(firstPos != null) {
            Vec3d pos1 = RenderUtil.vec3dFrom(firstPos);

            screenPosList.add(new ScreenPosData(
                    RenderUtil.worldToScreen(pos1, matrices.peek().getPositionMatrix(), projectionMatrix),
                    pos1.distanceTo(camera.getPos()),
                    new Vec2f(0, 0),
                    1.0F
            ));
        }

        if(secondPos != null) {
            Vec3d pos2 = RenderUtil.vec3dFrom(secondPos);

            screenPosList.add(new ScreenPosData(
                    RenderUtil.worldToScreen(pos2, matrices.peek().getPositionMatrix(), projectionMatrix),
                    pos2.distanceTo(camera.getPos()),
                    new Vec2f(13, 0),
                    1.0F
            ));
        }

        if(firstPos != null && secondPos != null) {
            screenPosList.sort((a, b) -> Double.compare(b.getB(), a.getB()));

            ScreenPosData background = screenPosList.get(0);
            ScreenPosData foreground = screenPosList.get(1);

            float distance = (float) background.getA().distanceTo(foreground.getA());

            if(foreground.getA().isAhead() && distance < 20.0F) {
                float alpha = Math.max(Math.min(1.0F, (distance - 4.0F) / 16.0F), 0.2F);

                screenPosList.set(0, new ScreenPosData(
                        background.getA(),
                        background.getB(),
                        background.getC(),
                        alpha
                ));
            }
        }
    }

    private static void renderOnScreen(MatrixStack matrices, double tickDelta) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        TextRenderer textRenderer = minecraftClient.textRenderer;

        for (ScreenPosData pair : screenPosList) {
            ScreenPos screenPos = pair.getA();
            if(screenPos.isBehind()) continue;
            double distance = pair.getB();
            Vec2f texUV = pair.getC();
            float alpha = pair.getD();

            float scale = (float) Math.max(1.0, (3.0 / Math.pow(distance, 0.6)));
            //Adjust scale for low resolution displays that default to GUI scale 1.
            if(minecraftClient.getWindow().getScaleFactor() <= 1) scale *= 1.5F;

            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
            RenderSystem.setShaderTexture(0, BLUEPRINT_POSITIONS_TEXTURE);

            matrices.push();

            matrices.translate(screenPos.x, screenPos.y, 90.0);
            matrices.scale(scale, scale, scale);
            matrices.translate(-6, -6, 0.0);

            DrawableHelper.drawTexture(matrices, 0, 0, 0, texUV.x, texUV.y, 13, 13, 36, 36);

            matrices.pop();

            //Add 1 to shift the center, because the texture had to be offset.
            if(screenPos.add(1, 1).distanceToCenter() <= 14 * scale) {
                matrices.push();

                matrices.translate(screenPos.x, screenPos.y + 9 * scale, 90.0);

                MutableText text = Text.literal(String.format("%,.1fm", distance));
                int width = textRenderer.getWidth(text);
                int height = 8;
                scale /= 1.5F;

                matrices.scale(scale, scale, scale);
                matrices.translate(1 + width / -2.0F, 0.0, 0.0);

                DrawableHelper.fill(matrices, -2, -2, width + 1, height + 1, 1409286144);
                textRenderer.draw(matrices, text, 0, 0, new Color(1.0F, 1.0F, 1.0F, alpha).getRGB());

                matrices.pop();
            }

            RenderSystem.disableBlend();
        }
    }

    public static void register() {
        ResourceReloadListener.include(BLUEPRINT_POSITIONS_TEXTURE);

        WorldRenderEvents.END.register(BlueprintPreviewRenderer::renderInWorld);
        InGameHudRenderCallback.START.register(BlueprintPreviewRenderer::renderOnScreen);
    }
}

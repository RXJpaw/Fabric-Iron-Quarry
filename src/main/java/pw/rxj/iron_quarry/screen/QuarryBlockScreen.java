package pw.rxj.iron_quarry.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import pw.rxj.iron_quarry.Main;
import pw.rxj.iron_quarry.blockentities.QuarryBlockEntity;
import pw.rxj.iron_quarry.blocks.QuarryBlock;
import pw.rxj.iron_quarry.records.IoOption;
import pw.rxj.iron_quarry.records.TexturePosition;
import pw.rxj.iron_quarry.screenhandler.QuarryBlockScreenHandler;
import pw.rxj.iron_quarry.types.Face;
import pw.rxj.iron_quarry.types.IoState;
import pw.rxj.iron_quarry.util.ManagedSlot;
import pw.rxj.iron_quarry.util.TrackableZone;

import java.util.List;
import java.util.Optional;

public class QuarryBlockScreen extends HandledScreen<QuarryBlockScreenHandler> {
    //A path to the gui texture. In this example we use the texture from the dispenser
    private final Identifier BACKGROUND_TEXTURE = new Identifier(Main.MOD_ID, "textures/gui/quarry_block_interface.png");
    private final Identifier OPTIONS_TEXTURE = new Identifier(Main.MOD_ID, "textures/gui/options.png");
    private final Identifier OPTIONS_CONFIGURATION_TEXTURE = new Identifier(Main.MOD_ID, "textures/gui/options_configuration.png");
    private final Identifier AUGMENTATION_CONFIGURATION_TEXTURE = new Identifier(Main.MOD_ID, "textures/gui/augmentation_configuration.png");
    private final BlockPos blockPos;

    private final TrackableZone EnergyDisplay = TrackableZone.empty();
    private final TrackableZone AugmentsConfig = TrackableZone.empty()
            .onTickDelta(2, (zone, ticks) -> {
                zone.width = (int) Math.min(22 + ticks * 39, 100);
                zone.height = (int) Math.min(22 + ticks * 35, 92);
            });
    private final TrackableZone IoConfig = TrackableZone.empty()
            .onTickDelta(2, (zone, ticks) -> {
                zone.width = (int) Math.min(22 + ticks * 39, 100);
                zone.height = (int) Math.min(22 + ticks * 35, 92);
            });

    private final int expandableMenuWidth;
    private final int realBackgroundWidth;
    private final int realBackgroundHeight;

    public static final List<IoOption> IO_OPTIONS = List.of(
            new IoOption(Face.TOP, Face.TOP, 40, 24),
            new IoOption(Face.LEFT, Face.RIGHT, 20, 44),
            new IoOption(Face.FRONT, Face.FRONT, 40, 44),
            new IoOption(Face.RIGHT, Face.LEFT,60, 44),
            new IoOption(Face.BOTTOM, Face.BOTTOM, 40, 64),
            new IoOption(Face.BACK, Face.BACK, 60, 64)
    );

    public static final List<Integer> AUGMENT_SLOTS = List.of(0, 1, 2, 3, 4, 5);

    public QuarryBlockScreen(QuarryBlockScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);

        this.expandableMenuWidth = 100;
        this.realBackgroundWidth = 176;
        this.realBackgroundHeight = 224;

        this.backgroundWidth = this.realBackgroundWidth + this.expandableMenuWidth;
        this.backgroundHeight = this.realBackgroundHeight;

        this.titleY = 6;
        this.titleX = 8;
        this.playerInventoryTitleY = 131;
        this.playerInventoryTitleX = 8;

        blockPos = handler.getPos();
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        MinecraftClient MinecraftInstance = MinecraftClient.getInstance();
        if(MinecraftInstance.world == null) return;

        QuarryBlockEntity blockEntity = (QuarryBlockEntity) MinecraftInstance.world.getBlockEntity(blockPos);
        if(blockEntity == null) return;

        QuarryBlock block = blockEntity.getQuarryBlock();
        if(block == null) return;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int backgroundX = this.x;
        int backgroundY = this.y;

        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        drawTexture(matrices, backgroundX, backgroundY, 0, 0, this.realBackgroundWidth, this.realBackgroundHeight);

        //Energy-Fill-% rendering
        if(blockEntity.EnergyContainer.getStored() > 0) {
            int chargedPixels = Math.max(0, Math.min(40, (int) (blockEntity.EnergyContainer.getFillPercent() * 40)));
            drawTexture(matrices, backgroundX + 10, backgroundY + 57 - chargedPixels, 179, 57 - chargedPixels, 12, chargedPixels);
        }

        //Battery Slot rendering
        if(!handler.slots.get(7).hasStack()){
            drawTexture(matrices, backgroundX + 11, backgroundY + 63, 180, 60, 10, 14);
        }

        //Energy-Fill-% tooltip
        EnergyDisplay.consume(TrackableZone.Zone.from(backgroundX + 9, backgroundY + 16, 14, 42), mouseX, mouseY);

        if(EnergyDisplay.isMouseOver()){
            renderTooltip(matrices, Text.of(String.format("%s / %s RF", blockEntity.EnergyContainer.getStored(), blockEntity.EnergyContainer.getCapacity())), mouseX, mouseY);
        }

        //Augmentation Configuration
        int augmentsMenuX = backgroundX + this.realBackgroundWidth;
        int augmentsMenuY = backgroundY + 26;
        int augmentsMenuWidth = AugmentsConfig.zone.width;
        int augmentsMenuHeight = AugmentsConfig.zone.height;

        AugmentsConfig.consume(TrackableZone.Zone.from(augmentsMenuX, augmentsMenuY, augmentsMenuWidth, augmentsMenuHeight), mouseX, mouseY);

        if(IoConfig.isUnused() && AugmentsConfig.consumeTickDelta(delta)){
            RenderSystem.setShaderTexture(0, AUGMENTATION_CONFIGURATION_TEXTURE);
            drawTexture(matrices, augmentsMenuX, augmentsMenuY, 0, 0, augmentsMenuWidth, augmentsMenuHeight);

            for (int slotIndex : AUGMENT_SLOTS) {
                Slot slot = handler.slots.get(slotIndex);

                int bgX = slot.x + 8;
                int bgY = slot.y + 8;
                boolean slotEnabled = (augmentsMenuWidth + this.realBackgroundWidth >= bgX) && (augmentsMenuHeight + 26 >= bgY);

                if(handler.slots.get(slotIndex) instanceof ManagedSlot managedSlot) managedSlot.setEnabled(slotEnabled);
            }
        } else {
            RenderSystem.setShaderTexture(0, OPTIONS_TEXTURE);
            drawTexture(matrices, augmentsMenuX, augmentsMenuY, 0, 22, 22, 22);

            AUGMENT_SLOTS.forEach(slotIndex -> {
                if(handler.slots.get(slotIndex) instanceof ManagedSlot slot) slot.setEnabled(false);
            });
        }

        //Io Configuration
        int ioConfigX = backgroundX + this.realBackgroundWidth;
        int ioConfigY = backgroundY + 4;
        int ioConfigWidth = IoConfig.zone.width;
        int ioConfigHeight = IoConfig.zone.height;

        IoConfig.consume(TrackableZone.Zone.from(ioConfigX, ioConfigY, ioConfigWidth, ioConfigHeight), mouseX, mouseY);

        if(AugmentsConfig.isUnused() && IoConfig.consumeTickDelta(delta)){
            RenderSystem.setShaderTexture(0, OPTIONS_CONFIGURATION_TEXTURE);
            drawTexture(matrices, ioConfigX, ioConfigY, 0, 0, ioConfigWidth, ioConfigHeight);

            IO_OPTIONS.forEach(ioOption -> {
                int bgX = ioOption.bgX();
                int bgY = ioOption.bgY();
                Face face = ioOption.frontFace();
                IoState ioState = blockEntity.Configuration.getIoState(face);
                TexturePosition ioTexture = blockEntity.getIoTexturePosition(face);
                TexturePosition bgTexture = block.getTexturePosition(face, ioState != IoState.BLOCKED);

                if(ioConfigWidth >= bgX && ioConfigHeight >= bgY){
                    RenderSystem.setShaderTexture(0, block.getTextureId());
                    drawTexture(matrices,
                            ioConfigX + bgX, ioConfigY + bgY,
                            bgTexture.u(), bgTexture.v(),
                            Math.min(bgTexture.width(), ioConfigWidth - bgX), Math.min(bgTexture.height(), ioConfigHeight - bgY));
                }

                if(ioConfigWidth >= bgX + 4 && ioConfigHeight >= bgY + 4){
                    RenderSystem.setShaderTexture(0, blockEntity.getIoTextureId());
                    drawTexture(matrices,
                            ioConfigX + bgX + 4, ioConfigY + bgY + 4,
                            ioTexture.u(), ioTexture.v(),
                            Math.min(ioTexture.width(), ioConfigWidth - (bgX + 4)), Math.min(ioTexture.height(), ioConfigHeight - (bgY + 4)));

                }
            });

        } else {
            RenderSystem.setShaderTexture(0, OPTIONS_TEXTURE);
            drawTexture(matrices, ioConfigX, ioConfigY, 0, 0, 22, 22);
        }
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        super.drawForeground(matrices, mouseX, mouseY);
    }

    @Override
    protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
        if(IoConfig.isMouseOver() || AugmentsConfig.isMouseOver()) return false;

        int offsetX = 0;
        int offsetY = 0;

        int topLeftX = (width/2) - (this.realBackgroundWidth/2) - offsetX;
        int topLeftY = (height/2) - (this.realBackgroundHeight/2) - offsetY;

        int bottomRightX = (width/2) + (this.realBackgroundWidth/2) - offsetX;
        int bottomRightY = (height/2) + (this.realBackgroundHeight/2) - offsetY;

        return (mouseX < topLeftX || mouseX > bottomRightX) || (mouseY < topLeftY || mouseY > bottomRightY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        MinecraftClient MinecraftInstance = MinecraftClient.getInstance();
        if(MinecraftInstance.world == null) return false;

        QuarryBlockEntity blockEntity = (QuarryBlockEntity) MinecraftInstance.world.getBlockEntity(blockPos);
        if(blockEntity == null) return false;

        QuarryBlock block = blockEntity.getQuarryBlock();
        if(block == null) return false;

        if(this.client == null) return false;
        if(this.client.interactionManager == null) return false;
        if(MinecraftInstance.cameraEntity == null) return false;

        if(IoConfig.isUsed()) {
            IO_OPTIONS.forEach(ioOption -> {
                TrackableZone.Zone ioButtonZone = TrackableZone.Zone.from(
                        IoConfig.zone.x + ioOption.bgX(),
                        IoConfig.zone.y + ioOption.bgY(),
                        16,
                        16
                );

                TrackableZone ioButton = TrackableZone.bake(ioButtonZone, (int) mouseX, (int) mouseY);

                if(ioButton.isMouseOver()) {
                    MinecraftInstance.cameraEntity.playSound(SoundEvents.UI_BUTTON_CLICK, 0.2F, 1.0F);

                    Optional<Byte> id = QuarryBlockScreenHandler.Buttons.toByte(0, ioOption.frontFace().getId(), button);
                    if(id.isEmpty()) return;

                    this.client.interactionManager.clickButton(handler.syncId, id.get());
                }
            });
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
//        if(this.handler.slots.get(31) instanceof ManagedSlot testSlot) testSlot.setEnabled(false);

        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        this.x = (this.width - this.realBackgroundWidth) / 2;
        this.y = (this.height - this.realBackgroundHeight) / 2;
    }
}

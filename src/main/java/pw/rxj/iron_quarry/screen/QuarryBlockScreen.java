package pw.rxj.iron_quarry.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import pw.rxj.iron_quarry.Global;
import pw.rxj.iron_quarry.Main;
import pw.rxj.iron_quarry.block.QuarryBlock;
import pw.rxj.iron_quarry.blockentity.QuarryBlockEntity;
import pw.rxj.iron_quarry.compat.Compat;
import pw.rxj.iron_quarry.records.IoOption;
import pw.rxj.iron_quarry.records.TexturePosition;
import pw.rxj.iron_quarry.render.RenderUtil;
import pw.rxj.iron_quarry.types.Face;
import pw.rxj.iron_quarry.types.IoState;
import pw.rxj.iron_quarry.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuarryBlockScreen extends HandledScreen<QuarryBlockScreenHandler> {
    //A path to the gui texture. In this example we use the texture from the dispenser
    public static final Identifier BACKGROUND_TEXTURE = new Identifier(Main.MOD_ID, "textures/gui/quarry_block_interface.png");
    public static final Identifier BACKGROUND_IO_OVERLAY = new Identifier(Main.MOD_ID, "textures/gui/quarry_block_io_overlay.png");
    public static final Identifier OPTIONS_TEXTURE = new Identifier(Main.MOD_ID, "textures/gui/options.png");
    public static final Identifier OPTIONS_CONFIGURATION_TEXTURE = new Identifier(Main.MOD_ID, "textures/gui/options_configuration.png");
    public static final Identifier AUGMENTATION_CONFIGURATION_TEXTURE = new Identifier(Main.MOD_ID, "textures/gui/augmentation_configuration.png");

    private final TrackableZone InfoDisplay = TrackableZone.empty();
    private final TrackableZone EnergyDisplay = TrackableZone.empty();
    private final TrackableZone AugmentsConfig = TrackableZone.empty()
            .onTickDelta(2.5F, (zone, ticks) -> {
                zone.width = (int) Math.min(22 + ticks * 31.2F, 100);
                zone.height = (int) Math.min(22 + ticks * 28.0F, 92);
            });
    private final TrackableZone IoConfig = TrackableZone.empty()
            .onTickDelta(2.5F, (zone, ticks) -> {
                zone.width = (int) Math.min(22 + ticks * 31.2F, 100);
                zone.height = (int) Math.min(22 + ticks * 28.0F, 92);
            });

    private final int expandableMenuWidth;
    private final int realBackgroundWidth;
    private final int realBackgroundHeight;

    private final BlockPos blockPos;

    private final List<IoOption> IO_OPTIONS = List.of(
            new IoOption(Face.TOP, Face.TOP, 40, 24),
            new IoOption(Face.LEFT, Face.RIGHT, 20, 44),
            new IoOption(Face.FRONT, Face.FRONT, 40, 44),
            new IoOption(Face.RIGHT, Face.LEFT,60, 44),
            new IoOption(Face.BOTTOM, Face.BOTTOM, 40, 64),
            new IoOption(Face.BACK, Face.BACK, 60, 64)
    );
    private final List<ManagedSlot> AUGMENT_SLOTS = new ArrayList<>();
    private final ManagedSlot BLUEPRINT_SLOT;
    private final ManagedSlot DRILL_SLOT;
    private final ManagedSlot BATTERY_SLOT;

    private void drawLockedSlot(DrawContext context, Slot slot, int width, int height) {
        if(width < 0) Main.LOGGER.warn("Tried to drawLockedSlot with width < 0 ({})", width);
        if(width > 18) Main.LOGGER.warn("Tried to drawLockedSlot with width > 18 ({})", width);
        if(height < 0) Main.LOGGER.warn("Tried to drawLockedSlot with height < 0 ({})", height);
        if(height > 18) Main.LOGGER.warn("Tried to drawLockedSlot with height > 18 ({})", height);

        context.drawTexture(AUGMENTATION_CONFIGURATION_TEXTURE, this.x + slot.x - 1, this.y + slot.y - 1, 100, 0, width, height);
    }

    public QuarryBlockScreen(QuarryBlockScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);

        this.expandableMenuWidth = Compat.REI_LOADED ? 0 : 100;
        this.realBackgroundWidth = 176;
        this.realBackgroundHeight = 226;

        this.backgroundWidth = this.realBackgroundWidth + this.expandableMenuWidth;
        this.backgroundHeight = this.realBackgroundHeight;

        this.titleY = 6;
        this.titleX = 8;
        this.playerInventoryTitleY = 132;
        this.playerInventoryTitleX = 8;

        blockPos = handler.getBlockPos();

        for (int i = 0; i < 6; i++) AUGMENT_SLOTS.add((ManagedSlot) handler.getSlot(i));
        BLUEPRINT_SLOT = (ManagedSlot) handler.getSlot(6);
        DRILL_SLOT = (ManagedSlot) handler.getSlot(7);
        BATTERY_SLOT = (ManagedSlot) handler.getSlot(8);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if(minecraftClient.world == null) return;

        QuarryBlockEntity blockEntity = handler.getQuarryBlockEntity();
        if(blockEntity == null) return;

        QuarryBlock block = blockEntity.getQuarryBlock();
        if(block == null) return;

        ItemStack stack = new ItemStack(block);
        blockEntity.setStackNbt(stack);

        ComplexEnergyContainer EnergyContainer = blockEntity.EnergyContainer;
        MachineConfiguration MachineConfiguration = blockEntity.Configuration;
        MachineUpgradesUtil machineUpgradesUtil = MachineUpgradesUtil.from(block.getMachineUpgradeStacks(stack));

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int backgroundX = this.x;
        int backgroundY = this.y;

        context.drawTexture(BACKGROUND_TEXTURE, backgroundX, backgroundY, 0, 0, this.realBackgroundWidth, this.realBackgroundHeight);

        //Energy-Fill-% rendering
        if(EnergyContainer.getStored() > 0) {
            int chargedPixels = Math.max(0, Math.min(40, (int) (EnergyContainer.getFillPercent() * 40)));
            context.drawTexture(BACKGROUND_TEXTURE, backgroundX + 10, backgroundY + 58 - chargedPixels, 179, 57 - chargedPixels, 12, chargedPixels);
        }

        //Battery Slot rendering
        if(!BATTERY_SLOT.hasStack()){
            context.drawTexture(BACKGROUND_TEXTURE, backgroundX + 11, backgroundY + 64, 180, 60, 10, 14);
        }

        //Energy-Fill-% tooltip
        EnergyDisplay.consume(TrackableZone.Zone.from(backgroundX + 9, backgroundY + 17, 14, 42), mouseX, mouseY);
        EnergyDisplay.supplyLine(() -> ReadableString.translatable("item.iron_quarry.lore.energy.capacity",
                ZUtil.expandableFixedInt(EnergyContainer.getStored()),
                ZUtil.expandableFixedInt(EnergyContainer.getCapacity())
        ).setStyle(Style.EMPTY.withColor(Global.RGB_RF_PURPLE)));

        //Info tooltip
        InfoDisplay.consume(TrackableZone.Zone.from(backgroundX + titleX + this.textRenderer.getWidth(this.title) - 1, backgroundY + titleY/2, 13, 13), mouseX, mouseY);
        InfoDisplay.supplyText(() -> {
            List<Text> tooltip = new ArrayList<>();

            tooltip.add(ReadableString.translatable("screen.iron_quarry.quarry_block.title.info_display"));
            block.appendTooltip(stack, blockEntity.getWorld(), tooltip, TooltipContext.Default.BASIC);

            int conflictAmount = machineUpgradesUtil.getConflictAmount();

            return tooltip.subList(0, conflictAmount > 0 ? 5 + conflictAmount : 4);
        });

        context.drawTexture(BACKGROUND_TEXTURE, InfoDisplay.zone.x + 5, InfoDisplay.zone.y + 2, 179, 5, 3, 9);
        if(machineUpgradesUtil.getConflictAmount() > 0) {
            double alpha = Math.sin(System.currentTimeMillis() / 100.0) + 0.5;

            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, (float) alpha);

            context.drawTexture(BACKGROUND_TEXTURE, InfoDisplay.zone.x + 5, InfoDisplay.zone.y + 2, 182, 5, 3, 9);

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.defaultBlendFunc();
        }

        //Augmentation Configuration
        TrackableZone.Zone augmentsMenuZone = AugmentsConfig.zone;
        augmentsMenuZone.x = backgroundX + this.realBackgroundWidth;
        augmentsMenuZone.y = backgroundY + 26;

        AugmentsConfig.consume(augmentsMenuZone, mouseX, mouseY);

        if(IoConfig.isUnused() && AugmentsConfig.consumeTickDelta(delta)){
            int augmentsMenuX = AugmentsConfig.zone.x;
            int augmentsMenuY = AugmentsConfig.zone.y;
            int augmentsMenuWidth = AugmentsConfig.zone.width;
            int augmentsMenuHeight = AugmentsConfig.zone.height;

            context.drawTexture(AUGMENTATION_CONFIGURATION_TEXTURE, augmentsMenuX, augmentsMenuY, 0, 0, augmentsMenuWidth, augmentsMenuHeight);

            RenderUtil.runScissored(augmentsMenuX, augmentsMenuY, augmentsMenuWidth, augmentsMenuHeight, () -> {
                context.drawTextWithShadow(this.textRenderer, ReadableString.translatable("screen.iron_quarry.quarry_block.title.augmentation"), augmentsMenuX + 20, augmentsMenuY + 7, 0xFFFFFF);
            });

            for (ManagedSlot slot: AUGMENT_SLOTS) {
                int testX = augmentsMenuWidth + this.realBackgroundWidth;
                int testY = augmentsMenuHeight + 26;

                boolean slotVisible = (testX >= slot.x - 1) && (testY >= slot.y - 1);
                slot.setEnabled(slotVisible);

                if(slot.isLocked() && slotVisible) {
                    this.drawLockedSlot(context, slot, Math.min(testX - slot.x + 1, 18), Math.min(testY - slot.y + 1, 18));
                }
            }

        } else {
            context.drawTexture(OPTIONS_TEXTURE, augmentsMenuZone.x, augmentsMenuZone.y, 0, 22, 22, 22);

            AUGMENT_SLOTS.forEach(slot -> slot.setEnabled(false));
        }

        //Io Configuration
        TrackableZone.Zone ioConfigZone = IoConfig.zone;
        ioConfigZone.x = backgroundX + this.realBackgroundWidth;
        ioConfigZone.y = backgroundY + 4;

        IoConfig.consume(ioConfigZone, mouseX, mouseY);

        if(AugmentsConfig.isUnused() && IoConfig.consumeTickDelta(delta)){
            int ioConfigX = IoConfig.zone.x;
            int ioConfigY = IoConfig.zone.y;
            int ioConfigWidth = IoConfig.zone.width;
            int ioConfigHeight = IoConfig.zone.height;

            //Io Overlay
            float cappedMaxTicks = IoConfig.getMaxTicks() / 3;
            if(IoConfig.getTicks() > cappedMaxTicks) {
                float cappedTicks = IoConfig.getTicks() - cappedMaxTicks;

                RenderSystem.enableBlend();
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, cappedTicks / cappedMaxTicks);

                context.drawTexture(BACKGROUND_IO_OVERLAY, backgroundX, backgroundY, 0, 0, this.realBackgroundWidth, this.realBackgroundHeight);

                //Battery Slot rendering
                if(!BATTERY_SLOT.hasStack()){
                    context.drawTexture(BACKGROUND_IO_OVERLAY, backgroundX + 11, backgroundY + 64, 180, 60, 10, 14);
                }

                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.defaultBlendFunc();
            }

            RenderSystem.setShaderTexture(0, OPTIONS_CONFIGURATION_TEXTURE);
            context.drawTexture(OPTIONS_CONFIGURATION_TEXTURE, ioConfigX, ioConfigY, 0, 0, ioConfigWidth, ioConfigHeight);

            RenderUtil.runScissored(ioConfigX, ioConfigY, ioConfigWidth, ioConfigHeight, () -> {
                context.drawTextWithShadow(this.textRenderer, ReadableString.translatable("screen.iron_quarry.quarry_block.title.configuration"), ioConfigX + 20, ioConfigY + 7, 0xFFFFFF);
            });

            IO_OPTIONS.forEach(ioOption -> {
                int bgX = ioOption.bgX();
                int bgY = ioOption.bgY();
                Face face = ioOption.frontFace();
                IoState ioState = MachineConfiguration.getIoState(face);
                TexturePosition bgTexture = block.getTexturePosition(face, ioState != IoState.BLOCKED);
                TexturePosition ioTexture = IoState.getTexturePosition(MachineConfiguration.getIoState(face));

                if(ioConfigWidth >= bgX && ioConfigHeight >= bgY){
                    context.drawTexture(block.getTextureId(),
                            ioConfigX + bgX, ioConfigY + bgY,
                            bgTexture.u(), bgTexture.v(),
                            Math.min(bgTexture.width(), ioConfigWidth - bgX), Math.min(bgTexture.height(), ioConfigHeight - bgY));
                }

                if(ioConfigWidth >= bgX + 4 && ioConfigHeight >= bgY + 4){
                    context.drawTexture(IoState.getTextureId(),
                            ioConfigX + bgX + 4, ioConfigY + bgY + 4,
                            ioTexture.u(), ioTexture.v(),
                            Math.min(ioTexture.width(), ioConfigWidth - (bgX + 4)), Math.min(ioTexture.height(), ioConfigHeight - (bgY + 4)));

                }

                if(TrackableZone.isMouseOver(ioConfigX + bgX, ioConfigY + bgY, bgTexture.width(), bgTexture.height(), mouseX, mouseY)) {
                    drawIoHighlight(context, ioConfigX + bgX, ioConfigY + bgY);
                }
            });

        } else {
            context.drawTexture(OPTIONS_TEXTURE, ioConfigZone.x, ioConfigZone.y, 0, 0, 22, 22);
        }
    }

    @Override
    protected void drawSlot(DrawContext context, Slot slot) {
        if(AUGMENT_SLOTS.contains((ManagedSlot) slot)) {
            TrackableZone.Zone zone = AugmentsConfig.zone;
            RenderUtil.runScissored(zone.x, zone.y, zone.width, zone.height, () -> {
                super.drawSlot(context, slot);
            });
        } else {
            super.drawSlot(context, slot);
        }
    }

    public List<TrackableZone.Zone> getExclusionZones(){
        return List.of(IoConfig.zone, AugmentsConfig.zone);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        super.drawForeground(context, mouseX, mouseY);

        if(!this.handler.getCursorStack().isEmpty()) return;

        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(-this.x, -this.y, 100);

        if(EnergyDisplay.isMouseOver(mouseX, mouseY)) {
            context.drawTooltip(this.textRenderer, EnergyDisplay.getSuppliedText(), mouseX, mouseY);
        } else if(InfoDisplay.isMouseOver(mouseX, mouseY)) {
            context.drawTooltip(this.textRenderer, InfoDisplay.getSuppliedText(), mouseX, mouseY);
        } else if(BLUEPRINT_SLOT.getStack().isEmpty() && TrackableZone.isMouseOver(BLUEPRINT_SLOT, this.x, this.y, mouseX, mouseY)) {
            context.drawTooltip(this.textRenderer, ReadableString.translatable("screen.iron_quarry.quarry_block.tooltip.blueprint_info"), mouseX, mouseY);
        } else if(DRILL_SLOT.getStack().isEmpty() && TrackableZone.isMouseOver(DRILL_SLOT, this.x, this.y, mouseX, mouseY)) {
            context.drawTooltip(this.textRenderer, ReadableString.translatable("screen.iron_quarry.quarry_block.tooltip.drill_info"), mouseX, mouseY);
        } else if(BATTERY_SLOT.getStack().isEmpty() && TrackableZone.isMouseOver(BATTERY_SLOT, this.x, this.y, mouseX, mouseY)) {
            context.drawTooltip(this.textRenderer, ReadableString.translatable("screen.iron_quarry.quarry_block.tooltip.battery_info"), mouseX, mouseY);
        } else if(AugmentsConfig.isMouseOver()) {
            for (ManagedSlot slot : AUGMENT_SLOTS) {
                if(slot.isLocked() && TrackableZone.isMouseOver(slot, this.x, this.y, mouseX, mouseY)) {
                    context.drawTooltip(this.textRenderer, ReadableString.translatable("screen.iron_quarry.quarry_block.tooltip.locked_augment_slot"), mouseX, mouseY);

                    break;
                }
            }
        }

        matrices.pop();
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
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if(minecraftClient.world == null) return false;

        QuarryBlockEntity blockEntity = handler.getQuarryBlockEntity();
        if(blockEntity == null) return false;

        QuarryBlock block = blockEntity.getQuarryBlock();
        if(block == null) return false;

        if(this.client == null) return false;
        if(this.client.interactionManager == null) return false;
        if(minecraftClient.cameraEntity == null) return false;

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
                    minecraftClient.cameraEntity.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.2F, 1.0F);

                    Optional<Byte> id = QuarryBlockScreenHandler.Buttons.toByte(0, ioOption.frontFace().getId(), button);
                    if(id.isEmpty()) return;

                    this.client.interactionManager.clickButton(handler.syncId, id.get());
                }
            });
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        this.x = (this.width - this.realBackgroundWidth) / 2;
        this.y = (this.height - this.realBackgroundHeight) / 2;
    }

    public static void drawIoHighlight(DrawContext context, int x, int y) {
        RenderSystem.disableDepthTest();
        RenderSystem.colorMask(true, true, true, false);
        context.fillGradient(x, y, x + 16, y + 16, 0, 1612718112, 1612718112);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.enableDepthTest();
    }
}

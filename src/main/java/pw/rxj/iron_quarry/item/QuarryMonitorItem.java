package pw.rxj.iron_quarry.item;

import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;
import pw.rxj.iron_quarry.block.QuarryBlock;
import pw.rxj.iron_quarry.blockentity.QuarryBlockEntity;
import pw.rxj.iron_quarry.blockentity.ZBlockEntities;
import pw.rxj.iron_quarry.gui.ITooltipDataProvider;
import pw.rxj.iron_quarry.interfaces.ITickingInventoryItem;
import pw.rxj.iron_quarry.resource.config.server.QuarryMonitorConfig;
import pw.rxj.iron_quarry.util.ComplexInventory;
import pw.rxj.iron_quarry.util.QuarryInventory;
import pw.rxj.iron_quarry.util.ReadableString;
import pw.rxj.iron_quarry.util.ZUtil;

import java.util.List;
import java.util.Optional;

public class QuarryMonitorItem extends Item implements ITickingInventoryItem, ITooltipDataProvider {
    private boolean interdimensional;
    private int range;

    public QuarryMonitorItem(Settings settings, QuarryMonitorConfig.Entry entry) {
        super(settings);

        this.interdimensional = entry.interdimensional;
        this.range = entry.range;
    }
    public void override(QuarryMonitorConfig.Entry entry) {
        this.interdimensional = entry.interdimensional;
        this.range = entry.range;
    }

    @Override
    public Text getName(ItemStack stack) {
        long timestamp = this.getCachedTimestamp(stack);

        if(Screen.hasShiftDown() && timestamp > 0) {
            ItemStack quarryBlockStack = this.getCachedQuarryBlockStack(stack);

            if(!quarryBlockStack.isEmpty()) {
                return quarryBlockStack.getName();
            } else {
                return ReadableString.translatable("item.iron_quarry.quarry_monitor.name.unknown_quarry_type");
            }
        }

        return super.getName(stack);
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        ItemStack blueprintStack = this.getCachedBlueprintStack(stack);
        if(blueprintStack.isEmpty()) return super.isItemBarVisible(stack);

        return blueprintStack.isItemBarVisible();
    }
    @Override
    public int getItemBarColor(ItemStack stack) {
        ItemStack blueprintStack = this.getCachedBlueprintStack(stack);
        if(blueprintStack.isEmpty()) return super.getItemBarColor(stack);

        return blueprintStack.getItemBarColor();
    }
    @Override
    public int getItemBarStep(ItemStack stack) {
        ItemStack blueprintStack = this.getCachedBlueprintStack(stack);
        if(blueprintStack.isEmpty()) return super.getItemBarStep(stack);

        return blueprintStack.getItemBarStep();
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        ItemStack quarryBlockStack = this.getCachedQuarryBlockStack(stack);

        if(ZUtil.getBlockOrItem(quarryBlockStack) instanceof QuarryBlock quarryBlock) {
            return quarryBlock.getTooltipData(quarryBlockStack);
        }

        return Optional.empty();
    }
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        ItemStack quarryBlockStack = this.getCachedQuarryBlockStack(stack);
        long timestamp = this.getCachedTimestamp(stack);

        if(Screen.hasShiftDown() && timestamp > 0) {
            if(ZUtil.getBlockOrItem(quarryBlockStack) instanceof QuarryBlock quarryBlock) {
                quarryBlock.appendTooltip(quarryBlockStack, world, tooltip, context);
            } else {
                tooltip.add(ReadableString.translatable("item.iron_quarry.quarry_monitor.lore.status.unknown"));
            }

        } else {
            tooltip.add(ReadableString.translatable("item.iron_quarry.quarry_monitor.lore.range", ReadableString.intFrom(range)));

            if(timestamp > 0) {
                tooltip.add(ReadableString.translatable("item.iron_quarry.quarry_monitor.lore.last_updated", ReadableString.toTimeAgo(timestamp)));

                int warningFlag = this.getWarningFlag(stack);
                if(QuarryMonitorItem.hasWarnings(warningFlag)) {
                    tooltip.add(Text.empty());

                    if(QuarryMonitorItem.hasWarning(warningFlag, QuarryMonitorItem.WARNING_ENERGY_10)) {
                        tooltip.add(ReadableString.translatable("item.iron_quarry.warning.quarry_energy_10"));
                    } else if(QuarryMonitorItem.hasWarning(warningFlag, QuarryMonitorItem.WARNING_ENERGY_30)) {
                        tooltip.add(ReadableString.translatable("item.iron_quarry.warning.quarry_energy_30"));
                    }

                    if(QuarryMonitorItem.hasWarning(warningFlag, QuarryMonitorItem.WARNING_INV_90)) {
                        tooltip.add(ReadableString.translatable("item.iron_quarry.warning.quarry_inv_90"));
                    } else if(QuarryMonitorItem.hasWarning(warningFlag, QuarryMonitorItem.WARNING_INV_70)) {
                        tooltip.add(ReadableString.translatable("item.iron_quarry.warning.quarry_inv_70"));
                    }

                    if(QuarryMonitorItem.hasWarning(warningFlag, QuarryMonitorItem.WARNING_DRILL_EMPTY)) {
                        tooltip.add(ReadableString.translatable("item.iron_quarry.warning.quarry_drill_empty"));
                    }
                }

                tooltip.add(ReadableString.empty());
                tooltip.add(ReadableString.translatable("item.iron_quarry.quarry_monitor.lore.details"));
            } else {
                tooltip.add(ReadableString.translatable("item.iron_quarry.quarry_monitor.lore.never_updated"));
            }
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        if(player == null) return ActionResult.FAIL;

        if(!context.getHand().equals(Hand.MAIN_HAND)) return ActionResult.PASS;

        World world = context.getWorld();
        BlockPos targetedPos = context.getBlockPos();
        if(world.getBlockState(targetedPos).getBlock() instanceof QuarryBlock) {
            ItemStack stack = context.getStack();
            this.setPosition(stack, world, targetedPos);
            Text positionText = ReadableString.textFrom(targetedPos).orElse(ReadableString.ERROR);

            player.sendMessage(ReadableString.translatable("item.iron_quarry.quarry_monitor.overlay.pos_set", positionText), true);

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return TypedActionResult.success(user.getStackInHand(hand), false);
    }

    public void setWorld(ItemStack stack, World world) {
        this.setWorld(stack, world.getRegistryKey());
    }
    public void setWorld(ItemStack stack, RegistryKey<World> newWorldKey) {
        RegistryKey<World> oldWorldKey = this.getWorldRegistryKey(stack).orElse(null);
        if(ZUtil.equals(oldWorldKey, newWorldKey)) return;

        this.resetPosition(stack);

        String worldKey = ZUtil.toString(newWorldKey);
        stack.getOrCreateNbt().putString("World", worldKey);

    }
    public Optional<RegistryKey<World>> getWorldRegistryKey(ItemStack stack) {
        NbtCompound itemNbt = stack.getNbt();
        if(itemNbt == null) return Optional.empty();

        String worldKey = itemNbt.getString("World");
        return Optional.ofNullable(ZUtil.toRegistryKey(worldKey));
    }

    public void resetPosition(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if(nbt == null) return;

        nbt.remove("Position");
    }
    private void setPosition(ItemStack stack, @Nullable World world, BlockPos blockPos) {
        if(world != null) {
            this.setWorld(stack, world);
            blockPos = ZUtil.limitInsideWorldBounds(blockPos, world);
        } else if(this.getWorldRegistryKey(stack).isEmpty()) {
            throw new IllegalStateException("QuarryItemMonitor#setWorld has to be called first if no world is supplied.");
        }

        stack.getOrCreateNbt().put("Position", ZUtil.toBlockPosNbt(blockPos));
    }
    public BlockPos getPosition(ItemStack stack) {
        NbtCompound itemNbt = stack.getNbt();
        if(itemNbt == null) return null;

        return ZUtil.nbtToBlockPos(itemNbt.getCompound("Position"));
    }

    @Override
    public void tick(ServerPlayerEntity playerEntity, ServerWorld world, ItemStack stack) {
        MinecraftServer minecraftServer = world.getServer();

        if(ZUtil.getBlockOrItem(stack) instanceof QuarryMonitorItem quarryMonitorItem) {
            if(System.currentTimeMillis() - quarryMonitorItem.getCachedTimestamp(stack) < 1000) return;

            QuarryBlockEntity quarryBlockEntity = quarryMonitorItem.fetchQuarryBlockEntity(minecraftServer, stack);

            if(quarryBlockEntity == null) {
                quarryMonitorItem.clearCachedQuarry(stack);
            } else {
                quarryMonitorItem.setCachedQuarry(stack, quarryBlockEntity);
            }
        }
    }

    public @Nullable QuarryBlockEntity fetchQuarryBlockEntity(MinecraftServer minecraftServer, ItemStack stack) {
        RegistryKey<World> worldRegistryKey = this.getWorldRegistryKey(stack).orElse(null);
        ServerWorld serverWorld = minecraftServer.getWorld(worldRegistryKey);
        if(serverWorld == null) return null;

        BlockPos quarryPos = this.getPosition(stack);
        ChunkPos chunkPos = new ChunkPos(quarryPos.getX() >> 4, quarryPos.getZ() >> 4);

        WorldChunk worldChunk = serverWorld.getChunkManager().getWorldChunk(chunkPos.x, chunkPos.z);
        if(worldChunk == null) return null;

        Optional<QuarryBlockEntity> quarryBlockEntity = worldChunk.getBlockEntity(quarryPos, ZBlockEntities.QUARRY_BLOCK_ENTITY);
        return quarryBlockEntity.orElse(null);

    }

    public void setCachedQuarry(ItemStack stack, QuarryBlockEntity blockEntity) {
        QuarryBlock quarryBlock = blockEntity.getQuarryBlock();
        if(quarryBlock == null) return;

        NbtCompound quarryCompoundNbt = new NbtCompound();
        blockEntity.writeNbt(quarryCompoundNbt);

        NbtCompound quarryCompound = new NbtCompound();
        quarryCompound.put("nbt", quarryCompoundNbt);
        quarryCompound.putString("id", Registry.BLOCK.getId(quarryBlock).toString());
        quarryCompound.putLong("timestamp", System.currentTimeMillis());

        stack.getOrCreateNbt().put("CachedQuarry", quarryCompound);
    }

    private @Nullable NbtCompound getCachedQuarryCompound(ItemStack stack) {
        NbtCompound itemNbt = stack.getNbt();
        if(itemNbt == null) return null;

        return itemNbt.getCompound("CachedQuarry");
    }
    public boolean exists(ItemStack stack) {
        NbtCompound quarryCompound = this.getCachedQuarryCompound(stack);
        if(quarryCompound == null) return false;

        return !quarryCompound.getCompound("nbt").isEmpty();
    }
    public void clearCachedQuarry(ItemStack stack) {
        NbtCompound quarryCompound = this.getCachedQuarryCompound(stack);
        if(quarryCompound == null || quarryCompound.getCompound("nbt").isEmpty()) return;

        quarryCompound.remove("nbt");
        quarryCompound.putLong("timestamp", System.currentTimeMillis());
    }
    private @Nullable QuarryBlock getCachedQuarryBlock(NbtCompound nbtCompound) {
        if(nbtCompound == null) return null;

        Identifier quarryBlockId = Identifier.tryParse(nbtCompound.getString("id"));
        if(quarryBlockId == null) return null;

        Block block = Registry.BLOCK.getOrEmpty(quarryBlockId).orElse(null);
        if(!(block instanceof QuarryBlock quarryBlock)) return null;

        return quarryBlock;
    }
    public ItemStack getCachedQuarryBlockStack(ItemStack stack) {
        NbtCompound quarryCompound = this.getCachedQuarryCompound(stack);
        if(quarryCompound == null) return ItemStack.EMPTY;

        QuarryBlock quarryBlock = this.getCachedQuarryBlock(quarryCompound);
        if(quarryBlock == null) return ItemStack.EMPTY;

        NbtCompound quarryCompoundNbt = quarryCompound.getCompound("nbt");
        if(quarryCompoundNbt.isEmpty()) return ItemStack.EMPTY;

        ItemStack quarryBlockStack = new ItemStack(quarryBlock);
        BlockItem.setBlockEntityNbt(quarryBlockStack, ZBlockEntities.QUARRY_BLOCK_ENTITY, quarryCompoundNbt);

        return quarryBlockStack;
    }
    @Deprecated(forRemoval = true)
    // will not behave expectedly as some checks are done live, f.e. #getBlock
    private @Nullable QuarryBlockEntity getCachedQuarryBlockEntity(ItemStack stack) {
        NbtCompound quarryCompound = this.getCachedQuarryCompound(stack);
        if(quarryCompound == null) return null;

        QuarryBlock quarryBlock = this.getCachedQuarryBlock(quarryCompound);
        if(quarryBlock == null) return null;

        NbtCompound quarryCompoundNbt = quarryCompound.getCompound("nbt");
        if(quarryCompoundNbt.isEmpty()) return null;

        QuarryBlockEntity blockEntity = new QuarryBlockEntity(BlockPos.ORIGIN, quarryBlock.getDefaultState());
        blockEntity.readNbt(quarryCompoundNbt);

        return blockEntity;
    }

    public long getCachedTimestamp(ItemStack stack) {
        NbtCompound quarryCompound = this.getCachedQuarryCompound(stack);
        if(quarryCompound == null) return 0;

        return quarryCompound.getLong("timestamp");
    }
    public float getCachedEnergyPct(ItemStack stack) {
        NbtCompound quarryCompound = this.getCachedQuarryCompound(stack);
        if(quarryCompound == null) return 0.0F;

        QuarryBlock quarryBlock = this.getCachedQuarryBlock(quarryCompound);
        if(quarryBlock == null) return 0.0F;

        return (float) quarryCompound.getCompound("nbt").getCompound("rxj.pw/Energy").getLong("Stored") / quarryBlock.getEnergyCapacity();
    }
    public float getCachedOutputInvPct(ItemStack stack) {
        NbtCompound quarryCompound = this.getCachedQuarryCompound(stack);
        if(quarryCompound == null) return 0.0F;

        ComplexInventory OutputInventory = QuarryInventory.Output.noRef();
        NbtList OutputInventoryList = quarryCompound.getCompound("nbt").getCompound("rxj.pw/Storage").getCompound("OutputInventory").getList("Items", NbtElement.COMPOUND_TYPE);

        return (float) OutputInventoryList.size() / OutputInventory.size();
    }
    public ItemStack getCachedBlueprintStack(ItemStack stack) {
        NbtCompound quarryCompound = this.getCachedQuarryCompound(stack);
        if(quarryCompound == null) return ItemStack.EMPTY;

        NbtList BlueprintInventoryList = quarryCompound.getCompound("nbt").getCompound("rxj.pw/Storage").getCompound("BlueprintInventory").getList("Items", NbtElement.COMPOUND_TYPE);

        return ItemStack.fromNbt(BlueprintInventoryList.getCompound(0));
    }
    public ItemStack getCachedDrillStack(ItemStack stack) {
        NbtCompound quarryCompound = this.getCachedQuarryCompound(stack);
        if(quarryCompound == null) return ItemStack.EMPTY;

        NbtList DrillInventoryList = quarryCompound.getCompound("nbt").getCompound("rxj.pw/Storage").getCompound("DrillInventory").getList("Items", NbtElement.COMPOUND_TYPE);

        return ItemStack.fromNbt(DrillInventoryList.getCompound(0));
    }

    //warnings

    public static final int WARNING_INV_70 = 0;
    public static final int WARNING_INV_90 = 1;
    public static final int WARNING_ENERGY_30 = 2;
    public static final int WARNING_ENERGY_10 = 3;
    public static final int WARNING_DRILL_EMPTY = 4;

    public static boolean hasWarnings(int flag) {
        return flag != 0;
    }
    public static boolean hasWarning(int flag, int warning) {
        warning = 1 << warning;

        return (flag & warning) == warning;
    }

    public boolean[] getWarningList(ItemStack stack) {
        float invPct = this.getCachedOutputInvPct(stack);
        float energyPct = this.getCachedEnergyPct(stack);
        boolean hasDrillStack = this.getCachedDrillStack(stack).getItem() instanceof DrillItem;

        return new boolean[] { invPct >= 0.7F, invPct >= 0.9F, energyPct <= 0.3F, energyPct <= 0.1F, !hasDrillStack };
    }
    public int getWarningAmount(ItemStack stack) {
        int amount = 0;

        for (boolean hasConflict : this.getWarningList(stack)) {
            if(hasConflict) amount++;
        }

        return amount;
    }
    public int getWarningFlag(ItemStack stack) {
        boolean[] list = this.getWarningList(stack);
        int flag = 0;

        if(list[WARNING_INV_70]) flag += 1 << WARNING_INV_70;
        if(list[WARNING_INV_90]) flag += 1 << WARNING_INV_90;
        if(list[WARNING_ENERGY_30]) flag += 1 << WARNING_ENERGY_30;
        if(list[WARNING_ENERGY_10]) flag += 1 << WARNING_ENERGY_10;
        if(list[WARNING_DRILL_EMPTY]) flag += 1 << WARNING_DRILL_EMPTY;

        return flag;
    }
}

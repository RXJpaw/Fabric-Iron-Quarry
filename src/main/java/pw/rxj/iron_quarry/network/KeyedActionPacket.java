package pw.rxj.iron_quarry.network;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pw.rxj.iron_quarry.Main;
import pw.rxj.iron_quarry.interfaces.IHandledKeyedAction;
import pw.rxj.iron_quarry.network.packet.StringByteByteBlockHitResultPacket;
import pw.rxj.iron_quarry.types.ActionGoal;
import pw.rxj.iron_quarry.util.ZUtil;

public class KeyedActionPacket extends ComplexPacketHandler<StringByteByteBlockHitResultPacket> {
    protected static KeyedActionPacket INSTANCE = new KeyedActionPacket();

    private KeyedActionPacket() { }

    @Override
    public Identifier getChannelId() {
        return Identifier.of(Main.MOD_ID, "key_press_action");
    }

    @Override
    public @Nullable StringByteByteBlockHitResultPacket read(PacketByteBuf buf) {
        return StringByteByteBlockHitResultPacket.read(buf);
    }

    @Override
    protected void receiveFromClient(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, @NotNull StringByteByteBlockHitResultPacket packet, PacketSender response) {
        Hand hand = ZUtil.safeEnumConstant(Hand.class, packet.byte0);
        if(hand == null) return;
        ActionGoal actionGoal = ZUtil.safeEnumConstant(ActionGoal.class, packet.byte1);
        if(actionGoal == null) return;

        String keyName = packet.string;
        BlockHitResult hitResult = packet.hitResult;

        ItemUsageContext context = new ItemUsageContext(player, hand, hitResult);

        switch(actionGoal) {
            case USE -> {
                ItemStack stack = player.getStackInHand(hand);

                if(ZUtil.getBlockOrItem(stack) instanceof IHandledKeyedAction handledKeyedAction) {
                    handledKeyedAction.keyedUse(keyName, context);
                }
            }
            case USE_ON_BLOCK -> {
                if(hitResult.getType().equals(HitResult.Type.MISS)) break;

                BlockState blockState = context.getWorld().getBlockState(hitResult.getBlockPos());

                if(blockState.getBlock() instanceof IHandledKeyedAction handledKeyedAction) {
                    handledKeyedAction.keyedUseOnBlock(keyName, context, blockState);
                }
            }
            case ATTACK_ON_BLOCK -> {
                if(hitResult.getType().equals(HitResult.Type.MISS)) break;

                BlockState blockState = context.getWorld().getBlockState(hitResult.getBlockPos());

                if(blockState.getBlock() instanceof IHandledKeyedAction handledKeyedAction) {
                    handledKeyedAction.keyedAttackOnBlock(keyName, context, blockState);
                }
            }
        }
    }

    public static PacketByteBuf bake(String keyName, Hand hand, ActionGoal actionGoal, BlockHitResult hitResult) {
        return StringByteByteBlockHitResultPacket.write(INSTANCE.getChannelId(), keyName, (byte) hand.ordinal(), (byte) actionGoal.ordinal(), hitResult);
    }
}

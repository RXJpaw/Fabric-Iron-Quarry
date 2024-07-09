package pw.rxj.iron_quarry.network.packet;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;

public class StringByteByteBlockHitResultPacket {
    public final String string;
    public final byte byte0;
    public final byte byte1;
    public final BlockHitResult hitResult;

    private StringByteByteBlockHitResultPacket(String string, Byte byte0, Byte byte1, BlockHitResult hitResult) {
        this.string = string;
        this.byte0 = byte0;
        this.byte1 = byte1;
        this.hitResult = hitResult;
    }

    public static StringByteByteBlockHitResultPacket read(PacketByteBuf buf) {
        try {
            String string = buf.readString();
            Byte byte0 = buf.readByte();
            Byte byte1 = buf.readByte();
            BlockHitResult hitResult = buf.readBlockHitResult();

            return new StringByteByteBlockHitResultPacket(string, byte0, byte1, hitResult);
        } catch(Exception ignored) {
            return null;
        }
    }
    public static PacketByteBuf write(Identifier channel, String string, Byte byte0, Byte byte1, BlockHitResult hitResult) {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
        packet.writeIdentifier(channel);
        packet.writeString(string);
        packet.writeByte(byte0);
        packet.writeByte(byte1);
        packet.writeBlockHitResult(hitResult);

        return packet;
    }
}

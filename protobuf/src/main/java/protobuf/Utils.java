package protobuf;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import protobuf.analysis.ParseMap;
import protobuf.generate.internal.Internal;

/**
 * @author huangweidong
 */
public class Utils {
    public static ByteBuf pack2Server(Message msg, int ptoNum, long netId, Internal.Dest dest, String userId) {
        Internal.GTransfer.Builder gtf = Internal.GTransfer.newBuilder();
        gtf.setPtoNum(ptoNum);
        gtf.setMsg(msg.toByteString());
        gtf.setNetId(netId);
        gtf.setDest(dest);
        gtf.setUserId(userId);

        byte[] bytes = gtf.build().toByteArray();
        int length = bytes.length;
        int gtfNum = ParseRegistryMap.GTRANSFER;
        ByteBuf buf = Unpooled.buffer(8 + length);
        buf.writeInt(length);
        //传输协议的协议号
        buf.writeInt(gtfNum);
        buf.writeBytes(bytes);
        return buf;
    }

    public static ByteBuf pack2Client(Message msg) {
        byte[] bytes = msg.toByteArray();
        int length = bytes.length;
        int ptoNum = ParseMap.getPtoNum(msg);

        ByteBuf buf = Unpooled.buffer(8 + length);
        buf.writeInt(length);
        buf.writeInt(ptoNum);
        buf.writeBytes(bytes);

        return buf;
    }

    public static ByteBuf pack2Server(Message msg, int ptoNum, Internal.Dest dest, String userId) {
        Internal.GTransfer.Builder gtf = Internal.GTransfer.newBuilder();
        gtf.setPtoNum(ptoNum);
        gtf.setMsg(msg.toByteString());
        gtf.setDest(dest);
        gtf.setUserId(userId);

        byte[] bytes = gtf.build().toByteArray();
        int length = bytes.length;
        int gtfNum = ParseRegistryMap.GTRANSFER;

        ByteBuf buf = Unpooled.buffer(8 + length);
        buf.writeInt(length);
        //传输协议的协议号
        buf.writeInt(gtfNum);
        buf.writeBytes(bytes);

        return buf;
    }
}

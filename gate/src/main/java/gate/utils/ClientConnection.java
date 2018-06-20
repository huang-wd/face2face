package gate.utils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 客户端连接的封装类
 *
 * @author huangweidong
 */
public class ClientConnection {

    public static AttributeKey<Long> NET_ID = AttributeKey.valueOf("net_id");

    private static final AtomicLong netIdGenerator = new AtomicLong(0);

    private String _userId;

    private Long _netId;

    private ChannelHandlerContext _ctx;

    ClientConnection(ChannelHandlerContext c) {
        _netId = netIdGenerator.incrementAndGet();
        _ctx = c;
        _ctx.attr(ClientConnection.NET_ID).set(_netId);
    }

    public Long getNetId() {
        return _netId;
    }

    public String getUserId() {
        return _userId;
    }

    public void setUserId(String userId) {
        _userId = userId;
    }

    public ChannelHandlerContext getCtx() {
        return _ctx;
    }
}

package auth;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import org.apache.thrift.TException;
import redis.clients.jedis.Jedis;

/**
 * @author huangweidong
 */
public abstract class AbstractImHandler {
    protected final String _userId;
    protected final Long _netId;
    protected final Message _msg;
    protected ChannelHandlerContext _ctx;
    protected Jedis _jedis;

    protected AbstractImHandler(String userId, Long netId, Message msg, ChannelHandlerContext ctx) {
        _userId = userId;
        _netId = netId;
        _msg = msg;
        _ctx = ctx;
    }

    /**
     * execute
     *
     * @param worker
     * @throws TException
     */
    protected abstract void execute(Worker worker) throws TException;
}

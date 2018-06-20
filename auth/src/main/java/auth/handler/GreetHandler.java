package auth.handler;

import auth.AbstractImHandler;
import auth.Worker;
import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author huangweidong
 */
public class GreetHandler extends AbstractImHandler {
    private static final Logger logger = LoggerFactory.getLogger(GreetHandler.class);

    public GreetHandler(String userId, long netId, Message msg, ChannelHandlerContext ctx) {
        super(userId, netId, msg, ctx);
    }

    @Override
    protected void execute(Worker worker) {
        AuthServerHandler.setGateAuthConnection(_ctx);
        logger.info("[Gate-Auth] connection is established");
    }
}

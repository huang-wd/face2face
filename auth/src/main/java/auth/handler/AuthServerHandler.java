package auth.handler;

import auth.HandlerManager;
import auth.AbstractImHandler;
import auth.Worker;
import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protobuf.analysis.ParseMap;
import protobuf.generate.internal.Internal;

import java.util.HashMap;

/**
 * @author huangweidong
 */
public class AuthServerHandler extends SimpleChannelInboundHandler<Message> {
    private static final Logger logger = LoggerFactory.getLogger(AuthServerHandler.class);

    private static HashMap<String, Long> userId2NetIdMap = new HashMap<>();

    private static ChannelHandlerContext _gateAuthConnection;

    public static void setGateAuthConnection(ChannelHandlerContext ctx) {
        _gateAuthConnection = ctx;
    }

    public static ChannelHandlerContext getGateAuthConnection() {
        if (_gateAuthConnection != null) {
            return _gateAuthConnection;
        } else {
            return null;
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {
        Internal.GTransfer gt = (Internal.GTransfer) message;
        int ptoNum = gt.getPtoNum();
        Message msg = ParseMap.getMessage(ptoNum, gt.getMsg().toByteArray());

        AbstractImHandler handler;
        if (msg instanceof Internal.Greet) {
            //来自gate的连接请求
            handler = HandlerManager.getHandler(ptoNum, gt.getUserId(), gt.getNetId(), msg, channelHandlerContext);
        } else {
            handler = HandlerManager.getHandler(ptoNum, gt.getUserId(), gt.getNetId(), msg, getGateAuthConnection());
        }
        Worker.dispatch(gt.getUserId(), handler);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        //super.exceptionCaught(ctx, cause);
        logger.error("An Exception Caught");
    }

    public static void putInUserIdMap(String userId, Long netId) {
        userId2NetIdMap.put(userId, netId);
    }

    public static Long getNetIdByUserId(String userId) {
        Long netId = userId2NetIdMap.get(userId);
        if (netId != null) {
            return netId;
        } else {
            return null;
        }
    }
}

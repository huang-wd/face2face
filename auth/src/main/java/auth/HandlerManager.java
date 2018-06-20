package auth;


import auth.handler.*;
import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protobuf.analysis.ParseMap;
import protobuf.generate.cli2srv.chat.Chat;
import protobuf.generate.cli2srv.login.Auth;
import protobuf.generate.internal.Internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author huangweidong
 */
public class HandlerManager {
    private static final Logger logger = LoggerFactory.getLogger(HandlerManager.class);

    private static final Map<Integer, Constructor<? extends AbstractImHandler>> _handlers = new HashMap<>();

    public static void register(Class<? extends Message> msg, Class<? extends AbstractImHandler> handler) {
        int num = ParseMap.getPtoNum(msg);
        try {
            Constructor<? extends AbstractImHandler> constructor = handler.getConstructor(String.class, Long.class, Message.class, ChannelHandlerContext.class);
            _handlers.put(num, constructor);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static AbstractImHandler getHandler(int ptoNum, String userId, Long netId, Message msg, ChannelHandlerContext ctx) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<? extends AbstractImHandler> constructor = _handlers.get(ptoNum);
        if (constructor == null) {
            logger.error("handler not exist, Message Number: {}", ptoNum);
            return null;
        }
        return constructor.newInstance(userId, netId, msg, ctx);
    }

    public static void initHandlers() {
        HandlerManager.register(Internal.Greet.class, GreetHandler.class);
        HandlerManager.register(Auth.CLogin.class, CLoginHandlerAbstract.class);
        HandlerManager.register(Auth.CRegister.class, CRegisterHandlerAbstract.class);
        HandlerManager.register(Chat.CPrivateChat.class, CPrivateChatHandlerAbstract.class);
    }
}

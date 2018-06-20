package gate;

import com.google.protobuf.Message;
import gate.handler.GateAuthConnectionHandler;
import gate.handler.GateLogicConnectionHandler;
import gate.utils.ClientConnection;
import gate.utils.ClientConnectionMap;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protobuf.Utils;
import protobuf.analysis.ParseMap;
import protobuf.generate.cli2srv.chat.Chat;
import protobuf.generate.cli2srv.login.Auth;
import protobuf.generate.internal.Internal;

import java.io.IOException;
import java.util.HashMap;


/**
 * @author huangweidong
 */
public class ClientMessage {
    private static final Logger logger = LoggerFactory.getLogger(ClientMessage.class);

    public static HashMap<Integer, Transfer> transferHandlerMap = new HashMap<>();
    public static HashMap<Class<?>, Integer> msg2PtoNum = new HashMap<>();

    @FunctionalInterface
    public interface Transfer {
        /**
         * process
         *
         * @param msg
         * @param conn
         * @throws IOException
         */
        void process(Message msg, ClientConnection conn) throws IOException;
    }

    public static void registerTransferHandler(Integer ptoNum, Transfer transfer, Class<?> cla) {
        if (transferHandlerMap.get(ptoNum) == null) {
            transferHandlerMap.put(ptoNum, transfer);
        } else {
            logger.error("pto has been registered in transferHandlerMap, ptoNum: {}", ptoNum);
            return;
        }

        if (msg2PtoNum.get(cla) == null) {
            msg2PtoNum.put(cla, ptoNum);
        } else {
            logger.error("pto has been registered in msg2PtoNum, ptoNum: {}", ptoNum);
            return;
        }
    }

    public static void processTransferHandler(Message msg, ClientConnection conn) throws IOException {
        logger.info("MessageName {}", msg.getClass());
        int ptoNum = msg2PtoNum.get(msg.getClass());
        Transfer transferHandler = transferHandlerMap.get(ptoNum);

        if (transferHandler != null) {
            transferHandler.process(msg, conn);
        }
    }

    public static void transfer2Logic(Message msg, ClientConnection conn) {
        ByteBuf byteBuf = null;
        if (conn.getUserId() == null) {
            logger.error("User not login.");
            return;
        }

        if (msg instanceof Chat.CPrivateChat) {
            byteBuf = Utils.pack2Server(msg, ParseMap.getPtoNum(msg), conn.getNetId(), Internal.Dest.Logic, conn.getUserId());
        }

        GateLogicConnectionHandler.getGateLogicConnection().writeAndFlush(byteBuf);
    }

    public static void transfer2Auth(Message msg, ClientConnection conn) {
        ByteBuf byteBuf = null;
        if (msg instanceof Auth.CLogin) {
            String userId = ((Auth.CLogin) msg).getUserid();
            byteBuf = Utils.pack2Server(msg, ParseMap.getPtoNum(msg), conn.getNetId(), Internal.Dest.Auth, userId);
            ClientConnectionMap.registerUserid(userId, conn.getNetId());
        } else if (msg instanceof Auth.CRegister) {
            byteBuf = Utils.pack2Server(msg, ParseMap.getPtoNum(msg), conn.getNetId(), Internal.Dest.Auth, ((Auth.CRegister) msg).getUserid());
        }

        GateAuthConnectionHandler.getGateAuthConnection().writeAndFlush(byteBuf);

    }
}

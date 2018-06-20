package gate.utils;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author huangweidong
 */
public class ClientConnectionMap {
    private static final Logger logger = LoggerFactory.getLogger(ClientConnectionMap.class);

    /**
     * 保存一个gateway上所有的客户端连接
     */
    public static ConcurrentHashMap<Long, ClientConnection> allClientMap = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<String, Long> userId2NetIdMap = new ConcurrentHashMap<>();

    public static ClientConnection getClientConnection(ChannelHandlerContext ctx) {
        Long netId = ctx.attr(ClientConnection.NET_ID).get();
        ClientConnection conn = allClientMap.get(netId);
        if (conn != null) {
            return conn;
        } else {
            logger.error("ClientConnection not found in allClientMap, netId: {}", netId);
        }
        return null;
    }

    public static ClientConnection getClientConnection(long netId) {
        ClientConnection conn = allClientMap.get(netId);
        if (conn != null) {
            return conn;
        } else {
            logger.error("ClientConnection not found in allClientMap, netId: {}", netId);
        }
        return null;
    }

    public static void addClientConnection(ChannelHandlerContext c) {
        //fixme 之后重复登录需要踢掉原来的连接
        ClientConnection conn = new ClientConnection(c);
        if (ClientConnectionMap.allClientMap.putIfAbsent(conn.getNetId(), conn) != null) {
            logger.error("Duplicated netId");
        }
    }

    public static void removeClientConnection(ChannelHandlerContext c) {
        ClientConnection conn = getClientConnection(c);
        Long netId = conn.getNetId();
        String userId = conn.getUserId();
        if (ClientConnectionMap.allClientMap.remove(netId) != null) {
            unRegisterUserId(userId);
        } else {
            logger.error("NetId: {} is not exist in allClientMap", netId);
        }
        logger.info("Client disconnected, netId: {}, userId: {}", netId, userId);
    }

    public static void registerUserid(String userid, long netId) {
        if (userId2NetIdMap.putIfAbsent(userid, netId) == null) {
            ClientConnection conn = ClientConnectionMap.getClientConnection(netId);
            if (conn != null) {
                conn.setUserId(userid);
            } else {
                logger.error("ClientConnection is null");
                return;
            }
        } else {
            logger.error("UserId: {} has registered in userId2NetIdMap", userid);
        }
    }

    protected static void unRegisterUserId(String userId) {
        if (ClientConnectionMap.userId2NetIdMap.remove(userId) == null) {
            logger.error("UserId: {} is not exist in userId2NetIdMap", userId);
        }
    }

    public static Long userId2NetId(String userId) {
        Long netId = userId2NetIdMap.get(userId);
        if (netId != null) {
            return netId;
        } else {
            logger.error("User not login, userId: {}", userId);
        }
        return null;
    }
}

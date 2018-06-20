package gate;

import protobuf.generate.cli2srv.chat.Chat;
import protobuf.generate.cli2srv.login.Auth;

import java.io.IOException;

/**
 * @author huangweidong
 */
public class TransferHandlerMap {
    public static void initRegistry() {
        ClientMessage.registerTransferHandler(1000, ClientMessage::transfer2Auth, Auth.CLogin.class);
        ClientMessage.registerTransferHandler(1001, ClientMessage::transfer2Auth, Auth.CRegister.class);
        ClientMessage.registerTransferHandler(1003, ClientMessage::transfer2Logic, Chat.CPrivateChat.class);
    }
}

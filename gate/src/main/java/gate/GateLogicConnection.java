package gate;

import gate.handler.GateLogicConnectionHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protobuf.code.PacketDecoder;
import protobuf.code.PacketEncoder;

/**
 * @author huangweidong
 */
public class GateLogicConnection {
    public static void startGateLogicConnection(String ip, int port) {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast("MessageDecoder", new PacketDecoder());
                        pipeline.addLast("MessageEncoder", new PacketEncoder());
                        //gate -> logic
                        pipeline.addLast("GateLogicConnectionHandler", new GateLogicConnectionHandler());
                    }
                });

        bootstrap.connect(ip, port);
    }
}

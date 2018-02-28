package com.liqiwei.server.netty.websocket;

import com.liqiwei.server.netty.NettyServer;
import com.liqiwei.server.util.Config;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * websocket长连接服务器
 * 启动入门
 */
public class WebsocketServer implements NettyServer{

    private static final Logger LOGGER = LoggerFactory.getLogger(WebsocketServer.class);
    private static final int PORT = Config.WEBSOCKET_SERVER_PORT;

    @Override
    public void start() {

        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .childHandler(new WebSocketServerInitializer())  //(4)
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)
            LOGGER.info("启动websocket服务，端口：{}", PORT);

            // 绑定端口，开始接收进来的连接
            ChannelFuture f = b.bind(PORT).sync(); // (7)

            // 等待服务器  socket 关闭 。
            f.channel().closeFuture().sync();
        }
        catch(Exception e){
            LOGGER.error(e.getMessage(), e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }

}

package com.liqiwei.server.netty.http;

import com.liqiwei.server.netty.NettyServer;
import com.liqiwei.server.netty.http.handle.HttpServerInboundHandler;
import com.liqiwei.server.netty.http.router.HttpRouteParse;
import com.liqiwei.server.util.Config;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  http 服务器
 *
 */
public class HttpServer implements NettyServer{

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);
    private static final int PORT = Config.HTTP_SERVER_PORT;

    @Override
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        final HttpRouteParse route = new HttpRouteParse("com.liqiwei.server.business.api");
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class) // (3)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            // server端发送的是httpResponse，所以要使用HttpResponseEncoder进行编码
                            ch.pipeline().addLast(new HttpResponseEncoder());
                            // server端接收到的是httpRequest，所以要使用HttpRequestDecoder进行解码
                            ch.pipeline().addLast(new HttpRequestDecoder());
                            ch.pipeline().addLast(new HttpServerInboundHandler(route));
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128) // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            LOGGER.info("启动http服务，端口：{}",PORT);

            ChannelFuture f = b.bind(PORT).sync(); // (7)
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}

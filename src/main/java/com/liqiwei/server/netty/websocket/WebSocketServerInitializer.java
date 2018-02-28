package com.liqiwei.server.netty.websocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebSocketServerInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();
          
        pipeline.addLast(new HttpServerCodec());
          
        pipeline.addLast(new ChunkedWriteHandler());

        pipeline.addLast(new HttpObjectAggregator(64*1024));

        pipeline.addLast(new HttpRequestHandler("/nettyserver/command/scmd"));

        pipeline.addLast(new WebSocketServerProtocolHandler("/nettyserver/command/scmd"));

        pipeline.addLast(new TextWebSocketFrameHandler());


    }
}
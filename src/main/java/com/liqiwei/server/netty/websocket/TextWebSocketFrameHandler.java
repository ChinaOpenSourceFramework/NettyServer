package com.liqiwei.server.netty.websocket;

import com.liqiwei.server.business.commend.ScmdWebsocketHandler;
import com.liqiwei.server.netty.websocket.attr.WsChannelAttrKey;
import com.liqiwei.server.netty.websocket.attr.WsRequestInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.concurrent.ImmediateEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Websocket Handler 处理请求
 */
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextWebSocketFrameHandler.class);
    private final ChannelGroup group;

    public TextWebSocketFrameHandler(){
        group = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);//通道组
        ScmdWebsocketHandler.getInstance().setChannelGroup(group);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        //加解密
        ScmdWebsocketHandler.getInstance().dispatchWebsocketMessage(msg.text());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //握手成功，连接建立，否则交由父类默认处理不符合协议的通讯
        if(evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE){
            //连接验证
            group.add(ctx.channel());
            ctx.pipeline().remove(HttpRequestHandler.class);
            //分发ws连接初始化事件
            WsRequestInfo ws = ctx.channel().attr(WsChannelAttrKey.WS_REQUEST_ATTR_KEY).get();
            if(ws != null){
                ScmdWebsocketHandler.getInstance().dispatchWebsocketOpen(ctx.channel());
            }
        } else{
            super.userEventTriggered(ctx, evt);
        }

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        group.remove(ctx.channel());
        ScmdWebsocketHandler.getInstance().dispatchWebsocketClose(ctx.channel());
        super.handlerRemoved(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        group.remove(ctx.channel());
        ScmdWebsocketHandler.getInstance().dispatchWebsocketClose(ctx.channel());
        super.exceptionCaught(ctx, cause);
    }
}

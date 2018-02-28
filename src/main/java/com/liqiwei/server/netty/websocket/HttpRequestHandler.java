package com.liqiwei.server.netty.websocket;

import com.liqiwei.server.netty.websocket.attr.WsChannelAttrKey;
import com.liqiwei.server.netty.websocket.attr.WsRequestInfo;
import com.liqiwei.server.util.HttpUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * websocket的参数获取 
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final String wsUri;//执行websocket连接的url

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestHandler.class);

    public HttpRequestHandler(String wsUri) {
        this.wsUri = wsUri;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        //客户端请求的url如果匹配(完全一致，排除了参数，此处需要修改支持参数)，传递到websocket处理器处理，否则按照常规http请求进行响应
        if(wsUri.equalsIgnoreCase(HttpUtils.getUriWithoutParameter(msg.uri()))){
            //传递到下一个handler进行处理(websocket)
            LOGGER.info("websocket target request: {}",msg.uri());
            //共享channel数据(封装请求地址、参数、创建时间等信息)
            ctx.channel().attr(WsChannelAttrKey.WS_REQUEST_ATTR_KEY).set(new WsRequestInfo(msg.uri()));;
            //重置uri，移除uri多余的参数信息，否则不会被websocket处理(此处可以做转发处理)
            msg.setUri(wsUri);
            ctx.fireChannelRead(msg.retain());
        }else{

            if(HttpUtil.is100ContinueExpected(msg)){
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
                ctx.writeAndFlush(response);
            }else {
                String result = "{\"code\":1000,\"message\":\"error\"}";
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,  Unpooled.copiedBuffer(result.getBytes("utf-8")));
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
                ctx.write(response);
                ctx.flush();
            }
        }

    }
}

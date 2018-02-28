package com.liqiwei.server.netty.http.handle;

import com.liqiwei.server.netty.http.router.HttpRouteParse;
import com.liqiwei.server.netty.http.wrapper.HttpRequestWrapper;
import com.liqiwei.server.netty.http.wrapper.HttpResponseWrapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * http 处理解析
 */
public class HttpServerInboundHandler extends SimpleChannelInboundHandler<Object> {
    private HttpRequest request;
    private final StringBuilder contentBuf = new StringBuilder();
    private final Map<String, Object> dataMap = new HashMap<String, Object>();
    private HttpRouteParse routeParse ;
    public HttpServerInboundHandler(HttpRouteParse route) {
        this.routeParse = route;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            request = (HttpRequest) msg;
            if (HttpUtil.is100ContinueExpected(request)) {
                send100Continue(ctx);
            }
            contentBuf.setLength(0);

            dataMap.clear();
            dataMap.put("REQUEST_URI", request.uri().substring(0, request.uri().indexOf("?") == -1 ? request.uri().length() : request.uri().indexOf("?")));
            dataMap.put("VERSION", request.protocolVersion().toString());
            dataMap.put("HOSTNAME", request.headers().get(HttpHeaderNames.HOST, "unknown"));
            dataMap.put("METHOD", request.method().name());

            HttpHeaders headers = request.headers();
            if (!headers.isEmpty()) {
                for (Map.Entry<String, String> h: headers) {
                    CharSequence key = h.getKey();
                    CharSequence value = h.getValue();
                    dataMap.put(key.toString(), value.toString());
                }
            }

            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
            dataMap.put("PARAMS", queryStringDecoder.parameters());
            appendDecoderResult(contentBuf, request);

        }

        if (msg instanceof HttpContent) {

            HttpContent httpContent = (HttpContent) msg;

            ByteBuf content = httpContent.content();
            if (content.isReadable()) {
                contentBuf.append(content.toString(CharsetUtil.UTF_8));
                appendDecoderResult(contentBuf, request);
            }

            if (msg instanceof LastHttpContent) {
                dataMap.put("CONTENT", contentBuf.toString());
                LastHttpContent trailer = (LastHttpContent) msg;

                //路由器处理
                preResponse(trailer, ctx);
            }
        }

    }

    /**
     * 预响应处理
     */
    private void preResponse(HttpObject currentObj, ChannelHandlerContext ctx){
        HttpResponseWrapper response = new HttpResponseWrapper();
        try {
            routeParse.dispatch(HttpRequestWrapper.wrapper(dataMap), response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //输出
            if (!doResponse(response, currentObj, ctx)) {
                ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
        super.channelReadComplete(ctx);
    }

    private static void appendDecoderResult(StringBuilder buf, HttpObject o) {
        DecoderResult result = o.decoderResult();
        if (result.isSuccess()) {
            return;
        }
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.write(response);
    }


    /**
     * 执行响应
     *
     * @param httpResponse
     * @param currentObj
     * @param ctx
     * @return
     */
    private boolean doResponse(HttpResponseWrapper httpResponse, HttpObject currentObj, ChannelHandlerContext ctx){
        // Decide whether to close the connection or not.
        boolean keepAlive = HttpUtil.isKeepAlive(request);
        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                currentObj.decoderResult().isSuccess()? HttpResponseStatus.OK : HttpResponseStatus.BAD_REQUEST,
                (httpResponse.getInputBytes() == null || httpResponse.getInputBytes().length == 0) ?
                        Unpooled.copiedBuffer(httpResponse.flush(), CharsetUtil.UTF_8):
                        Unpooled.copiedBuffer(httpResponse.getInputBytes())
        );

        if(httpResponse.getContentType() != null){
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, httpResponse.getContentType());
        }else{
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        }


        if (keepAlive) {
            // Add 'Content-Length' header only for a keep-alive connection.
            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            // Add keep alive header as per:
            // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }


        //自定义响应头
        if(httpResponse.getHeaders() != null && httpResponse.getHeaders().size() > 0){
            for (Map.Entry<String, String> entry : httpResponse.getHeaders().entrySet()) {
                response.headers().add(entry.getKey(), entry.getValue());
            }
        }

        // Write the response.
        ctx.write(response);

        return keepAlive;
    }


}

package com.liqiwei.server;

import com.liqiwei.server.netty.NettyServer;
import com.liqiwei.server.netty.http.HttpServer;
import com.liqiwei.server.netty.websocket.WebsocketServer;

/**
 * 应用主入口。启动类
 *
 * 提供http服务
 * 提供websocket服务
 */
public class ApplicationServer {

    public static void main(String[] args) {
        //启动http服务器
        new Thread(new Runnable() {
            @Override
            public void run() {
                NettyServer httpServer = new HttpServer();
                httpServer.start();
            }
        }).start();
        
        //启动websocket服务器
        new Thread(new Runnable() {
            @Override
            public void run() {
                NettyServer websocketServer = new WebsocketServer();
                websocketServer.start();
            }
        }).start();
    }
}

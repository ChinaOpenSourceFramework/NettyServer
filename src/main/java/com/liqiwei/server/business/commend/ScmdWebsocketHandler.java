package com.liqiwei.server.business.commend;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.util.internal.PlatformDependent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentMap;

/**
 *
 * websocket具体处理
 *
 */
public class ScmdWebsocketHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScmdWebsocketHandler.class);
    private static ScmdWebsocketHandler instance = new ScmdWebsocketHandler();
    final static ConcurrentMap<String, Channel> onlineChannel = PlatformDependent.newConcurrentHashMap();
    private ChannelGroup group;

    private ScmdWebsocketHandler(){}

    public static ScmdWebsocketHandler getInstance(){
        return instance;
    }

    public void setChannelGroup(ChannelGroup group){
        this.group = group;
    }

    //连接打开
    public void dispatchWebsocketOpen(Channel channel){
        LOGGER.info("建立连接");
    }

    //具体消息
    public void dispatchWebsocketMessage(String text){
        LOGGER.info("发送内容");
    }

    //连接断开
    public void dispatchWebsocketClose(Channel channel){
        LOGGER.info("关闭连接");
    }
}
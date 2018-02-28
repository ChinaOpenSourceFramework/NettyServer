package com.liqiwei.server.netty.websocket.attr;

import io.netty.util.AttributeKey;

/**
 * 通道对象附件key保持
 */
public class WsChannelAttrKey {
	
	/**channel 请求 key值*/
	public static final AttributeKey<WsRequestInfo> WS_REQUEST_ATTR_KEY = AttributeKey.newInstance("WS_ATTR_KEY");
}

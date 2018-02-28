package com.liqiwei.server.netty.websocket.attr;

import com.liqiwei.server.util.HttpUtils;

import java.util.Map;

/**
 * 封装ws请求信息，绑定在channel上进行共享
 */
public class WsRequestInfo {
	
	private String uri;
	private Map<String, String> params;
	
	public WsRequestInfo(String uri){
		this.uri = HttpUtils.getUriWithoutParameter(uri);
		this.params = HttpUtils.getUriParameters(uri);
	}
	
	/**
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}
	/**
	 * @param uri the uri to set
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * 获取请求参数
	 * 
	 * @param paramName 参数名称
	 * @return
	 */
	public String getRequestParam(String paramName){
		return params == null ? null : params.get(paramName);
	}
	
	@Override
	public String toString() {
		return uri + "-" + params;
	}
}

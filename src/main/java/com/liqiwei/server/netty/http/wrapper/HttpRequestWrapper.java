package com.liqiwei.server.netty.http.wrapper;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;

/**
 * http请求包装器
 */
public class HttpRequestWrapper {

	private Map<String, Object> dataMap;

	private HttpRequestWrapper(Map<String, Object> dataMap){
		//克隆
		this.dataMap = ObjectUtils.clone(dataMap);
	}

	public static HttpRequestWrapper wrapper(Map<String, Object> dataMap){
		//数据提取
		return new HttpRequestWrapper(dataMap);
	}

	/**
	 * 获取请求头信息
	 *
	 * @param headerKey 请求头键值
	 * @return 请求头信息
	 */
	public String getHeader(String headerKey){
		return String.valueOf(dataMap.get(headerKey));
	}

	/**
	 * 获取请求路径
	 *
	 * @return 请求路径地址
	 */
	public String getUri(){
		return String.valueOf(dataMap.get("REQUEST_URI"));
	}

	/**
	 * 获取请求参数
	 *
	 * @param key
	 * @return 请求参数
	 */
	public String getParameter(String key){
		Map<String, List<String>> params = (Map<String, List<String>>)dataMap.get("PARAMS");
		List<String> paramList = params.get(key);
		return paramList == null ? null : paramList.get(0);
	}

	/**
	 * 获取请求参数
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> getUrlParameter(){
		return  (Map<String, String>) dataMap.get("URL_PARAMS");
	}

	/**
	 * 获取请求体信息(utf-8编码后)
	 */
	public String getRequestBody(){
		return String.valueOf(dataMap.get("CONTENT"));
	}

	/**
	 * 获取请求方法
	 * @return
	 */
	public String getMethod(){
		return String.valueOf(dataMap.get("METHOD"));
	}

	/**
	 * 获取http版本
	 *
	 * @return http协议版本号
	 */
	public String getHttpVersion(){
		return String.valueOf(dataMap.get("VERSION"));
	}
}

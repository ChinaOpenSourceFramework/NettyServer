package com.liqiwei.server.util;

import java.util.HashMap;
import java.util.Map;

/**
 * http工具类
 *
 */
public class HttpUtils {
	
	/**
	 * 提取不带参数的uri
	 * 
	 * @param orginUri 原始的uri，可能带有参数，比如/ws?deviceId=abc1213sds
	 * 
	 * @return uri
	 */
	public static String getUriWithoutParameter(String orginUri){
		return orginUri != null ? (orginUri.indexOf("?") != -1 ? (orginUri.substring(0, orginUri.indexOf("?"))) : orginUri) : null;
	}
	
	/**
	 * 提取uri参数，作为map返回
	 * 
	 * @param orginUri 原始的uri，可能带有参数，比如/ws?deviceId=abc1213sds
	 * 
	 * @return uri
	 */
	public static Map<String, String> getUriParameters(String orginUri){
		if(orginUri.indexOf("?") == -1){
			return null;
		}else{
			String[] paramsString = orginUri.substring(orginUri.indexOf("?") + 1, orginUri.length()).split("&");
			Map<String, String> paramMap = new HashMap<String, String>();
			for (int i = 0; i < paramsString.length; i++) {
				String[] item = paramsString[i].split("=");
				paramMap.put(item[0], item.length == 1 ? null : item[1]);
			}
			return paramMap;
		}
	}
	
}

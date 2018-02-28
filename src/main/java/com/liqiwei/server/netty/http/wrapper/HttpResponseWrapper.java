package com.liqiwei.server.netty.http.wrapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

/**
 * http响应包装器(内容)
 *
 */
public class HttpResponseWrapper {

	private String contentType;
	private Map<String, String> extendsHeaders;
	private StringBuffer buffer;
	//输出字节(存在设计缺陷、大文件拜拜，最好是引用输入流，使用缓冲区逐步输出至结束)
	private byte[] inputBytes;

	public HttpResponseWrapper(){
		buffer = new StringBuffer();
	}

	public void setHeader(String key, String value){
		if(extendsHeaders == null){
			extendsHeaders = new HashMap<String, String>();
		}
		extendsHeaders.put(key, value);
	}

	public Map<String, String> getHeaders(){
		return extendsHeaders;
	}

	public byte[] getInputBytes(){
		return inputBytes;
	}

	public void setByte(byte[] imgByte){
		inputBytes = imgByte;

	}

	public void readInputStream(InputStream inputStream){
		try {
			inputBytes = IOUtils.toByteArray(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType){
		this.contentType = contentType;
	}

	public HttpResponseWrapper append(String content){
		buffer.append(content);
		return this;
	}

	public String flush(){
		return buffer.toString();
	}
}

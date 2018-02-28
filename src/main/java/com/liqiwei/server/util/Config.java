package com.liqiwei.server.util;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 程序配置对象
 */
public class Config {
	
	private static Properties PROPS;
	private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);
	
	static{
		PROPS = new Properties();
		try {
			PROPS.load(Config.class.getClassLoader().getResourceAsStream("app.cfg"));
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	/**http服务器端口*/
	public static final int HTTP_SERVER_PORT = Integer.parseInt(PROPS.getProperty("http.server.port"));
	
	/**websocket服务器端口*/
	public static final int WEBSOCKET_SERVER_PORT = Integer.parseInt(PROPS.getProperty("ws.server.port"));
	
	/**redis服务器地址*/
	public static final String REDIS_SERVER_HOST = PROPS.getProperty("redis.server.host");
	
	/**redis服务器端口*/
	public static final int REDIS_SERVER_PORT = Integer.parseInt(PROPS.getProperty("redis.server.port"));

	/**redis数据库索引*/
	public static final int REDIS_SERVER_DBID = Integer.parseInt(PROPS.getProperty("redis.server.dbid"));
	
	/**redis连接池最大值*/
	public static final int REDIS_SERVER_POOL_MAXACTIVE = Integer.parseInt(PROPS.getProperty("redis.server.pool.maxactive"));
	
	/**redis连接池最大空闲*/
	public static final int REDIS_SERVER_POOL_MAXIDLE = Integer.parseInt(PROPS.getProperty("redis.server.pool.maxidle"));
	
	/**redis连接池最大等待时长*/
	public static final int REDIS_SERVER_POOL_MAXWAIT = Integer.parseInt(PROPS.getProperty("redis.server.pool.maxwait"));
	
	/**redis连接池获取超时*/
	public static final int REDIS_SERVER_POOL_TIMEOUT = Integer.parseInt(PROPS.getProperty("redis.server.pool.timeout"));
	
	/**redis连接池连接前验证*/
	public static final boolean REDIS_SERVER_POOL_TESTONBORROW = Boolean.parseBoolean(PROPS.getProperty("redis.server.pool.testonborrow"));

}

package com.liqiwei.server.netty.http.router;

import com.liqiwei.server.annotations.Controller;
import com.liqiwei.server.annotations.RequestMapping;
import com.liqiwei.server.netty.http.wrapper.HttpRequestWrapper;
import com.liqiwei.server.netty.http.wrapper.HttpResponseWrapper;
import com.liqiwei.server.util.ClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * http 路由配置
 */
public class HttpRouteParse {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRouteParse.class);
    private Map<String, MappingResult> controllers = new HashMap<String, MappingResult>();


    /**
     * controller所在包
     *
     */
    public HttpRouteParse(String controllerPackageName){
        controllers = new HashMap<String, MappingResult>();
        scanPackage(controllerPackageName);
    }

    /**
     * 扫描包
     *
     */
    private void scanPackage(String pkgName) {
        Set<Class<?>> classes = ClassUtil.getClasses(pkgName);
        for (Class<?> clazz : classes) {
            Controller ctrl = clazz.getAnnotation(Controller.class);
            if (ctrl != null) {
                //类级别映射
                RequestMapping classMapping = clazz.getAnnotation(RequestMapping.class);

                Method[] method = clazz.getMethods();
                for (int i = 0; i < method.length; i++) {
                    RequestMapping requestMapping = method[i].getAnnotation(RequestMapping.class);
                    if (requestMapping != null) {

                        String url = (classMapping == null ? "" : classMapping.value());
                        //拼接映射URL
                        url +=requestMapping.value();
                        MappingResult result;
                        try {
                            result = new MappingResult(url, clazz, ctrl.ctrlName(), method[i], clazz.newInstance());
                            controllers.put(url, result);
                            LOGGER.info(result.toString());
                        } catch (Exception e) {
                            LOGGER.error(e.getMessage(), e);
                        }
                    }
                }
            }
        }
    }


    /**
     * 路由转发
     *
     * @param request
     * @param response
     */
    public void dispatch(HttpRequestWrapper request, HttpResponseWrapper response){

        if(controllers != null && request.getUri() != "/favicon.ico"){
            MappingResult mappingResult = controllers.get(request.getUri());
            if(mappingResult == null){
                response.append("url no found : "+request.getUri());
                return;
            }
            try {
                mappingResult.getMethod().invoke(mappingResult.getCtrlInstance(), request, response);
                //增加cors
                response.setHeader("Access-Control-Allow-Origin", "*");
                response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
                response.setHeader("Access-Control-Max-Age", "3600");
                response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
                //打印输出日志：
                LOGGER.info("响应输出：{}",response.flush());
            } catch (Exception e) {
                LOGGER.error("处理请求" + request.getUri() + "发生异常，" + e.getMessage(), e);
            }
        }else{
            response.append("route error");
        }
    }

}
package com.liqiwei.server.netty.http.router;

import java.lang.reflect.Method;

/**
 * 反射方法实体
 */
public class MappingResult {

    private String path;
    private Class<?> clazz;
    private String ctrlName;
    private Method method;
    private Object ctrlInstance;

    public MappingResult(String path, Class<?> clazz, String ctrlName, Method method, Object ctrlInstance) {
        this.path = path;
        this.clazz = clazz;
        this.ctrlName = ctrlName;
        this.method = method;
        this.ctrlInstance = ctrlInstance;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getCtrlName() {
        return ctrlName;
    }

    public void setCtrlName(String ctrlName) {
        this.ctrlName = ctrlName;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getCtrlInstance() {
        return ctrlInstance;
    }

    public void setCtrlInstance(Object ctrlInstance) {
        this.ctrlInstance = ctrlInstance;
    }

    @Override
    public String toString() {
        return "mapping url :"+this.getPath() + ", class :" + getClass().getCanonicalName() + ", method :" + getMethod().getName();
    }
}

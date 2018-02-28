package com.liqiwei.server.business.api;

import com.liqiwei.server.annotations.Controller;
import com.liqiwei.server.annotations.RequestMapping;
import com.liqiwei.server.netty.http.wrapper.HttpRequestWrapper;
import com.liqiwei.server.netty.http.wrapper.HttpResponseWrapper;

/**
 * 用户管理
 */
@Controller
@RequestMapping(value = "/user")
public class UserController {

    @RequestMapping(value = "/test")
    public void test(HttpRequestWrapper request, HttpResponseWrapper response) {
        response.setContentType("application/json");
        response.append("成功");
    }

}

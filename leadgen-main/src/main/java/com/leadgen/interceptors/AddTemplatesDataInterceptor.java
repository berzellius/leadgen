package com.leadgen.interceptors;

import com.leadgen.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by berz on 20.10.14.
 */
@Component
public class AddTemplatesDataInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    OrderService orderService;


    @Override
    public void postHandle(HttpServletRequest request,
                    HttpServletResponse response,
                    Object handler,
                    ModelAndView modelAndView)
            throws Exception{


    }
}

package com.genericauthserver.config.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class TestControllerInterceptor implements HandlerInterceptor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("| Request from:'"+request.getRemoteAddr()+"'| with-username:'"+username+"' | Endpoint:'"+request.getRequestURI()+"' |");

        return true;
    }
}

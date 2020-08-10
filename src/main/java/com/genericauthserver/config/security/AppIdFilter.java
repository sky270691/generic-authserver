package com.genericauthserver.config.security;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AppIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if(request.getHeader("app-id").equalsIgnoreCase("YXBwLWFuZHJvaWQ=") ||
            request.getHeader("app-id").equalsIgnoreCase("YXBwLWNtcw==")){
            filterChain.doFilter(request,response);
        }else{
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

    }


}

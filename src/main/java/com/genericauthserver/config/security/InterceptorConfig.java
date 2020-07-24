package com.genericauthserver.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    private final TestControllerInterceptor testControllerInterceptor;

    @Autowired
    public InterceptorConfig(TestControllerInterceptor testControllerInterceptor){
        this.testControllerInterceptor = testControllerInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(testControllerInterceptor);
    }
}

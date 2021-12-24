package com.example.testserver.config;

import com.example.testserver.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@SpringBootConfiguration
public class MyWebConfigurer implements WebMvcConfigurer {
    private String UPLOAD_FOLDER = "D:/testUpload/";
    @Bean
    public LoginInterceptor getLoginInterceptor(){
        return new LoginInterceptor();
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origins = {
            "http://66.42.44.151:8080", "http://localhost:8080"
        };
        //所有请求都允许跨域
        registry.addMapping("/**")
                .allowCredentials(true)
//                .allowedOrigins("http://localhost:8080")
                .allowedOrigins(origins)
                .allowedMethods("*")
                .allowedHeaders("*");

    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/img/**").addResourceLocations("file:"+UPLOAD_FOLDER);
        registry.addResourceHandler("/temp/**").addResourceLocations("file:"+UPLOAD_FOLDER+"tempFile/");
        WebMvcConfigurer.super.addResourceHandlers(registry);
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getLoginInterceptor()).addPathPatterns("/**").excludePathPatterns("/index.html").excludePathPatterns("/login")
                .excludePathPatterns("/img/**").excludePathPatterns("/temp/**");
    }
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
//    }
}

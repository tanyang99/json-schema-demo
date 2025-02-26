package com.example.demo.config;

import com.example.demo.jsonschema.JsonSchemaValidationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JsonSchemaValidationInterceptor jsonSchemaValidationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jsonSchemaValidationInterceptor).order(Ordered.LOWEST_PRECEDENCE - 1)
                .addPathPatterns("/**");
    }
}
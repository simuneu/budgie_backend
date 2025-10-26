package com.budgie.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer corsConfig(){
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry){
                registry.addMapping("/api/auth/**")
                        .allowedOrigins("http://localhost:5173")
                        .allowedMethods("GET", "POST", "DELETE", "OPTIONS", "PUT")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}

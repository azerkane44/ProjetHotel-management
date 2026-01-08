package com.example.springhotel.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Servir les fichiers depuis src/main/resources/static/uploads/
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("classpath:/static/uploads/");

        System.out.println("✅ Configuration uploads : /uploads/** -> classpath:/static/uploads/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:*")  // ✅ CHANGÉ ICI
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);

        System.out.println("✅ CORS configuré pour tous les ports localhost");
    }
}


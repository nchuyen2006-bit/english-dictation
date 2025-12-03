package com.java.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private AuthInterceptor authInterceptor;
    
    @Autowired
    private RoleInterceptor roleInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Các API không cần xác thực (public)
        String[] excludePatterns = {
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/validate",
            "/api/auth/hash-password",  // XÓA SAU KHI SETUP XONG
            "/api/lesson",              // Public để xem danh sách
            "/api/lessons",             // Public để xem lessons
            "/api/lessons/**",          // Public để xem lesson detail
            "/api/categories"           // Public để xem categories
        };
        
        // Áp dụng AuthInterceptor cho tất cả các API trừ excludePatterns
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(excludePatterns);
        
        // Áp dụng RoleInterceptor sau AuthInterceptor
        registry.addInterceptor(roleInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(excludePatterns);
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
    
    @Override
    public void addResourceHandlers(org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry registry) {
        // Serve uploaded audio files
        registry.addResourceHandler("/uploads/audios/**")
                .addResourceLocations("file:uploads/audios/");
    }
}
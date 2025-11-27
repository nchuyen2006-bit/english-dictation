package com.java.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
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
        // Các API không cần xác thực
        String[] excludePatterns = {
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/validate",
            "/api/auth/hash-password"  // CHỈ DÙNG ĐỂ TEST - XÓA SAU
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
}

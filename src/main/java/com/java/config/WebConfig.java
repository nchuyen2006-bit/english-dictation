package com.java.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
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
            "/api/auth/**",           
            "/api/lesson",            
            "/api/lesson/**",         
            "/api/lessons/**",        
            "/api/categories",        
            "/api/categories/**",     
            "/api/category/**",      
            "/api/audios/lesson/**", 
            "/api/transcripts"  
        };
        
        
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(excludePatterns);
        
       
        registry.addInterceptor(roleInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(excludePatterns);
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                    "http://127.0.0.1:5500",
                    "http://localhost:5500",
                    "http://127.0.0.1:5501",
                    "http://localhost:5501"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve uploaded audio files
        registry.addResourceHandler("/uploads/audios/**")
                .addResourceLocations("file:uploads/audios/");
    }
}
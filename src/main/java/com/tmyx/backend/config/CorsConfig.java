package com.tmyx.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${file.resource-path}")
    private String resourcePath;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 对所有接口生效
                .allowedOriginPatterns("*") // 允许所有源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的方法
                .allowedHeaders("*") // 允许的 Header
                .allowCredentials(true) // 允许携带 Cookie
                .maxAge(3600); // 预检请求有效期
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 确保你的上传路径是以 file: 开头的合法 URL
        String filePath = resourcePath.replace("\\", "/");
        if (!filePath.endsWith("/")) filePath += "/";

        // 文章
//        registry.addResourceHandler("/articles/**")
//                .addResourceLocations("file:" + filePath + "articles/");

        // 通用映射
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + filePath)
                .setCachePeriod(3600);

    }


}

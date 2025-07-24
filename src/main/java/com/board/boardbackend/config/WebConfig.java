package com.board.boardbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://127.0.0.1:3000") // ✨ 허용할 프론트엔드 Origin 지정
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드 지정
                .allowedHeaders("*") // 모든 헤더 허용 (Authorization 등)
                .allowCredentials(true) // 자격 증명 (쿠키, HTTP 인증, JWT 토큰 등) 허용
                .maxAge(3600); // Pre-flight 요청 캐싱 시간 (초)
    }
}

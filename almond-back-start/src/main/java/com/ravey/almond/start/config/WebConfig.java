package com.ravey.almond.start.config;

import com.ravey.almond.start.interceptor.TokenInterceptor;
import com.ravey.common.service.web.interceptor.TransmitCacheInfoInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 *
 * @author ravey
 * @since 1.0.0
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final TokenInterceptor tokenInterceptor;

    @Bean
    public TransmitCacheInfoInterceptor transmitCacheInfoInterceptor() {
        return new TransmitCacheInfoInterceptor();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. 优先执行Token拦截器，解析Token并设置UserInfo
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/swagger**/**", "/v3/api-docs/**", "/doc.html", "/webjars/**"); // 排除Swagger等路径

        // 2. 注册用户信息透传拦截器（作为备选方案，或者用于处理网关转发过来的UserInfo头）
        // 注意：如果在TokenInterceptor中已经设置了UserInfo，这个拦截器可能会覆盖或者被跳过（取决于具体逻辑）
        // 鉴于TokenInterceptor已经处理了Token校验和UserInfo设置，这里的TransmitCacheInfoInterceptor主要用于
        // 兼容那些已经通过网关处理并带上UserInfo头的请求。
        registry.addInterceptor(transmitCacheInfoInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/swagger**/**", "/v3/api-docs/**", "/doc.html", "/webjars/**");
    }
}

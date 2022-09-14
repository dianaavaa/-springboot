package com.pw.demo.config;

import com.pw.demo.config.interceptor.JwtIntereceptor;
import com.pw.demo.handler.WebSecurityHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: P
 * @DateTime: 2022/1/10 11:30
 **/
@Configuration
public class IntereceptorConfig implements WebMvcConfigurer {

    @Bean
    public WebSecurityHandler getWebSecurityHandler(){
        return new WebSecurityHandler();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //拦截所有请求，判断token是否合法 但是要排除登录登出导入导出等
        registry.addInterceptor(jwtInterceptor())
                .addPathPatterns("/**")  // 拦截所有请求，通过判断token是否合法来决定是否需要登录
                .excludePathPatterns("/user/login", "/user/register", "/**/export", "/**/import", "/file/**","/user/register/**",
                        "/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**", "/api", "/api-docs", "/api-docs/**")
                .excludePathPatterns( "/**/*.html", "/**/*.js", "/**/*.css", "/**/*.woff", "/**/*.ttf");  // 放行静态文件

        registry.addInterceptor(getWebSecurityHandler());


    }
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/assets/**").addResourceLocations("/resources/assets/");
        //swagger增加url映射
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

    }


    @Bean
    public JwtIntereceptor jwtInterceptor(){
        return new JwtIntereceptor();
    }
}

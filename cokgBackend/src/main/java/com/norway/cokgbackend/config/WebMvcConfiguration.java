package com.norway.cokgbackend.config;

import com.norway.cokgbackend.interceptor.AuthControlInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description: TODO
 * @Author: Norway
 * @Email: 1959415641@qq.com
 * @Date: 2026/1/3 19:17
 */
@Configuration // 配置类，用于定义 Spring Bean，Spring启动时会加载该类
public class WebMvcConfiguration implements WebMvcConfigurer {

    /**
     * 1. 将自定义拦截器注册为 Bean（让 Spring 托管，支持 @Autowired 注入依赖）
     */
    @Bean
    public AuthControlInterceptor authControlInterceptor() {
        return new AuthControlInterceptor();
    }

    /**
     * 2. 注册拦截器到 Spring MVC 中
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加自定义拦截器
        registry.addInterceptor(authControlInterceptor())
                .addPathPatterns("/**") // 拦截所有请求（/** 表示所有层级的请求）
                .excludePathPatterns( // 排除不需要拦截的请求
                        "/user/login", // 登录接口
                        "/user/register", // 注册接口
                        "/user/check_auth", // 校验权限接口
                        "/graph/**"  // 所有图数据接口
                );
    }


}

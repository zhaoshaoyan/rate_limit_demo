package com.shaoyan.rate_limiter.conf;

import com.shaoyan.rate_limiter.filter.RepeatAbleRequestFilter;
import com.shaoyan.rate_limiter.interceptor.RepeatSubmitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author: zsy
 * @date: 2023/4/5
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    RepeatSubmitInterceptor repeatSubmitInterceptor;


    @Bean
    FilterRegistrationBean<RepeatAbleRequestFilter> filterFilterRegistrationBean() {
        FilterRegistrationBean<RepeatAbleRequestFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new RepeatAbleRequestFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(repeatSubmitInterceptor).addPathPatterns("/**");
    }
}
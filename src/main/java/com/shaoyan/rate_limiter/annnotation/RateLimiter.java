package com.shaoyan.rate_limiter.annnotation;

import com.shaoyan.rate_limiter.enums.LimitType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: zsy
 * @date: 2023/4/1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimiter {
    /**
     * 限流的key，主要是指前缀
     *
     * @return
     */
    String key() default "rate_limit:";

    /**
     * 限流时间
     *
     * @return
     */
    int time() default 60;

    /**
     * 限流次数
     */
    int count() default 100;

    /**
     * 限流类型
     */
    LimitType limitType() default LimitType.DEFAULT;
}
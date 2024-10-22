package com.shaoyan.rate_limiter.enums;

/**
 * @author: zsy
 * @date: 2023/4/1
 * <p>
 * 限流的类型
 */
public enum LimitType {

    /**
     * 默认的限流策略
     */
    DEFAULT,
    /**
     * 基于IP地址限流
     */
    IP
}
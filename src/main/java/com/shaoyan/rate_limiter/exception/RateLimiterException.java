package com.shaoyan.rate_limiter.exception;

/**
 * @author: zsy
 * @date: 2023/4/3
 */
public class RateLimiterException extends Throwable {

    public RateLimiterException(String message) {
        super(message);
    }
}

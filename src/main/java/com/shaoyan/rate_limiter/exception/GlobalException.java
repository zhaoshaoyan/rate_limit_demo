package com.shaoyan.rate_limiter.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: zsy
 * @date: 2023/4/3
 */
@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(RateLimiterException.class)
    public Map<String, Object> LimitException(RateLimiterException e) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("status", 500);
        map.put("Message", e.getMessage());
        return map;
    }
}
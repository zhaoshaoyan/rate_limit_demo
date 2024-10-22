package com.shaoyan.rate_limiter.controller;

import com.shaoyan.rate_limiter.annnotation.RateLimiter;
import com.shaoyan.rate_limiter.annnotation.RepeatSubmit;
import com.shaoyan.rate_limiter.model.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: zsy
 * @date: 2023/4/8
 */

@RestController
public class HelloController {

    @RepeatSubmit(interval = 5000)
    @PostMapping("/hello")
    public String hello(@RequestBody String json) {
        return json;
    }


    @RateLimiter
    @GetMapping("/test1")
    public Map test1(User user) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("data", user);
        resultMap.put("code", "200");
        resultMap.put("message", "success");
        return resultMap;
    }
}
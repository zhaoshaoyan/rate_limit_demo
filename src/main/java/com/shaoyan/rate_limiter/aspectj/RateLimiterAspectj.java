package com.shaoyan.rate_limiter.aspectj;

import com.shaoyan.rate_limiter.annnotation.RateLimiter;
import com.shaoyan.rate_limiter.enums.LimitType;
import com.shaoyan.rate_limiter.exception.RateLimiterException;
import com.shaoyan.rate_limiter.utils.IpUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Collections;

/**
 * @author: zsy
 * @date: 2023/4/3
 */
@Aspect
@Component
public class RateLimiterAspectj {
    private static final Logger logger = LoggerFactory.getLogger(RateLimiterAspectj.class);
    @Autowired
    RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    RedisScript<Long> redisScript;

    @Before("@annotation(rateLimiter)")
    public void before(JoinPoint point, RateLimiter rateLimiter) throws RateLimiterException {
        int count = rateLimiter.count();
        int time = rateLimiter.time();
        String combineKey = getCombineKey(point, rateLimiter);
        try {
            Long number = redisTemplate.execute(redisScript, Collections.singletonList(combineKey), time, count);
            if(number == null || number.intValue() > count) {
                // 超过限流阈值
                logger.info("当前接口达到最大限流次数");
                throw new RateLimiterException("访问过于频繁,请稍后访问");
            }
            logger.info("一个时间窗口内请求次数:{},当前请求次数{},缓存的key{}", count, number, combineKey);
        } catch(RateLimiterException e) {
            throw e;
        }


    }

    /**
     * 这个key就是redis中的key
     * ip    rate_limit:11.11.11.11-com.shaoyan.controller.HelloController-hello
     * default  rate_limit:com.shaoyan.controller.HelloController-hello
     *
     * @param point
     * @param rateLimiter
     * @return
     */
    private String getCombineKey(JoinPoint point, RateLimiter rateLimiter) {
        StringBuffer stringBuffer = new StringBuffer(rateLimiter.key());
        if(rateLimiter.limitType() == LimitType.IP) {
            stringBuffer.append(IpUtils.getIpAddr(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()))
                    .append("-");
        }
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        stringBuffer
                .append(method.getDeclaringClass().getName())
                .append(method.getName());
        return stringBuffer.toString();
    }
}
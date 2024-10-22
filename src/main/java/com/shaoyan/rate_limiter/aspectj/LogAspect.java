package com.shaoyan.rate_limiter.aspectj;

import com.alibaba.fastjson2.JSON;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author: zsy
 * @date: 2023/8/24
 */
@Aspect
@Component

public class LogAspect {
    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);


    @Around("execution(* com.shaoyan.rate_limiter.controller.*.*(..))")
    public Object RequestAround(ProceedingJoinPoint joinPoint) {
        // 开始时间
        long startTime = System.currentTimeMillis();
        // 请求参数
        Object[] args = joinPoint.getArgs();

        // 方法名称
        String methodName = joinPoint.getSignature().getName();
        String ClassName = joinPoint.getSignature().getDeclaringType().getName();
        Object proceed = null;
        try {
            proceed = joinPoint.proceed();
        } catch(Throwable e) {
            e.printStackTrace();
        } finally {
            log.info("=================调用接口==================================");
            log.info("接口名称：" + ClassName + "-" + methodName);
            log.info("请求参数:" + JSON.toJSONString(args));
            log.info("返回参数:" + JSON.toJSONString(proceed));
            log.info("调用耗时:" + (System.currentTimeMillis() - startTime));
            log.info("=================接口返回==================================");
            return proceed;
        }

    }


}
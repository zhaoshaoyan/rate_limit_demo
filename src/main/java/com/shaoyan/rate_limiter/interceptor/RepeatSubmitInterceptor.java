package com.shaoyan.rate_limiter.interceptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaoyan.rate_limiter.annnotation.RepeatSubmit;
import com.shaoyan.rate_limiter.redis.RedisCache;
import com.shaoyan.rate_limiter.request.RepeatSubmitRequestWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: zsy
 * @date: 2023/4/5
 */
@Component
public class RepeatSubmitInterceptor implements HandlerInterceptor {

    public static final String REPEAT_PARAMS = "repeat_params";
    public static final String REPEAT_TIME = "repeat_time";
    public static final String REPEAT_SUBMIT_KEY = "repeat_submit_key";
    public static final String HEADER = "Authorization";

    @Autowired
    private RedisCache redisCache;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /**
         * HandlerMethod 为封装的方法对象，里面有各种方法的信息
         */
        if(handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            RepeatSubmit annotation = method.getAnnotation(RepeatSubmit.class);
            if(annotation != null) {
                if(isRepeatSubmit(request, annotation)) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("status", 500);
                    map.put("message", annotation.message());
                    response.getWriter().write(new ObjectMapper().writeValueAsString(map));
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 判断是否重复提交
     *
     * @param request
     * @param repeatSubmit
     * @return
     */
    private boolean isRepeatSubmit(HttpServletRequest request, RepeatSubmit repeatSubmit) {
        String nowParams = "";
        if(request instanceof RepeatSubmitRequestWrapper) {
            try {
                nowParams = ((RepeatSubmitRequestWrapper) request).getReader().readLine();
            } catch(IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                nowParams = new ObjectMapper().writeValueAsString(request.getParameterMap());
            } catch(JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        HashMap<String, Object> nowMap = new HashMap<>();
        nowMap.put(REPEAT_PARAMS, nowParams);
        nowMap.put(REPEAT_TIME, System.currentTimeMillis());
        String requestURI = request.getRequestURI();
        String header = request.getHeader(HEADER);
        String cacheKey = REPEAT_SUBMIT_KEY + requestURI + header.replace("bearer ", "");
        Object cacheObject = redisCache.getCacheObject(cacheKey);
        if(cacheObject != null) {
            Map<String, Object> map = (Map<String, Object>) cacheObject;
            if(ComparParam(map, nowMap) && comparTime(map, nowMap, repeatSubmit.interval())) {
                return true;
            }
        }
        redisCache.setCacheObject(cacheKey, nowMap, repeatSubmit.interval(), TimeUnit.MILLISECONDS);
        return false;
    }

    private boolean comparTime(Map<String, Object> map, HashMap<String, Object> nowMap, int interval) {
        Long time1 = (Long) map.get(REPEAT_TIME);
        Long time2 = (Long) nowMap.get(REPEAT_TIME);
        if((time2 - time1) < interval) {
            return true;
        }
        return false;
    }

    private boolean ComparParam(Map<String, Object> map, HashMap<String, Object> nowMap) {
        String nowParam = ((String) nowMap.get(REPEAT_PARAMS));
        String dataParams = (String) map.get(REPEAT_PARAMS);

        return nowParam.equals(dataParams);
    }
}
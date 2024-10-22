package com.shaoyan.rate_limiter.annnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: zsy
 * @date: 2023/4/5
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RepeatSubmit {

    /**
     * 间隔时间(ms)，小于此时间视为重复提交
     */
    public int interval() default 3000;


    /**
     * 提示消息
     */
    public String message() default "不允许重复提交，请稍候再试";
}
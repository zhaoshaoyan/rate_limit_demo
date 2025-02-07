# 判定接口限流/重复提交 解决方案

## 1. 基于自定义注解+lua脚本实现限流
1.1 引入相关依赖
```text
  <dependency>
            <groupId>com.alibaba.fastjson2</groupId>
            <artifactId>fastjson2</artifactId>
            <version>2.0.33</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>
```
2. 自定义注解
```text
com.shaoyan.rate_limiter.annnotation
```
3. 自定义限流切面
```text
com.shaoyan.rate_limiter.aspect
```
4. 配置redis
在resources/application.properties中配置redis,或使用yml格式
5. 自定义lua脚本
```javascript
local key = KEYS[1]
local time = tonumber(ARGV[1])
local count = tonumber(ARGV[2])
local current = redis.call('get',key)
if current and tonumber(current)>count then
return tonumber(current)
end
current = redis.call('incr',key)
if tonumber(current) == 1 then
redis.call('expire',key,time)
end
return tonumber(current)
```

##  基于自定义注解 + 拦截器实现的判断是否重复提交解决方案
1. 自定义注解
```text
com.shaoyan.rate_limiter.annnotation
```
2. 自定义拦截器
```text
com.shaoyan.rate_limiter.interceptor.RepeatSubmitInterceptor
```
3. 配置拦截器
```text
com.shaoyan.rate_limiter.conf.WebConfig
```
添加filter解决请求参数不能重复读取的问题
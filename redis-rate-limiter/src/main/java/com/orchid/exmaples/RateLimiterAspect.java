package com.orchid.exmaples;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Aspect
@Component
public class RateLimiterAspect {


    private Map<String, RateLimiter> rateLimitors=new ConcurrentHashMap<>();


    @Pointcut("@annotation(RateLimiterAnno)")
    public void pointCut(){}


    @Before("pointCut()")
    public void before(JoinPoint joinPoint){
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();
//        Object target = joinPoint.getTarget();
//        Object[] arguments = joinPoint.getArgs();
//        RateLimiterAnno limiter=targetMethod.getAnnotation(RateLimiterAnno.class);
        String key=targetMethod.getName();
        RateLimiter limiter=null;
        synchronized (rateLimitors){
            if(!rateLimitors.containsKey(key)){
                limiter=RateLimiter.create(3);
                rateLimitors.put(key, limiter);
            }else{
                limiter=rateLimitors.get(key);
            }
        }
        log.info("xxxxxxxxxxxxx获取令牌：{}", limiter.acquire());
    }


}

package com.my.blog.aspect;

import com.alibaba.fastjson.JSON;
import com.my.blog.annotation.Log;
import com.my.blog.enums.BusinessType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class LogAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Pointcut("@annotation(com.my.blog.annotation.Log)")
    public void pointcut() {
    }

    @AfterReturning(pointcut = "pointcut()", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Object jsonResult) {
        handleLog(joinPoint, null, jsonResult);
    }

    @AfterThrowing(pointcut = "pointcut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        handleLog(joinPoint, e, null);
    }

    private void handleLog(JoinPoint joinPoint, Exception e, Object jsonResult) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Log logAnnotation = method.getAnnotation(Log.class);
            if (logAnnotation == null) {
                return;
            }

            String className = joinPoint.getTarget().getClass().getName();
            String methodName = signature.getName();

            StringBuilder params = new StringBuilder();
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                for (Object arg : args) {
                    params.append(JSON.toJSONString(arg)).append(" ");
                }
            }

            if (e != null) {
                logger.error("操作名称:{},方法:{},类名:{},参数:{},异常:{}",
                    logAnnotation.title(), methodName, className, params, e.getMessage());
            } else {
                logger.info("操作名称:{},业务类型:{},请求方法:{},方法:{},类名:{},参数:{},结果:{}",
                    logAnnotation.title(), logAnnotation.businessType(),
                    logAnnotation.requestMethod(), methodName, className, params,
                    jsonResult != null ? JSON.toJSONString(jsonResult) : "success");
            }
        } catch (Exception ex) {
            logger.error("日志记录异常", ex);
        }
    }
}

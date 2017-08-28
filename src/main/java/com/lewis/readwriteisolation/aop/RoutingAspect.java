package com.lewis.readwriteisolation.aop;

import com.lewis.readwriteisolation.core.JdbcContextHolder;
import com.lewis.readwriteisolation.core.RoutingDB;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

//@Component
//@Aspect
public class RoutingAspect {

    //@Before(value = "@annotation(com.lewis.readwriteisolation.core.RoutingDB)")
    public void before(JoinPoint point){
        Object target = point.getTarget();
        String method = point.getSignature().getName();
        Class<?>[] classz = target.getClass().getInterfaces();
        MethodSignature methodSignature = (MethodSignature)point.getSignature();
        Class<?>[] parameterTypes = methodSignature.getMethod().getParameterTypes();
        try {
            Method m = classz[0].getMethod(method, parameterTypes);
            if (m!=null ) {
                if( m.isAnnotationPresent(RoutingDB.class)){
                    JdbcContextHolder.clearJdbcType();
                    RoutingDB routingDB = m.getAnnotation(RoutingDB.class);
                    JdbcContextHolder.setJdbcType(routingDB.routingType().getType());
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}

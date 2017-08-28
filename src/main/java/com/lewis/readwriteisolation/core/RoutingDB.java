package com.lewis.readwriteisolation.core;

import com.lewis.readwriteisolation.core.enums.RoutingEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE,ElementType.METHOD})
public @interface RoutingDB {

    RoutingEnum routingType() default RoutingEnum.MASTER;

}

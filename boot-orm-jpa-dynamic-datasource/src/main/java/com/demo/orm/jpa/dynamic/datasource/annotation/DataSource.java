package com.demo.orm.jpa.dynamic.datasource.annotation;

import java.lang.annotation.*;

/**
 * 备注：自定义数据源选择注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSource {
    String name() default "";
}

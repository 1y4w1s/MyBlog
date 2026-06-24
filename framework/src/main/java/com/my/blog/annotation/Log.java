package com.my.blog.annotation;

import com.my.blog.enums.BusinessType;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    BusinessType businessType() default BusinessType.OTHER;

    String method() default "";

    String requestMethod() default "";

    String title() default "";

    String contentType() default "";
}

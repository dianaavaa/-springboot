package com.pw.demo.config;

import java.lang.annotation.*;

/**
 * @Author: P
 * @DateTime: 2022/1/14 12:33
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthAccess {

}
package com.pw.demo.annotation;

import java.lang.annotation.*;

/**
 * @Author: P
 * @DateTime: 2022/2/8 11:59
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessLimit {

    /**
     * 单位时间 秒
     * @return int
     */
    int seconds();

    /**
     *  单位时间最大请求次数
     * @return int
     */
    int maxCount();
}

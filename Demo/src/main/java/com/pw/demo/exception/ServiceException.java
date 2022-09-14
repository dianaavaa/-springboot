package com.pw.demo.exception;

import lombok.Getter;

/**
 * @Author: P
 * @DateTime: 2022/1/8 20:40
 **/
@Getter
public class ServiceException extends RuntimeException {
    private String code;
    public ServiceException(String code,String msg){
        super(msg);
        this.code=code;
    }


}

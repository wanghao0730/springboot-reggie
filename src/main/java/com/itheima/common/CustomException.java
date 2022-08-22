package com.itheima.common;

/**
 * 自定义异常处理类
 */
public class CustomException extends RuntimeException{
    public CustomException(String msg) {
        super(msg);
    }
}

package com.itheima.common;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Result类 用于传递给前台的响应数据 结构
 * @param <T>
 */
@Data
public class Result<T> implements Serializable {

    private Integer code; //编码：1成功，0和其它数字为失败

    private String msg; //错误信息

    private T data; //数据

    private Map map = new HashMap(); //动态数据

    public static <T> Result<T> success(T object) {
        Result<T> res = new Result<T>();
        res.data = object;
        res.code = 1; //1 表示成功
        return res;
    }


    public static <T> Result<T> error(String msg) {
        Result res = new Result();
        res.msg = msg;
        res.code = 0; //0 表示失败
        return res;
    }

    //添加动态数据
    public Result<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }



}


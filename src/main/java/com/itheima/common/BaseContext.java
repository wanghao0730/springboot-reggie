package com.itheima.common;

/**
 * 基于ThreadLocal封装工具类，用户保存和获取当前登录用户id
 */
public class BaseContext {
    //相当于一次请求过来对应一个线程，每个线程都有自己的副本，不会造成线程之间变量的相同
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置值到threadlocal
     * @param id
     */
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    /**
     * 获取值
     * @return
     */
    public static Long getCurrentId() {
        return threadLocal.get();
    }
}

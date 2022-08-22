package com.itheima.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.itheima.common.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.assertj.core.util.DateUtil;
import org.springframework.stereotype.Component;

/**
 * 实现填充器没有这一步 自动填充数据不能生效
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    //插入数据的方法
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill ...");
        //设置创建日期和更新日期的自动填充
        this.setFieldValByName("createTime", DateUtil.now(),metaObject);
        this.setFieldValByName("updateTime",DateUtil.now(),metaObject);
        //创建的用户人也要更新和插入
        this.setFieldValByName("createUser", BaseContext.getCurrentId(),metaObject);
        this.setFieldValByName("updateUser",BaseContext.getCurrentId(),metaObject);
    }

    // 更新数据的方法
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill...");
        this.setFieldValByName("updateTime",DateUtil.now(),metaObject);
        //更新修改的用户值
        this.setFieldValByName("updateUser",BaseContext.getCurrentId(),metaObject);
    }
}

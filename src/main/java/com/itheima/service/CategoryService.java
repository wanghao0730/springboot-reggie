package com.itheima.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.entity.Category;

/**
 * 分类的service接口层
 */
public interface CategoryService extends IService<Category> {
    //根据id删除分类 逻辑删除并不是真实删除
    void remove(Long id);
}

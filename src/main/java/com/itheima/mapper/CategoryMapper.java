package com.itheima.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.entity.Category;
import org.apache.ibatis.annotations.Mapper;

//mybatis plus 会扫描@Mapper注解
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}

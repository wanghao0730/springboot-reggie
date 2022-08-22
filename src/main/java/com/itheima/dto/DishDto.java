package com.itheima.dto;

import com.itheima.entity.Dish;
import com.itheima.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO 全称为Data transfer object 既数据传输对象,一般用于展示层和服务层之间的数据传输
 * 例如：在菜品表传递的时候有个字段flavors(口味list) 但是在dish实体类上是没有这个bean对象的这时候会映射就有问题,所以可以单独创建一个可以用于数据传输的对象 也就是整合了两张表中的字段一样
 */
@Data
public class DishDto extends Dish {

    //口味list
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;

}

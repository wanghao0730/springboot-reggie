package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.common.CustomException;
import com.itheima.entity.Category;
import com.itheima.entity.Dish;
import com.itheima.entity.Setmeal;
import com.itheima.mapper.CategoryMapper;
import com.itheima.service.CategoryService;
import com.itheima.service.DishService;
import com.itheima.service.EmployeeService;
import com.itheima.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 分类的service层实现
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService; //菜品的service层

    @Autowired
    private SetmealService setmealService; // 套餐的service层

    /**
     * 逻辑删除分类，删除前进行判断 因为当前的分类可能有其他的菜品依赖这个分类(因为菜品属于分类之中)
     * @param id
     */
    @Override
    public void remove(Long id) {
        //查看下传入的id (分类id进行查询) 判断当前分类下有没有菜品或者套餐
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件id
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);

        //返回dish中依赖category分类的总数
        int dishCount = dishService.count(dishLambdaQueryWrapper);

        if (dishCount > 0) { //表示当前的分类有被依赖的菜品
            //已经关联了业务抛出异常
            throw new CustomException("当前分类下关联了菜品,不能删除");
        }

         //判断当前的分类有没有被依着的套餐
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);

        int setmealCount = setmealService.count(setmealLambdaQueryWrapper);

        if (setmealCount > 0) { //表示有依赖
            //已经关联业务抛出异常
            throw new CustomException("当前分类下关联了套餐,不能删除");
        }

        //调用删除的方法
        super.removeById(id);
    }
}

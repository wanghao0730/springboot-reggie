package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.common.CustomException;
import com.itheima.dto.SetmealDto;
import com.itheima.entity.Setmeal;
import com.itheima.entity.SetmealDish;
import com.itheima.mapper.SetmealMapper;
import com.itheima.service.SetmealDishService;
import com.itheima.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐的操作 以及 新增套餐菜品表的操作 套菜菜品表其实就是关联了套餐id以及菜品的id
     * @param setmealDto
     */
    //多张表操作加上事务 错误可回滚
    @Transactional
    public void saveWithMeal(SetmealDto setmealDto) {
        //先 新增套餐 执行完毕后mp会对我们的原对象SetmealDto进行id值的插入
        this.save(setmealDto);
        // 获取SetmealDto中保存的套餐菜品
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //又因为每个套餐菜品需要关联套餐的id 所以执行新增完毕后可以从原来的SetmealDto中取值 ，当然菜品的id已经在每个对象中了
        setmealDishes.stream().map((item) -> {
            //设置套餐的id值
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        //进行批量的数据插入
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐，同时需要删除套餐菜品中的关联数据
     * @param ids
     */
    @Override
    public void removeWithMeal(List<Long> ids) {
        // select count(*) from setmeal where id in (1,2,3) and status = 1
        //查询套餐状态，确定是否可删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        int count = this.count(queryWrapper);
        if (count > 0) { //表示查出来了有套餐的状态为1那么就不能删除要先修改套餐的在售状态
            throw new CustomException("套餐正在销售,不能删除");
        }
        //调用删除套餐的方法 注意是ids传入的是list
        this.removeByIds(ids);

        // 查询套餐下的套餐菜品
        //delete from setmeal_dish where setmeal_id in (1,2,3)
        LambdaQueryWrapper<SetmealDish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        //删除关系表中的数据 ---setmeal_dish
        setmealDishService.remove(dishLambdaQueryWrapper);

    }

}

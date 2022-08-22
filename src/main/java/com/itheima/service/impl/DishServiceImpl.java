package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.dto.DishDto;
import com.itheima.entity.Dish;
import com.itheima.entity.DishFlavor;
import com.itheima.mapper.DishMapper;
import com.itheima.service.DishFlavorService;
import com.itheima.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     * 新增菜品以及 新增菜品口味表的数据
     * @param dishDto
     */
    // 涉及到多张表的操作这里要使用事务注解 在启动类要开启事务
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {

        // mp会利用雪花算法自动生成id 也就是菜品插入生成的id值插入到数据库中
        // 先利用dishdto中的数据进行插入到dish菜品表中
        this.save(dishDto);


        //从 dishDto 中获取菜品的id 因为 菜品口味表的每一条数据都依赖一个菜品的id进行关联,但是前台传递过来的list数据没有包含id我们需要手动遍历插入id值
        Long dishDtoId = dishDto.getId();

        //拿到菜品口味数据list
        List<DishFlavor> dishDtoFlavors = dishDto.getFlavors();
        //然后遍历数据可以使用forEach 或者stream流
//        dishDtoFlavors.forEach((item) -> {
//            //给每个对象中设置菜品的id
//            item.setDishId(dishDtoId);
//        });
        dishDtoFlavors = dishDtoFlavors.stream().map((item) -> {
            item.setDishId(dishDtoId);
            return item;
        }).collect(Collectors.toList());

        //保存菜品口味数据到菜品口味表dish_flavor 使用saveBatch方法批量处理
        dishFlavorService.saveBatch(dishDtoFlavors);

    }

    /**
     * 根据id查询菜品信息和对于的口味信息
     * @param id
     * @return DishDto
     */
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品的信息，从dish查询
        Dish byId = this.getById(id);

        //构造一个dishdto
        DishDto dishDto = new DishDto();
        //将查询到的dish菜品进行拷贝到dishdto中 然后后面查询到的口味(flavors)再进行插入值
        BeanUtils.copyProperties(byId,dishDto);

        //查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> flavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //口味的查询条件dish_id与传入的id相等
        flavorLambdaQueryWrapper.eq(DishFlavor::getDishId,byId.getId());
        //获取出当前要修改菜品的所有口味信息
        List<DishFlavor> list = dishFlavorService.list(flavorLambdaQueryWrapper);

        //把list存储到dishDto中
        dishDto.setFlavors(list);
        //返回dishdto
        return dishDto;
    }


    /**
     * 根据传入的dishDto进行菜品表的更新以及菜品口味表的更新
     * @param dishDto
     */
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新菜品表的基本信息
        this.updateById(dishDto);

        //清理当前菜品对应口味数据---dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> flavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        flavorLambdaQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        //先将flavor表中对应的数据先删除掉
        dishFlavorService.remove(flavorLambdaQueryWrapper);

        // 再从前台传入的dishdto中获取口味表
        List<DishFlavor> flavors = dishDto.getFlavors();
        //因为回显dish接口的时候进行了copy所以flavors数组中的每一项都有id的值，如果前台删除了口味重新添加就没了还是要进行数据遍历插入dishid的值
        flavors = flavors.stream().map((item) -> {
            //因为dishDto是继承dish的所以它反序列化的时候就会保存到有id的值
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        //进行批量操作进行口味的插入
        dishFlavorService.saveBatch(flavors);
    }
}

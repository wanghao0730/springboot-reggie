package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.Result;
import com.itheima.dto.DishDto;
import com.itheima.entity.Category;
import com.itheima.entity.Dish;
import com.itheima.entity.DishFlavor;
import com.itheima.service.CategoryService;
import com.itheima.service.DishFlavorService;
import com.itheima.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;
    /**
     * 分页返回菜品的信息
     * @param page 页码
     * @param pageSize 每页显示条数
     * @param name 是否存在关键词搜索
     * @return
     */
    @GetMapping("/page")
    public Result<Page> pageList(int page,int pageSize,String name) {

        // 如果直接返回Dish实体对象出去,前台拿到的分类名称只是个id值  需要利用dto解决这个问题
        Page<Dish> dishPage = new Page<>(page, pageSize);

        Page<DishDto> dishDtoPage = new Page<>();

        //创建查询条件
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //判断查询条件 先要判断前面的StringUtils.hasText(name)是否为空 然后构造sql语句
        dishLambdaQueryWrapper.like(StringUtils.hasText(name),Dish::getName,name);

        dishLambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(dishPage,dishLambdaQueryWrapper);
        //完成page查询调用后 dishPage变量里面有所有的参数 我们需要对records这个list数组进行处理 也就是解决分类id查询获取分类名称的解决
        //先将dishPage中所有的分页参数进行拷贝到dishDtoPage中 排除records字段 这样dishDtoPage中就有处理page中的records变量的其他所有的属性值了
        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");
        //获取要进行处理的records(前台将显示的内容)
        List<Dish> records = dishPage.getRecords();

        //用于存储最终的list数组
        List<DishDto> dishDtoList = new ArrayList<>();
        //以下的操作遍历数组 获取每个categoryid 然后进行数据库查询重新构造每个item值
        records.forEach((item) -> {
            //我们要返回的数组将是List<DishDto>类型的 所以将 item进行拷贝到DishDto
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto); //这样dishDto对象就有所有的参数了 可以在下面设置categoryname的名称 获取
            Long categoryId = item.getCategoryId();
            //根据id查询分类
            Category byId = categoryService.getById(categoryId);
            //存储到dishDto名字
            String categoryName = byId.getName();
            if (categoryName != null) { //判断是否存在分类名在添加
                dishDto.setCategoryName(categoryName);
            }
            //添加到数组当中去
            dishDtoList.add(dishDto);
        });

        //最后将 处理好的records进行存储
        dishDtoPage.setRecords(dishDtoList);

        return Result.success(dishDtoPage);
    }


    /**
     * 添加菜品的接口
     * 当前接口要处理 完成菜品的新增 已经菜品口味表的新增 菜品表和口味表分成了两张
     * 注意点：前端传递的数据是json数据 当这里下面的DishDto要做反序列化成对象的时候是需要加上@RequestBody 才能反序列化到对象的
     * @param dish
     * @param flavors
     * @return
     */
    @PostMapping
    public Result<String> addDish(@RequestBody DishDto dishDto) {
        log.info("数据传输对象:{}",dishDto);
        dishService.saveWithFlavor(dishDto);
        return Result.success("新增菜品成功");
    }

    /**
     * 用于根据菜品的id查询菜品的详细信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishDto> getDishByid(@PathVariable("id") Long id) {

        //调用service层方法查询数据
        DishDto byIdWithFlavor = dishService.getByIdWithFlavor(id);

        if (byIdWithFlavor == null) {
            return Result.error("查询失败");

        }
        return Result.success(byIdWithFlavor);
    }

    /**
     * 菜品更新
     * @param dishDto
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody DishDto dishDto) {

        dishService.updateWithFlavor(dishDto);


        return Result.success("修改成功");
    }

    /**
     * 根据传入的数据进行查询菜品的列表信息
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    public Result<List<Dish>> list(Dish dish) {
//
//        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        dishLambdaQueryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
//         //查询状态等于1的 表示在售
//        dishLambdaQueryWrapper.eq(Dish::getStatus,1);
//        //添加排序条件
//        dishLambdaQueryWrapper.orderByDesc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        //返回查询的dish列表
//        List<Dish> list = dishService.list(dishLambdaQueryWrapper);
//
//        return Result.success(list);
//    }
    //利用DishDto 因为前台页面需要使用的口味的数据
    @GetMapping("/list")
    public Result<List<DishDto>> list(Dish dish) {

        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        //查询状态等于1的 表示在售
        dishLambdaQueryWrapper.eq(Dish::getStatus,1);
        //添加排序条件
        dishLambdaQueryWrapper.orderByDesc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        //返回查询的dish列表
        List<Dish> list = dishService.list(dishLambdaQueryWrapper);

        List<DishDto> dishDtoList = new ArrayList<>();
        //遍历这个list中的每个dish 因为每个dish对象中有需要的id值用于去查询dish_flavor表
        list.forEach((item) -> {
            //创建一个dto 拷贝值进去
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            //然后获取菜品的id
            Long dishId = item.getId();

            //更具dishid去查询所有的数据
            LambdaQueryWrapper<DishFlavor> flavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            flavorLambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            //得到当前菜品的所有口味list
            List<DishFlavor> dishFlavorList = dishFlavorService.list(flavorLambdaQueryWrapper);
            //设置到 dto中
            dishDto.setFlavors(dishFlavorList);

            //将dto设置到数组
            dishDtoList.add(dishDto);
        });


        return Result.success(dishDtoList);
    }


}

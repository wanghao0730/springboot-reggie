package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.Result;
import com.itheima.dto.SetmealDto;
import com.itheima.entity.Category;
import com.itheima.entity.Setmeal;
import com.itheima.service.CategoryService;
import com.itheima.service.SetmealDishService;
import com.itheima.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 添加套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public Result<String> addSetmeal(@RequestBody SetmealDto setmealDto) {

        setmealService.saveWithMeal(setmealDto);

        return Result.success("新增套餐成功");
    }

    /**
     * 分页获取套餐的方法
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(int page,int pageSize,String name) {

        //构造setmealpage
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        // 解决存在的问题如果直接响应setmeal对象的话 会存在套餐名称只是id值的情况 我们要根据category_id去查找对应的菜品分类表 表示这个套餐属于哪一类
        Page<SetmealDto> setmealDtoPage = new Page<>();
        //构造查询条件
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.like(StringUtils.hasText(name),Setmeal::getName,name);
        //添加规则
        setmealLambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(setmealPage,setmealLambdaQueryWrapper);
        //进行拷贝对象
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");
        //对records字段进行处理 也就是调用完 setmealService.page 后 从setmealPage中取records
        List<Setmeal> setmealPageRecords = setmealPage.getRecords();
        //遍历这里面的每一个setmeal 然后拿category_id去查找值
        List<SetmealDto> setmealDtoList = new ArrayList<>();
        setmealPageRecords.forEach((item) -> {
            //创建一个dto先接收拷贝的结果
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            //拷贝完然后拿每个item里面的id去查找
            Long categoryId = item.getCategoryId();
            //获取category对象
            Category categoryObj = categoryService.getById(categoryId);
            if (categoryObj != null) {
                String categoryName = categoryObj.getName();
                setmealDto.setCategoryName(categoryName);
            }
            setmealDtoList.add(setmealDto);
        });
        // 把setmealDtoList放回去page中
        setmealDtoPage.setRecords(setmealDtoList);
        return Result.success(setmealDtoPage);
    }


    /**
     *
     * @param ids 传递过来的数据可能是多个可以用List来接受
     * @return
     */
    @DeleteMapping
    public Result<String> deleteSetmeal(@RequestParam List<Long> ids) {

        setmealService.removeWithMeal(ids);
        return Result.success("套餐删除成功");
    }
}

package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.Result;
import com.itheima.entity.Category;
import com.itheima.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    //注入category的service层
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public Result<String> addCategory(@RequestBody Category category) {
        boolean save = categoryService.save(category);
        if (!save) {
            return Result.error("添加失败");
        }
        return Result.success("添加成功");
    }
    /**
     * 菜品的分类接口
     * @param page 当前页码
     * @param pageSize 当前页码显示的条数
     * @return
     */
    @GetMapping("/page")
    public Result<Page> cateList(int page,int pageSize) {
        log.info("当前的页码:{},以及当前的显示条数:{}",page,pageSize);
        //构造分页page对象
        Page<Category> catePageInfo = new Page<>(page, pageSize);

        //如果还有条件查询可以在这里添加各种查询的条件
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        //Category::getCreateTime 相当于获取数据库的字段名称 这里使用降序进行排序
        queryWrapper.orderByDesc(Category::getCreateTime);

        //执行service层的page分页方法 执行完毕后会对传递进去的对象进行复制 所有的数据信息都会保存到原对象中 直接返回前台即可
        categoryService.page(catePageInfo,queryWrapper);
        log.info("查询后的结果数据:{}",catePageInfo);
        return Result.success(catePageInfo);
    }


    @DeleteMapping
    public Result<String> remove(Long ids) {

        //调用自己封装的service层的代码
        categoryService.remove(ids);
        return Result.success("删除菜品成功");
    }

    /**
     * 更新分类的数据
     * @param category
     * @return
     */

    @PutMapping
    public Result<String> updateCate(@RequestBody Category category) {
        log.info("分类修改数据:{}",category);
        boolean updateRes = categoryService.updateById(category);
        if (!updateRes) {
            return Result.error("修改失败,稍后再试");
        }
        return Result.success("修改成功");
    }

    /**
     * 根据传递的类型进行菜品分类的数据返回
     * @param category
     * @return
     */
    @GetMapping("/list")
    public Result<List<Category>> list(Category category) {

        //构造查询
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //先判断下 前台有没有传递type这个类型 eq这个方法 其实就 where XX =XX的意思
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());

        //根据sort排升序 根据更新时间降序
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);

        log.info("查看分类的list结果:{}",list);

        return Result.success(list);
    }
}

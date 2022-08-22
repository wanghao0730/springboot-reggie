package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.Result;
import com.itheima.entity.Employee;
import com.itheima.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@RestController
@RequestMapping("/employee") //设置请求统一的前缀 /employee/后面就是对应下面的方法地址
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // Result是自己封装的响应结果集对象

    /**
     * 员工登录
     * @param servletRequest 登录成功后可以往session中存储数据
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest servletRequest, @RequestBody Employee employee) {

        // 1.将页面提交的密码进行MD5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        log.info("加密后的password:{}",password);

        //2.根据页面提交的用户username查询数据库 查看是否有当前用户
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        //用户名是唯一的所以可以使用getone方法进行查找
        Employee emp = employeeService.getOne(queryWrapper);
        //表示没有找到同名的用户返回结果
        if (emp == null) {
            return Result.error("登录失败,没有当前用户");
        }
        // 如果存在用户，判断用户的密码和传入(经过加密后的密码)比对是否相同
        if (!emp.getPassword().equals(password)) { //表示不相同
            return Result.error("登录失败,密码不正确");
        }
        //判断当前员工是否被禁用
        if (emp.getStatus() == 0) {
            return Result.error("员工被禁用");
        }
        //登录成功将登录状态存储到session中去
        HttpSession session = servletRequest.getSession();
        session.setAttribute("employee",emp.getId());

        //到这一步表示成功登录了 将返回的emp作为结果返回出去
        return Result.success(emp);
    }

    /**
     * 用户退出登录接口
     * @param servletRequest
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest servletRequest) {
        //清除session 把session移除
        servletRequest.getSession().removeAttribute("employee");
        log.info("移除成功");
        return  Result.success("退出成功");
    }

    /**
     * 添加用户的处理
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public Result<String> addUser(HttpServletRequest request,@RequestBody Employee employee) {
        log.info("查看前台传递员工的信息：{}",employee);

        //获取当前创建该账户的管理员号码
        Long empId = (Long) request.getSession().getAttribute("employee");
        log.info("查看管理员的id：{}",empId);
        //往 employee对象中写入默认的密码 md5加密
        String md5Pwd = DigestUtils.md5DigestAsHex("123456".getBytes());
        log.info("查看加密后的密码:{}",md5Pwd);
        //设置创建的用户
        employee.setPassword(md5Pwd);
        //创建人的信息已经在MyMetaObjectHandler类中完成了注入
//        employee.setCreateUser(empId); //存储创建人的信息
//        employee.setUpdateUser(empId); //存储是谁更新的信息

        boolean save = employeeService.save(employee);
        if (!save) {
            return Result.error("添加员工失败");
        }

        return Result.success("添加员工成功");
    }

    /**
     * 返回员工信息的列表
     * @param page 当前页码
     * @param pageSize 当前条数
     * @param name   传递搜索的关键信息
     * @return
     */
    @GetMapping("/page")
    public Result<Page> getEmpList(int page,int pageSize,String name) {
        log.info("查看当前线程:{}",Thread.currentThread().getId());
        //构建个page对象 这个对象包含要响应给前台的数据
        Page<Employee> employeePage = new Page<>(page, pageSize);

        //创建条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //name 不为null才会比较getusername方法和前端传入的name是否匹配的过滤条件,Employee::getUsername相当于获取数据库的表字段username
        queryWrapper.like(StringUtils.hasText(name),Employee::getUsername,name);

        //添加排序的条件 根据更新时间进行降序排序 也就是新的靠前面
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //去数据库查询
        employeeService.page(employeePage,queryWrapper);
        //成功查询后会注入到employeePage这个Page对象中，其中也包含了我们要响应出去的数据

        return Result.success(employeePage);
    }

    /**
     * 更新管理员的状态
     * @return
     */
    @PutMapping
    public Result<String> update(HttpServletRequest request,@RequestBody Employee employee) {
        //获取session中的用户id
        long empId = (long)request.getSession().getAttribute("employee");

        log.info("查看当前线程:{}",Thread.currentThread().getId());
//        更新创建用户的值也通过自动注入进行完成了
//        employee.setUpdateUser(empId);
        boolean updateRes = employeeService.updateById(employee);

        if (!updateRes) {
            return Result.error("更新用户失败");
        }
        //获取
        return Result.success("更新用户成功");

    }

    /**
     * 获取用户的详细信息
     * @return
     */
    @GetMapping("/{id}")
    public Result<Employee> getEmp(@PathVariable("id") Long empId) {
        log.info("获取查询用户的id:{}",empId);
        //获取用户信息
        Employee empRes = employeeService.getById(empId);
        if (empRes == null) {
            return Result.error("查询用户失败");
        }
        return Result.success(empRes);
    }
}

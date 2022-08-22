package com.itheima.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.entity.Employee;
import com.itheima.mapper.EmployeeMapper;
import com.itheima.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * 实现自己的employee的service层 mybatisplus提供了一个类ServiceImpl<M,T> 传入mapper层，以及操作的bean对象，和实现一些自己的接口方法
 * 可以实现mybatisplus提供的一些方法以及自定义扩展自己的方法
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}

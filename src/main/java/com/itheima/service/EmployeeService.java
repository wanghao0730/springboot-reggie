package com.itheima.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.entity.Employee;

// 接口继承 IService 因为IService接口中定义了大量的方法，这里进行继承 service层的实现类进行继承ServiceImpl类的时候就实现了继承类的方法同时也实现了接口中定义需要被实现的方法
public interface EmployeeService extends IService<Employee> {
}

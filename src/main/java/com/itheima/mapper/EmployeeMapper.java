package com.itheima.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * employee的mapper层操作 继承mybatisplus提供的basemapper接口 具有一些常用的数据库操作功能方法
 */
@Mapper //该注解不能忘记 因为在扫描mapper层的时候会扫描带有改注解的才行
public interface EmployeeMapper extends BaseMapper<Employee> {
}

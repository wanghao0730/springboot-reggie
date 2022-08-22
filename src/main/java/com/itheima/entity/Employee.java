package com.itheima.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.assertj.core.util.DateUtil;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * employee表的实体类
 */
@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;  // 身份证号

    private Integer status;

    //fill    字段填充标记 （ FieldFill, 配合自动填充使用 ） FieldFill填充策略 INSERT表示插入填充字段
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    //利用JsonFormat注解 进行json转换 会将从数据库获取的数据(如果没有转换会直接返回时间戳毫秒13位的形式) 利用这个注解就可以进行日期的转换 如果存在时区误差可以使用timezone进行设置
    //插入和更新填充字段
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

}

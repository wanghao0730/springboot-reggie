package com.itheima.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 定义全局的异常处理类
 * 如果类上加有 @RestController、@Controller注解(annotations的属性值)的类中有方法抛出异常，由GlobalExceptionHander来处理异常
 *
 */
//@RestControllerAdvice  也可以声明成rest的控制层advice通知
@Slf4j
@ControllerAdvice(annotations = {RestController.class,Controller.class}) //声明要处理标注的什么样的注解类进行异常捕获
@ResponseBody //将结果封装成JSON数据并返回
public class GlobalExceptionHandler {
    //解决 字段username被唯一索引约束的情况下，添加相同的username，抛出SQLIntegrityConstraintViolationException 的全局异常 因为在employee表中username的字段被标注了唯一
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<String> exceptionHandler(SQLIntegrityConstraintViolationException e) {

        if (e.getMessage().contains("Duplicate entry")) { //抛出的错误信息中有这个关键字的时候进行处理
            String[] splits = e.getMessage().split(" ");
            String msg = splits[2] + "已存在";
            log.info("出错的原因是:{}",msg);
            return Result.error(msg);
        }
        return Result.error("操作失败");
    }

    //全局异常处理除了可以捕获一些常见的异常比如算术异常 或者sql异常，我们还可以捕获自定义的异常(CustomException) 因为当controller层发生了异常错误后会被这里所捕获
    @ExceptionHandler(CustomException.class)
    public Result<String> exceptionHandler(CustomException e) {

        log.error(e.getMessage());
        return Result.error(e.getMessage());
    }
}

package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.itheima.Utils.SendEmailUtils;
import com.itheima.common.CustomException;
import com.itheima.common.Result;
import com.itheima.entity.User;
import com.itheima.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.mockito.internal.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SendEmailUtils sendEmailUtils;
    /**
     * 前台用户登录进行处理
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public Result<String> userLogin(@RequestBody User user, HttpServletRequest request) {

        //获取session
        HttpSession session = request.getSession();

        String randVal = null;
        //返回验证码
        //传入要发送的邮箱地址
        try {
            //返回随机生成的验证码并且帮助我们发送邮箱
             randVal = sendEmailUtils.sendEmail(user.getPhone());
             //存储到seesion 用于后面验证
            session.setAttribute("code",randVal);
        } catch (MessagingException e) {
            throw new CustomException(e.getMessage());
        }

        return Result.success("成功发送验证码");


    }

    @PostMapping("/login")
    public Result<User> userLogin(@RequestBody Map<String,Object> info,HttpServletRequest request) {
        log.info("用户传递的登录数据:{}",info);
        //获取session
        HttpSession session = request.getSession();
        //获取用户的手机号码 和 验证码判断
        String userPhone = (String) info.get("phone");
        String code = (String) info.get("code");

        //先判断用户code和session中的code 不一致的情况
        if (!(code != null && session.getAttribute("code").equals(code))) {
            //清除code值
            session.removeAttribute("code");
            return Result.error("验证码不正确,请重新获取验证码");
        }
        //先判断下当前用户的号码在数据库是否存在是不是新用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.hasText(userPhone),User::getPhone,userPhone);
//        //判断是否存在 用户
        User userServiceOne = userService.getOne(queryWrapper);
        //如果不存在则存储到数据库中当前用户
        if (userServiceOne == null) {
            User user = new User();
            user.setPhone(userPhone);
            //调用存储方法 会自动生成id主键值并且自动注入到bean
            userService.save(user);
            //将当前用户存储到session中 并且返回出去
            session.setAttribute("user",user.getId());
            //清除code值
            session.removeAttribute("code");
            // 将当前用户的信息进行返回处理
            return Result.success(user);
        }
        //设置当前用户的信息到session中
        session.setAttribute("user",userServiceOne.getId());
        session.removeAttribute("code");
//        //表示当前用户存在
        return Result.success(userServiceOne);
    }
}

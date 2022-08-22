package com.itheima.Utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Random;

@Slf4j
@Component
public class SendEmailUtils {

    @Autowired
    private JavaMailSenderImpl mailSender;

    //从配置文件中获取由谁发送的数据
    @Value("${spring.mail.username}")
    private String sendFromUser;

    /**
     * 邮箱发送功能
     * @param sendToUser 传入要发送的用户
     * @return 返回随机生成的状态码
     * @throws MessagingException
     */
    public String sendEmail(String sendToUser) throws MessagingException {
        log.info("查看valu注入:{}",sendFromUser);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        //调用工具生成验证码
        String randomVal = ValidateCodeUtils.generateValidateCode4String(4);
        //标题
        helper.setSubject("您的验证码为：" + randomVal);
        //内容
        helper.setText("您好！感谢登录外卖系统。您的验证码为：" + "<h2>" + randomVal + "</h2>" + "不能告诉别人哟！", true);
        //邮件接收者
        helper.setTo(sendToUser);
        //邮件发送者，必须和配置文件里的一样，不然授权码匹配不上
        helper.setFrom(sendFromUser);
        mailSender.send(mimeMessage);
        return randomVal;
    }
}

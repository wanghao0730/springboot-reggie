package com.itheima;

import com.itheima.Utils.SendEmailUtils;
import com.itheima.Utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Random;


@Slf4j
@SpringBootTest
public class SpringBootTestEmailSend {

    @Autowired
    JavaMailSenderImpl mailSender;

    @Autowired
    SendEmailUtils sendEmailUtils;

    @DisplayName("邮箱发送测试")
    @Test
    void contextLoads() throws MessagingException {
        int count = 1;//默认发送一次
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        while (count-- != 0) {
            String codeNum = "";
            int[] code = new int[3];
            Random random = new Random();
            //自动生成验证码
            for (int i = 0; i < 6; i++) {
                int num = random.nextInt(10) + 48;
                int uppercase = random.nextInt(26) + 65;
                int lowercase = random.nextInt(26) + 97;
                code[0] = num;
                code[1] = uppercase;
                code[2] = lowercase;
                codeNum += (char) code[random.nextInt(3)];
            }
            System.out.println(codeNum);
            //标题
            helper.setSubject("您的验证码为：" + codeNum);
            //内容
            helper.setText("您好！感谢登录外卖系统。您的验证码为：" + "<h2>" + codeNum + "</h2>" + "千万不能告诉别人哦！", true);
            //邮件接收者
            helper.setTo("1149243584@qq.com");
            //邮件发送者，必须和配置文件里的一样，不然授权码匹配不上
            helper.setFrom("1149243584@qq.com");
            mailSender.send(mimeMessage);
            System.out.println("邮件发送成功！" + (count + 1));
        }
    }

    @DisplayName("测试生成的随机数值")
    @Test
    void testRandomVal() throws MessagingException {
//        Integer integer = ValidateCodeUtils.generateValidateCode(4);
////        log.info("生成的随机值:{}",integer);
//        sendEmailUtils
        sendEmailUtils.sendEmail("1149243584@qq.com");
    }
}

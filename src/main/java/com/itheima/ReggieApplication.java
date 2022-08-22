package com.itheima;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//lombok提供的方法 可以打印数据
@Slf4j
@SpringBootApplication
@ServletComponentScan //用于扫描原生的filter和 servlet 以及listener
@EnableTransactionManagement //开启事务支持
public class ReggieApplication {
    public static void main(String[] args) {
        //启动springboot应用
        SpringApplication.run(ReggieApplication.class,args);
        log.info("springboot启动了");
    }
}

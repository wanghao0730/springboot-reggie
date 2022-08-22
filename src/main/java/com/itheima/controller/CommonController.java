package com.itheima.controller;

import com.itheima.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * 文件上传/下载
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {


    //从配置文件中读取数据
    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传处理
     * @param file 对应前台的字段name='file'
     * @return
     */
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        //file是个临时文件，需要转存到指定位置，否则本次请求结束后临时文件就清除了
        log.info(file.toString());
        //从file对象中可以获取原始文件名
        String originalFilename = file.getOriginalFilename();
        //获取.jpg的位置进行截取 因为下面要用UUID生成的值.jpg/.png
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        log.info("截取后的后缀:{}",suffix);
//        直接使用原始文件名会有上传名字重复的问题可以使用UUID进行随机生成文件名
        String filename = UUID.randomUUID().toString() + suffix;

        //在配置文件中如果配置的路径是D:\img 有可能在硬盘中不存在当前img目录所以basePath就会出现拼接的错误 所以可以先进行一个判断是否存在这个目录如果不存在创建即可
        File dir = new File(basePath);

        if (!dir.exists()) { //如果不存在目录就创建个目录
            dir.mkdirs(); //创建目录
        }
        try {
            file.transferTo(new File(basePath + filename));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //文件上传成功后要回显 返回这个文件名称回去
        return Result.success(filename);
    }

    /**
     * 文件下载接口
     * @param name  要下载的文件名称
     * @param response 使用输出流输出结果
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {

        try {
            //改判断图片存在与否代码后期要修改
            File file = new File(basePath + name);
            if (!file.exists()) {
                log.info("改文件不存在:{}",name);
                return;
            }
            //
            //new File(basePath + name) 根据传入的文件名读取文件
            //输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(file);


            //输出流,通过输出流将文件写回浏览器，在浏览器展示图片 通过响应对象获取输出流
            ServletOutputStream outputStream = response.getOutputStream();

            //设置响应头返回数据类型
            response.setContentType("image/jpeg");

            //读取文件
            int len = 0; //记录长度
            byte[] bytes = new byte[1024]; //每次读取的数据将存储到这个bytes数组中
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            fileInputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}

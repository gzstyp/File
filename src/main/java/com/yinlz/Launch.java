package com.yinlz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * 文件上传服务，上传到指定目录,通过 nginx 访问,配置请看
 * @param
 * @作者 田应平
 * @QQ 444141300
 * @创建时间 2019/9/30 16:25
*/
@SpringBootApplication
public class Launch extends SpringBootServletInitializer{

    public static void main(final String[] args){
        SpringApplication.run(Launch.class,args);
        System.out.println("--应用启动成功--");
    }

    /**打包war时能正常运行*/
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Launch.class);
    }
}
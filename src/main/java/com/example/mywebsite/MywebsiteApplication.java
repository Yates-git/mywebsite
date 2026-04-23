package com.example.mywebsite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * MywebsiteApplication.java - 程序入口
 *
 * 这个类的特点：
 * 1. 有 @SpringBootApplication 注解
 * 2. 包含 main() 方法
 *
 * 作用：启动整个网站项目
 */
@SpringBootApplication
public class MywebsiteApplication {

    public static void main(String[] args) {
        // SpringApplication.run() 启动 Spring Boot 项目
        // 启动后会自动扫描当前包及其子包，找到所有控制器、配置等
        SpringApplication.run(MywebsiteApplication.class, args);
    }
}

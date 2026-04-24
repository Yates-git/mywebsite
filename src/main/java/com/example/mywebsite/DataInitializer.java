package com.example.mywebsite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * DataInitializer.java - 数据初始化器
 *
 * 作用：
 * - 应用启动时自动执行
 * - 创建默认管理员账号（如果不存在）
 * - 确保数据库中有初始数据
 *
 * 实现 CommandLineRunner 接口的应用会在 Spring 容器初始化完成后执行
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // 检查是否已存在 admin 用户
        Optional<User> existingAdmin = userRepository.findByUsername("admin");

        if (existingAdmin.isEmpty()) {
            // 创建默认管理员账号
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("123456");
            admin.setIsAdmin(1);  // 1 = 管理员
            admin.setIsDeleted(0); // 0 = 未删除
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());

            userRepository.save(admin);

            System.out.println("========================================");
            System.out.println("默认管理员账号已创建:");
            System.out.println("  用户名: admin");
            System.out.println("  密码: 123456");
            System.out.println("========================================");
        } else {
            System.out.println("========================================");
            System.out.println("管理员账号已存在，跳过初始化");
            System.out.println("========================================");
        }
    }
}
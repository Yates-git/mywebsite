package com.example.mywebsite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

/**
 * DataInitializer.java - 数据初始化器
 *
 * 作用：
 * - 应用启动时自动执行
 * - 创建默认管理员账号（如果不存在）
 * - 初始化默认页面（用于权限管理）
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PageRepository pageRepository;

    @Override
    public void run(String... args) throws Exception {
        // 初始化管理员账号
        initAdminUser();

        // 初始化默认页面
        initDefaultPages();
    }

    /**
     * 初始化管理员账号
     */
    private void initAdminUser() {
        Optional<User> existingAdmin = userRepository.findByUsername("admin");

        if (existingAdmin.isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("123456");
            admin.setIsAdmin(1);
            admin.setIsDeleted(0);
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

    /**
     * 初始化默认页面（用于权限管理）
     * 注意：后台管理页面不在此列表中，只有管理员可以访问
     */
    private void initDefaultPages() {
        // 定义默认页面：路径、名称、是否系统页面、排序
        Object[][] defaultPages = {
            {"/main", "首页", true, 1},
        };

        for (Object[] pageData : defaultPages) {
            String path = (String) pageData[0];
            String name = (String) pageData[1];
            Boolean isSystem = (Boolean) pageData[2];
            Integer sortOrder = (Integer) pageData[3];

            if (!pageRepository.existsByPath(path)) {
                Page page = new Page(name, path, isSystem, sortOrder);
                pageRepository.save(page);
                System.out.println("默认页面已创建: " + name + " (" + path + ")");
            }
        }

        long pageCount = pageRepository.count();
        if (pageCount > 0) {
            System.out.println("========================================");
            System.out.println("已初始化 " + pageCount + " 个默认页面");
            System.out.println("========================================");
        }
    }
}

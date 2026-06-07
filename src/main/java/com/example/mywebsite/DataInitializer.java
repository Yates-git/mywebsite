package com.example.mywebsite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * DataInitializer.java - 数据初始化器
 *
 * 作用：
 * - 应用启动时自动执行
 * - 创建默认管理员账号（如果不存在）
 * - 初始化默认页面（用于权限管理）
 * - 启动时确保数据/日志目录存在
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PageRepository pageRepository;

    @Override
    public void run(String... args) throws Exception {
        // 启动前确保数据与日志目录存在
        ensureDirectories();

        // 初始化管理员账号
        initAdminUser();

        // 初始化默认页面
        initDefaultPages();
    }

    /**
     * 确保数据目录和日志目录存在
     * Spring Boot 的 logging.file.name 不会自动创建父目录
     */
    private void ensureDirectories() throws java.io.IOException {
        Files.createDirectories(Paths.get("./data"));
        Files.createDirectories(Paths.get("./logs"));
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

            userRepository.save(admin);

            log.info("========================================");
            log.info("默认管理员账号已创建:");
            log.info("  用户名: admin");
            log.info("  密码: 123456");
            log.info("========================================");
        } else {
            log.info("========================================");
            log.info("管理员账号已存在，跳过初始化");
            log.info("========================================");
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

        int created = 0;
        for (Object[] pageData : defaultPages) {
            String path = (String) pageData[0];
            String name = (String) pageData[1];
            Boolean isSystem = (Boolean) pageData[2];
            Integer sortOrder = (Integer) pageData[3];

            if (!pageRepository.existsByPath(path)) {
                Page page = new Page(name, path, isSystem, sortOrder);
                pageRepository.save(page);
                created++;
                log.info("默认页面已创建: {} ({})", name, path);
            }
        }

        if (created > 0) {
            log.info("========================================");
            log.info("本次启动新创建 {} 个默认页面", created);
            log.info("========================================");
        }
    }
}

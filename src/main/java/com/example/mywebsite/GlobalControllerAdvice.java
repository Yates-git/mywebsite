package com.example.mywebsite;

import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * GlobalControllerAdvice.java - 全局控制器通知
 *
 * favicon 已迁移到 src/main/resources/static/favicon.svg 静态资源
 * 此处仅作为扩展点保留（未来可在此统一填充全局 model 属性）
 */
@ControllerAdvice
public class GlobalControllerAdvice {
    // 当前无全局属性需要注入；占位以备后续扩展
}

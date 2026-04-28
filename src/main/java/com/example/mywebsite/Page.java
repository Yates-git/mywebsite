package com.example.mywebsite;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Page.java - 页面实体
 * 
 * 用于权限管理，记录系统中的所有一级页面
 * 注意：后台管理页面不在此表中，只有管理员可以访问
 */
@Entity
@Table(name = "page")
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 页面名称（显示用）
     */
    @Column(nullable = false)
    private String name;

    /**
     * 页面路径（唯一，用于权限判断）
     */
    @Column(nullable = false, unique = true)
    private String path;

    /**
     * 是否为系统页面（系统页面不可删除）
     */
    @Column(name = "is_system")
    private Boolean isSystem = false;

    /**
     * 排序顺序
     */
    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // -------------------- 构造函数 --------------------
    public Page() {
    }

    public Page(String name, String path, Boolean isSystem, Integer sortOrder) {
        this.name = name;
        this.path = path;
        this.isSystem = isSystem;
        this.sortOrder = sortOrder;
    }

    // -------------------- 生命周期回调 --------------------
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // -------------------- getter 和 setter --------------------
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(Boolean isSystem) {
        this.isSystem = isSystem;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

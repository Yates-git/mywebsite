package com.example.mywebsite;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Group.java - 用户分组实体
 * 
 * 用于将用户划分到不同的组中，实现分组管理和权限控制
 */
@Entity
@Table(name = "user_group")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 分组名称 - 必填，唯一
     */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * 分组描述
     */
    private String description;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 修改时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // -------------------- 构造函数 --------------------
    public Group() {
    }

    public Group(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // -------------------- 生命周期回调 --------------------
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

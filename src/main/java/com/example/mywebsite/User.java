package com.example.mywebsite;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * User.java - 用户实体类
 *
 * 什么是实体类？
 * - 用来描述一个"用户"的数据结构
 * - 包含用户的属性（用户名、密码）
 * - 提供 get/set 方法让其他代码读取或修改这些属性
 *
 * 通俗理解：User 就像一张"用户卡片"，记录用户的各种信息
 *
 * JPA 注解说明：
 * - @Entity          : 标记这个类是数据库实体
 * - @Table           : 指定对应的数据库表名
 * - @Id              : 标记主键字段
 * - @GeneratedValue  : 主键自动生成策略
 * - @Column          : 指定数据库列名和属性
 */
@Entity
@Table(name = "user")
public class User {

    // -------------------- 数据库字段 --------------------

    /**
     * 用户ID - 主键
     * 使用自增策略
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户名 - 必填，唯一
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * 密码 - 必填
     */
    @Column(nullable = false)
    private String password;

    /**
     * 是否管理员
     * 0: 普通用户
     * 1: 管理员
     */
    @Column(name = "is_admin")
    private Integer isAdmin = 0;

    /**
     * 是否删除（软删除标记）
     * 0: 未删除
     * 1: 已删除
     */
    @Column(name = "is_deleted")
    private Integer isDeleted = 0;

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

    // 无参构造函数 - JPA 必须有
    public User() {
    }

    // 有参构造函数 - 方便快速创建用户对象
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // -------------------- 生命周期回调 --------------------
    // 在保存之前自动设置创建时间和修改时间
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    // 在更新之前自动设置修改时间
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Integer isAdmin) {
        this.isAdmin = isAdmin;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
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

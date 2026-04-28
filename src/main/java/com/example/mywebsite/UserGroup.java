package com.example.mywebsite;

import jakarta.persistence.*;

/**
 * UserGroup.java - 用户分组关联实体
 * 
 * 记录用户与分组的关联关系（多对多）
 */
@Entity
@Table(name = "user_user_group")
public class UserGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 分组ID
     */
    @Column(name = "group_id", nullable = false)
    private Long groupId;

    // -------------------- 构造函数 --------------------
    public UserGroup() {
    }

    public UserGroup(Long userId, Long groupId) {
        this.userId = userId;
        this.groupId = groupId;
    }

    // -------------------- getter 和 setter --------------------
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
}

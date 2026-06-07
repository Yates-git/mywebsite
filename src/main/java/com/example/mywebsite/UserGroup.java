package com.example.mywebsite;

import jakarta.persistence.*;

/**
 * UserGroup.java - 用户分组关联实体
 * 
 * 记录用户与分组的关联关系（多对多）
 */
@Entity
@Table(name = "user_user_group", indexes = {
    // 复合唯一索引：同一用户在同一分组中只能存在一条
    @Index(name = "idx_ug_user_group", columnList = "user_id, group_id", unique = true),
    // 反向索引：按分组 ID 查找成员
    @Index(name = "idx_ug_group_user", columnList = "group_id, user_id")
})
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

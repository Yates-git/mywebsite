package com.example.mywebsite;

import jakarta.persistence.*;

/**
 * GroupPermission.java - 分组权限实体
 * 
 * 记录每个分组可以访问的页面（多对多关系）
 */
@Entity
@Table(name = "group_permission", indexes = {
    // 复合唯一索引：同一分组对同一页面只能有一条权限记录
    @Index(name = "idx_gp_group_page", columnList = "group_id, page_id", unique = true),
    // 反向索引：删除页面时按 page_id 清理
    @Index(name = "idx_gp_page_group", columnList = "page_id, group_id")
})
public class GroupPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 分组ID
     */
    @Column(name = "group_id", nullable = false)
    private Long groupId;

    /**
     * 页面ID
     */
    @Column(name = "page_id", nullable = false)
    private Long pageId;

    // -------------------- 构造函数 --------------------
    public GroupPermission() {
    }

    public GroupPermission(Long groupId, Long pageId) {
        this.groupId = groupId;
        this.pageId = pageId;
    }

    // -------------------- getter 和 setter --------------------
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getPageId() {
        return pageId;
    }

    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }
}

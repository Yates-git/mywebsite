package com.example.mywebsite;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * GroupPermissionRepository.java - 分组权限数据访问层
 */
@Repository
public interface GroupPermissionRepository extends JpaRepository<GroupPermission, Long> {

    /**
     * 根据分组ID查找所有权限
     */
    List<GroupPermission> findByGroupId(Long groupId);

    /**
     * 根据页面ID查找所有分组权限
     */
    List<GroupPermission> findByPageId(Long pageId);

    /**
     * 检查某个分组是否有某个页面的权限
     */
    boolean existsByGroupIdAndPageId(Long groupId, Long pageId);

    /**
     * 删除分组的所有权限
     */
    @Modifying
    @Query("DELETE FROM GroupPermission gp WHERE gp.groupId = :groupId")
    void deleteByGroupId(@Param("groupId") Long groupId);

    /**
     * 删除页面所有权限
     */
    @Modifying
    @Query("DELETE FROM GroupPermission gp WHERE gp.pageId = :pageId")
    void deleteByPageId(@Param("pageId") Long pageId);
}

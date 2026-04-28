package com.example.mywebsite;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * UserGroupRepository.java - 用户分组关联数据访问层
 */
@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {

    /**
     * 根据用户ID查找所有分组
     */
    List<UserGroup> findByUserId(Long userId);

    /**
     * 根据分组ID查找所有用户
     */
    List<UserGroup> findByGroupId(Long groupId);

    /**
     * 检查用户是否在某分组中
     */
    boolean existsByUserIdAndGroupId(Long userId, Long groupId);

    /**
     * 删除用户的所有分组关联
     */
    @Modifying
    @Query("DELETE FROM UserGroup ug WHERE ug.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    /**
     * 删除分组的所有用户关联
     */
    @Modifying
    @Query("DELETE FROM UserGroup ug WHERE ug.groupId = :groupId")
    void deleteByGroupId(@Param("groupId") Long groupId);
}

package com.example.mywebsite;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
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
     * 批量查询：一次拉取多个用户的所有分组关联
     * 用于避免 N+1（按用户循环单查）
     */
    List<UserGroup> findAllByUserIdIn(Collection<Long> userIds);

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

    /**
     * 按用户 + 分组直接删除单条关联（带返回影响行数）
     * 用于避免先 fetch 再判断再 delete 的 N+1
     */
    @Modifying
    @Query("DELETE FROM UserGroup ug WHERE ug.userId = :userId AND ug.groupId = :groupId")
    int deleteByUserIdAndGroupId(@Param("userId") Long userId, @Param("groupId") Long groupId);
}

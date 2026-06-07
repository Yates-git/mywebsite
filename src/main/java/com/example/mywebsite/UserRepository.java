package com.example.mywebsite;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * UserRepository.java - 用户数据访问层
 *
 * 什么是 Repository？
 * - 负责与数据库交互
 * - 提供增删改查的方法
 * - 继承 JpaRepository 自动获得常用方法
 *
 * 常用方法：
 * - findById(id)           : 根据 ID 查询
 * - findByUsername(name)   : 根据用户名查询
 * - save(user)             : 保存或更新用户
 * - deleteById(id)         : 根据 ID 删除
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查询用户
     * Spring Data JPA 会自动根据方法名生成 SQL
     *
     * @param username 用户名
     * @return 找到返回 Optional<User>，找不到返回空
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据用户名查询未删除的用户（软删除支持）
     *
     * @param username   用户名
     * @param isDeleted  删除标记（0 表示未删除）
     * @return 找到返回 Optional<User>，找不到返回空
     */
    Optional<User> findByUsernameAndIsDeleted(String username, Integer isDeleted);

    /**
     * 批量查询：返回 ID 不在指定集合中的用户
     * 用于"不在某分组中的用户"等 NOT IN 场景，避免拉全表
     */
    @Query("SELECT u FROM User u WHERE u.id NOT IN :ids")
    List<User> findByIdNotIn(@Param("ids") Collection<Long> ids);
}

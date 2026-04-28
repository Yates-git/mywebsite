package com.example.mywebsite;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * GroupRepository.java - 分组数据访问层
 */
@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    /**
     * 根据分组名称查找
     */
    Optional<Group> findByName(String name);

    /**
     * 检查分组名称是否已存在
     */
    boolean existsByName(String name);
}

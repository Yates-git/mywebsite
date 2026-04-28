package com.example.mywebsite;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * PageRepository.java - 页面数据访问层
 */
@Repository
public interface PageRepository extends JpaRepository<Page, Long> {

    /**
     * 根据路径查找页面
     */
    Optional<Page> findByPath(String path);

    /**
     * 检查路径是否已存在
     */
    boolean existsByPath(String path);

    /**
     * 获取所有页面，按排序顺序
     */
    List<Page> findAllByOrderBySortOrderAsc();
}

package com.example.mywebsite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PermissionService.java - 权限服务
 */
@Service
public class PermissionService {

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupPermissionRepository groupPermissionRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    /**
     * 获取所有页面
     */
    public List<Page> getAllPages() {
        return pageRepository.findAllByOrderBySortOrderAsc();
    }

    /**
     * 获取所有分组
     */
    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    /**
     * 添加新页面（当新增一级页面时调用）
     */
    @Transactional
    public Page addPage(String name, String path, Integer sortOrder) {
        if (pageRepository.existsByPath(path)) {
            return null;
        }
        Page page = new Page(name, path, false, sortOrder);
        return pageRepository.save(page);
    }

    /**
     * 获取分组的所有页面权限
     */
    public List<Long> getGroupPageIds(Long groupId) {
        return groupPermissionRepository.findByGroupId(groupId).stream()
                .map(GroupPermission::getPageId)
                .collect(Collectors.toList());
    }

    /**
     * 设置分组的页面权限
     */
    @Transactional
    public void setGroupPermissions(Long groupId, List<Long> pageIds) {
        // 删除分组现有的权限
        groupPermissionRepository.deleteByGroupId(groupId);
        // 创建新的权限
        for (Long pageId : pageIds) {
            GroupPermission gp = new GroupPermission(groupId, pageId);
            groupPermissionRepository.save(gp);
        }
    }

    /**
     * 检查用户是否有权限访问某个页面
     * 管理员始终有权限
     */
    public boolean hasPagePermission(Long userId, Integer isAdmin, String pagePath) {
        // 管理员有所有权限
        if (isAdmin != null && isAdmin == 1) {
            return true;
        }

        // 后台管理页面只有管理员能访问
        if (pagePath.startsWith("/admin/")) {
            return false;
        }

        // 获取用户所属的所有分组
        List<UserGroup> userGroups = userGroupRepository.findByUserId(userId);
        if (userGroups.isEmpty()) {
            // 用户没有分组，默认只能访问首页
            return "/main".equals(pagePath);
        }

        // 检查用户是否有该页面的权限
        List<Long> groupIds = userGroups.stream()
                .map(UserGroup::getGroupId)
                .collect(Collectors.toList());

        // 查找该页面
        var pageOpt = pageRepository.findByPath(pagePath);
        if (pageOpt.isEmpty()) {
            // 页面不存在于权限表中，默认允许访问
            return true;
        }
        Long pageId = pageOpt.get().getId();

        // 检查是否有分组拥有该页面的权限
        for (Long groupId : groupIds) {
            if (groupPermissionRepository.existsByGroupIdAndPageId(groupId, pageId)) {
                return true;
            }
        }

        // 默认只能访问首页
        return "/main".equals(pagePath);
    }

    /**
     * 删除页面时同时删除相关权限
     */
    @Transactional
    public void deletePage(Long pageId) {
        groupPermissionRepository.deleteByPageId(pageId);
        pageRepository.deleteById(pageId);
    }
}

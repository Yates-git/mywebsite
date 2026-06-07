package com.example.mywebsite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * GroupService.java - 分组服务
 */
@Service
@Transactional(readOnly = true) // 类级只读事务，写方法通过 @Transactional 覆盖
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 获取所有分组
     */
    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    /**
     * 根据ID获取分组
     */
    public Optional<Group> getGroupById(Long id) {
        return groupRepository.findById(id);
    }

    /**
     * 创建分组
     */
    @Transactional
    public Group createGroup(String name, String description) {
        if (groupRepository.existsByName(name)) {
            return null;
        }
        Group group = new Group(name, description);
        return groupRepository.save(group);
    }

    /**
     * 更新分组
     */
    @Transactional
    public boolean updateGroup(Long id, String name, String description) {
        Optional<Group> groupOpt = groupRepository.findById(id);
        if (groupOpt.isEmpty()) {
            return false;
        }
        Group group = groupOpt.get();
        group.setName(name);
        group.setDescription(description);
        groupRepository.save(group);
        return true;
    }

    /**
     * 删除分组
     */
    @Transactional
    public boolean deleteGroup(Long id) {
        if (!groupRepository.existsById(id)) {
            return false;
        }
        // 删除分组的所有用户关联
        userGroupRepository.deleteByGroupId(id);
        // 删除分组
        groupRepository.deleteById(id);
        return true;
    }

    /**
     * 获取用户所属的所有分组
     */
    public List<Group> getUserGroups(Long userId) {
        List<UserGroup> userGroups = userGroupRepository.findByUserId(userId);
        List<Long> groupIds = userGroups.stream()
                .map(UserGroup::getGroupId)
                .collect(Collectors.toList());
        return groupRepository.findAllById(groupIds);
    }

    /**
     * 获取分组的所有用户
     */
    public List<User> getGroupUsers(Long groupId) {
        List<UserGroup> userGroups = userGroupRepository.findByGroupId(groupId);
        List<Long> userIds = userGroups.stream()
                .map(UserGroup::getUserId)
                .collect(Collectors.toList());
        return userRepository.findAllById(userIds);
    }

    /**
     * 获取不在某个分组中的所有用户（用于添加用户到分组）
     *
     * 优化前：拉全表 User 再内存过滤
     * 优化后：NOT IN 一次 SQL 搞定
     */
    public List<User> getUsersNotInGroup(Long groupId) {
        List<UserGroup> userGroups = userGroupRepository.findByGroupId(groupId);
        List<Long> existingUserIds = userGroups.stream()
                .map(UserGroup::getUserId)
                .collect(Collectors.toList());
        if (existingUserIds.isEmpty()) {
            return userRepository.findAll();
        }
        return userRepository.findByIdNotIn(existingUserIds);
    }

    /**
     * 添加用户到分组
     */
    @Transactional
    public boolean addUserToGroup(Long userId, Long groupId) {
        if (!userRepository.existsById(userId) || !groupRepository.existsById(groupId)) {
            return false;
        }
        if (userGroupRepository.existsByUserIdAndGroupId(userId, groupId)) {
            return true; // 已在分组中
        }
        UserGroup ug = new UserGroup(userId, groupId);
        userGroupRepository.save(ug);
        return true;
    }

    /**
     * 从分组移除用户
     *
     * 优化前：fetch 该用户所有 UserGroup，循环找匹配再删除
     * 优化后：直接 DELETE WHERE user_id=? AND group_id=? 看影响行数
     */
    @Transactional
    public boolean removeUserFromGroup(Long userId, Long groupId) {
        int affected = userGroupRepository.deleteByUserIdAndGroupId(userId, groupId);
        return affected > 0;
    }
}

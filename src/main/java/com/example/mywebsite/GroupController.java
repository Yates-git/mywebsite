package com.example.mywebsite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;

/**
 * GroupController.java - 分组管理控制器
 */
@Controller
public class GroupController {

    @Autowired
    private GroupService groupService;

    /**
     * 检查是否为管理员
     */
    private boolean isAdmin(HttpSession session) {
        Integer isAdmin = (Integer) session.getAttribute("isAdmin");
        return isAdmin != null && isAdmin == 1;
    }

    /**
     * 检查是否已登录
     */
    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("loginUser") != null;
    }

    /**
     * 分组管理页面
     */
    @GetMapping("/admin/groups")
    public String groupManage(HttpSession session, Model model) {
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }
        if (!isAdmin(session)) {
            return "redirect:/main";
        }

        List<Group> groups = groupService.getAllGroups();
        model.addAttribute("username", session.getAttribute("loginUser"));
        model.addAttribute("isAdmin", true);
        model.addAttribute("groups", groups);

        return "admin/groupManage";
    }

    /**
     * 创建分组
     */
    @PostMapping("/admin/group/create")
    public String createGroup(
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isLoggedIn(session) || !isAdmin(session)) {
            return "redirect:/";
        }

        Group group = groupService.createGroup(name, description);
        if (group == null) {
            redirectAttributes.addFlashAttribute("error", "分组名称已存在");
        } else {
            redirectAttributes.addFlashAttribute("success", "分组 '" + name + "' 创建成功");
        }

        return "redirect:/admin/groups";
    }

    /**
     * 删除分组
     */
    @GetMapping("/admin/group/delete/{id}")
    public String deleteGroup(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isLoggedIn(session) || !isAdmin(session)) {
            return "redirect:/";
        }

        boolean success = groupService.deleteGroup(id);
        if (success) {
            redirectAttributes.addFlashAttribute("success", "分组删除成功");
        } else {
            redirectAttributes.addFlashAttribute("error", "分组删除失败");
        }

        return "redirect:/admin/groups";
    }

    /**
     * 更新分组
     */
    @PostMapping("/admin/group/update")
    public String updateGroup(
            @RequestParam("id") Long id,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isLoggedIn(session) || !isAdmin(session)) {
            return "redirect:/";
        }

        boolean success = groupService.updateGroup(id, name, description);
        if (success) {
            redirectAttributes.addFlashAttribute("success", "分组更新成功");
        } else {
            redirectAttributes.addFlashAttribute("error", "分组更新失败");
        }

        return "redirect:/admin/groups";
    }

    /**
     * 分组详情页面（查看组成员）
     */
    @GetMapping("/admin/group/{id}")
    public String groupDetail(
            @PathVariable Long id,
            HttpSession session,
            Model model) {

        if (!isLoggedIn(session) || !isAdmin(session)) {
            return "redirect:/";
        }

        var groupOpt = groupService.getGroupById(id);
        if (groupOpt.isEmpty()) {
            return "redirect:/admin/groups";
        }

        List<User> members = groupService.getGroupUsers(id);
        List<User> allUsers = groupService.getUsersNotInGroup(id);

        model.addAttribute("username", session.getAttribute("loginUser"));
        model.addAttribute("isAdmin", true);
        model.addAttribute("group", groupOpt.get());
        model.addAttribute("members", members);
        model.addAttribute("otherUsers", allUsers);

        return "admin/groupDetail";
    }

    /**
     * 添加用户到分组
     */
    @PostMapping("/admin/group/{id}/addUser")
    public String addUserToGroup(
            @PathVariable Long id,
            @RequestParam("userId") Long userId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isLoggedIn(session) || !isAdmin(session)) {
            return "redirect:/";
        }

        groupService.addUserToGroup(userId, id);
        return "redirect:/admin/group/" + id;
    }

    /**
     * 从分组移除用户
     */
    @GetMapping("/admin/group/{groupId}/removeUser/{userId}")
    public String removeUserFromGroup(
            @PathVariable Long groupId,
            @PathVariable Long userId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isLoggedIn(session) || !isAdmin(session)) {
            return "redirect:/";
        }

        groupService.removeUserFromGroup(userId, groupId);
        return "redirect:/admin/group/" + groupId;
    }
}

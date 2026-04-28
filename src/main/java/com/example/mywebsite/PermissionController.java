package com.example.mywebsite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PermissionController.java - 权限管理控制器
 */
@Controller
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private PageRepository pageRepository;

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
     * 权限管理页面
     */
    @GetMapping("/admin/permissions")
    public String permissionManage(HttpSession session, Model model) {
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }
        if (!isAdmin(session)) {
            return "redirect:/main";
        }

        List<Group> groups = permissionService.getAllGroups();
        List<Page> pages = permissionService.getAllPages();

        // 获取首页ID，确保首页权限始终存在
        Long mainPageId = null;
        for (Page page : pages) {
            if ("/main".equals(page.getPath())) {
                mainPageId = page.getId();
                break;
            }
        }

        // 获取每个分组的页面权限
        Map<Long, List<Long>> groupPermissions = new HashMap<>();
        for (Group group : groups) {
            List<Long> pageIds = permissionService.getGroupPageIds(group.getId());
            // 确保首页权限始终包含在内
            if (mainPageId != null && !pageIds.contains(mainPageId)) {
                pageIds.add(mainPageId);
            }
            groupPermissions.put(group.getId(), pageIds);
        }

        model.addAttribute("username", session.getAttribute("loginUser"));
        model.addAttribute("isAdmin", true);
        model.addAttribute("groups", groups);
        model.addAttribute("pages", pages);
        model.addAttribute("groupPermissions", groupPermissions);

        return "admin/permissionManage";
    }

    /**
     * 更新分组权限
     */
    @PostMapping("/admin/permission/update")
    public String updatePermission(
            @RequestParam("groupId") Long groupId,
            @RequestParam(value = "pageIds", required = false) List<Long> pageIds,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isLoggedIn(session) || !isAdmin(session)) {
            return "redirect:/";
        }

        if (pageIds == null) {
            pageIds = List.of();
        }

        // 确保首页权限始终被包含
        Page mainPage = pageRepository.findByPath("/main").orElse(null);
        if (mainPage != null && !pageIds.contains(mainPage.getId())) {
            pageIds.add(mainPage.getId());
        }

        permissionService.setGroupPermissions(groupId, pageIds);
        redirectAttributes.addFlashAttribute("success", "权限更新成功");

        return "redirect:/admin/permissions";
    }

    /**
     * 添加新页面（系统管理员手动添加新的页面配置）
     */
    @PostMapping("/admin/page/add")
    public String addPage(
            @RequestParam("name") String name,
            @RequestParam("path") String path,
            @RequestParam(value = "sortOrder", defaultValue = "99") Integer sortOrder,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isLoggedIn(session) || !isAdmin(session)) {
            return "redirect:/";
        }

        // 自动添加 /main 首页
        if ("/main".equals(path)) {
            redirectAttributes.addFlashAttribute("error", "/main 是系统保留页面，不能添加");
            return "redirect:/admin/permissions";
        }

        Page page = permissionService.addPage(name, path, sortOrder);
        if (page == null) {
            redirectAttributes.addFlashAttribute("error", "页面路径已存在");
        } else {
            redirectAttributes.addFlashAttribute("success", "页面 '" + name + "' 添加成功");
        }

        return "redirect:/admin/permissions";
    }
}

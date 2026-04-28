package com.example.mywebsite;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import com.example.mywebsite.User;
import com.example.mywebsite.Group;

/**
 * Controller.java - 控制器
 *
 * 什么是控制器？
 * - 负责处理用户的请求
 * - 接收用户输入，返回相应页面
 *
 * 想象餐厅：
 * - 客人（用户）点菜（发送请求）
 * - 服务员（控制器）接收订单
 * - 厨房（Service/数据库）做菜
 * - 服务员端菜回来（返回页面）
 *
 * 常用注解说明：
 * - @Controller        : 标记这个类是控制器
 * - @GetMapping        : 处理 GET 请求（获取页面）
 * - @PostMapping       : 处理 POST 请求（提交表单）
 * - @RequestParam       : 获取请求参数（如表单提交的数据）
 * - HttpSession        : 会话对象，记录用户登录状态
 * - Model              : 模型对象，用于传递数据给页面
 * - @Autowired         : 自动注入依赖
 */
@Controller
public class UserController {

    /**
     * 用户 Repository - 负责数据库操作
     * 注入方式：直接在属性上加 @Autowired 注解
     */
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupService groupService;

    /**
     * 首页/登录页 - 处理 GET 请求
     * 当用户访问 http://localhost:8080/ 时，显示登录页面
     */
    @GetMapping("/")
    public String home() {
        // 返回 "login" - Thymeleaf 会去找 templates/login.html
        return "login";
    }

    /**
     * 登录处理 - 处理 POST 请求
     * 当用户提交登录表单时，这个方法被调用
     *
     * @param username 用户输入的用户名
     * @param password 用户输入的密码
     * @param session  会话对象，用于保存登录状态
     * @param model    模型对象，用于传递数据给页面
     * @return 跳转的页面路径
     */
    @PostMapping("/login")
    public String login(
            @RequestParam("username") String username,  // @RequestParam 获取表单参数
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {

        // -------------------- 数据库验证 --------------------
        // 从数据库查询用户
        // isDeleted = 0 表示查询未删除的用户（软删除支持）
        Optional<User> userOpt = userRepository.findByUsernameAndIsDeleted(username, 0);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // 验证密码（明文比较）
            if (password.equals(user.getPassword())) {
                // 登录成功，把用户信息存到 session
                // session 就像一张卡片，可以记录用户的状态
                session.setAttribute("loginUser", user.getUsername());
                // 保存用户ID，方便后续操作
                session.setAttribute("loginUserId", user.getId());
                // 保存是否为管理员
                session.setAttribute("isAdmin", user.getIsAdmin());

                // 跳转到主页
                return "redirect:/main";
            }
        }

        // 登录失败，返回登录页，并显示错误信息
        model.addAttribute("error", "用户名或密码错误");

        // forward 到 login.html，Thymeleaf 会渲染模板
        return "login";
    }

    /**
     * 主页 - 处理 GET 请求
     * 只有登录后的用户才能访问
     *
     * @param session 会话对象
     * @param model   模型对象
     * @return 主页或登录页
     */
    @GetMapping("/main")
    public String main(HttpServletRequest request, HttpSession session, Model model) {
        // 检查是否已登录
        String username = (String) session.getAttribute("loginUser");

        if (username == null) {
            // 未登录，重定向到登录页
            return "redirect:/";
        }

        // 已登录，把用户名传给页面显示
        model.addAttribute("username", username);

        // 传递是否管理员给页面
        Integer isAdmin = (Integer) session.getAttribute("isAdmin");
        model.addAttribute("isAdmin", isAdmin != null && isAdmin == 1);

        // 传递当前请求路径给模板，用于高亮导航栏
        model.addAttribute("currentUri", request.getRequestURI());

        // 返回 "main" - Thymeleaf 会去找 templates/main.html
        return "main";
    }

    /**
     * 退出登录 - 处理 GET 请求
     * 访问 /logout 会清除登录状态并跳转到登录页
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // 清除 session 中的用户信息
        session.removeAttribute("loginUser");

        // 使 session 失效
        session.invalidate();

        // 重定向到登录页
        return "redirect:/";
    }

    // ==================== 后台管理接口 ====================

    /**
     * 检查是否为管理员
     * 私有方法，用于验证当前用户是否是管理员
     *
     * @param session 会话对象
     * @return 是管理员返回 true，否则返回 false
     */
    private boolean isAdmin(HttpSession session) {
        Integer isAdmin = (Integer) session.getAttribute("isAdmin");
        return isAdmin != null && isAdmin == 1;
    }

    /**
     * 用户管理页面 - 处理 GET 请求
     * 显示所有用户列表（仅管理员可访问）
     *
     * @param session 会话对象
     * @param model   模型对象
     * @return 用户管理页面或重定向
     */
    @GetMapping("/admin/users")
    public String userManage(HttpSession session, Model model) {
        // 检查是否已登录
        if (session.getAttribute("loginUser") == null) {
            return "redirect:/";
        }

        // 检查是否为管理员
        if (!isAdmin(session)) {
            // 不是管理员，跳转到主页
            return "redirect:/main";
        }

        // 获取所有用户（包括已删除的）
        List<User> users = userRepository.findAll();

        // 获取每个用户所属的分组（userId -> 分组名称列表）
        Map<Long, List<String>> userGroupsMap = new HashMap<>();
        for (User user : users) {
            List<Group> groups = groupService.getUserGroups(user.getId());
            List<String> groupNames = groups.stream().map(Group::getName).collect(Collectors.toList());
            userGroupsMap.put(user.getId(), groupNames);
        }

        // 传递数据给页面
        model.addAttribute("username", session.getAttribute("loginUser"));
        model.addAttribute("isAdmin", true);
        model.addAttribute("users", users);
        model.addAttribute("userGroupsMap", userGroupsMap);

        // 返回用户管理页面
        return "userManage";
    }

    /**
     * 添加用户页面 - 处理 GET 请求
     *
     * @param session 会话对象
     * @param model   模型对象
     * @return 用户表单页面或重定向
     */
    @GetMapping("/admin/user/add")
    public String userAdd(HttpSession session, Model model) {
        // 检查是否已登录
        if (session.getAttribute("loginUser") == null) {
            return "redirect:/";
        }

        // 检查是否为管理员
        if (!isAdmin(session)) {
            return "redirect:/main";
        }

        // 传递数据给页面
        model.addAttribute("username", session.getAttribute("loginUser"));
        model.addAttribute("isAdmin", true);
        model.addAttribute("user", new User());  // 空 User 对象用于表单绑定

        return "userForm";
    }

    /**
     * 编辑用户页面 - 处理 GET 请求
     *
     * @param id     用户ID
     * @param session 会话对象
     * @param model   模型对象
     * @return 用户表单页面或重定向
     */
    @GetMapping("/admin/user/edit/{id}")
    public String userEdit(@PathVariable Long id, HttpSession session, Model model) {
        // 检查是否已登录
        if (session.getAttribute("loginUser") == null) {
            return "redirect:/";
        }

        // 检查是否为管理员
        if (!isAdmin(session)) {
            return "redirect:/main";
        }

        // 查询用户
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            // 用户不存在，重定向到用户管理页面
            return "redirect:/admin/users";
        }

        // 传递数据给页面
        model.addAttribute("username", session.getAttribute("loginUser"));
        model.addAttribute("isAdmin", true);
        model.addAttribute("user", userOpt.get());

        return "userForm";
    }

    /**
     * 保存用户 - 处理 POST 请求（添加新用户）
     *
     * @param username       用户名
     * @param password       密码
     * @param confirmPassword 确认密码
     * @param isAdmin        是否管理员
     * @param session        会话对象
     * @param model          模型对象
     * @return 重定向到用户管理页面或返回表单页面
     */
    @PostMapping("/admin/user/save")
    public String userSave(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam(value = "isAdmin", defaultValue = "0") Integer isAdmin,
            HttpSession session,
            Model model) {

        // 检查是否已登录
        if (session.getAttribute("loginUser") == null) {
            return "redirect:/";
        }

        // 检查是否为管理员
        if (!isAdmin(session)) {
            return "redirect:/main";
        }

        // 验证密码
        if (!password.equals(confirmPassword)) {
            model.addAttribute("username", session.getAttribute("loginUser"));
            model.addAttribute("isAdmin", true);
            model.addAttribute("user", null);
            model.addAttribute("error", "两次输入的密码不一致");
            return "userForm";
        }

        // 检查用户名是否已存在
        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            model.addAttribute("username", session.getAttribute("loginUser"));
            model.addAttribute("isAdmin", true);
            model.addAttribute("user", null);
            model.addAttribute("error", "用户名已存在");
            return "userForm";
        }

        // 创建新用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setIsAdmin(isAdmin);
        user.setIsDeleted(0);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // 保存到数据库
        userRepository.save(user);

        // 重定向到用户管理页面
        return "redirect:/admin/users";
    }

    /**
     * 更新用户 - 处理 POST 请求（编辑用户）
     *
     * @param id          用户ID
     * @param password    新密码（可选）
     * @param isAdmin     是否管理员
     * @param isDeleted   是否删除
     * @param createdAt   创建时间
     * @param session     会话对象
     * @return 重定向到用户管理页面
     */
    @PostMapping("/admin/user/update")
    public String userUpdate(
            @RequestParam("id") Long id,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "isAdmin", defaultValue = "0") Integer isAdmin,
            @RequestParam(value = "isDeleted", defaultValue = "0") Integer isDeleted,
            @RequestParam("createdAt") LocalDateTime createdAt,
            HttpSession session) {

        // 检查是否已登录
        if (session.getAttribute("loginUser") == null) {
            return "redirect:/";
        }

        // 检查是否为管理员
        if (!isAdmin(session)) {
            return "redirect:/main";
        }

        // 查询用户
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return "redirect:/admin/users";
        }

        User user = userOpt.get();

        // 更新用户信息
        // 用户名不能修改，所以只更新其他字段
        if (password != null && !password.isEmpty()) {
            user.setPassword(password);
        }
        user.setIsAdmin(isAdmin);
        user.setIsDeleted(isDeleted);
        user.setCreatedAt(createdAt);
        user.setUpdatedAt(LocalDateTime.now());

        // 保存到数据库
        userRepository.save(user);

        // 重定向到用户管理页面
        return "redirect:/admin/users";
    }

    /**
     * 删除/恢复用户 - 处理 GET 请求（软删除）
     *
     * @param id      用户ID
     * @param session 会话对象
     * @return 重定向到用户管理页面
     */
    @GetMapping("/admin/user/delete/{id}")
    public String userDelete(@PathVariable Long id, HttpSession session) {
        // 检查是否已登录
        if (session.getAttribute("loginUser") == null) {
            return "redirect:/";
        }

        // 检查是否为管理员
        if (!isAdmin(session)) {
            return "redirect:/main";
        }

        // 查询用户
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return "redirect:/admin/users";
        }

        User user = userOpt.get();

        // 切换删除状态（软删除）
        user.setIsDeleted(user.getIsDeleted() == 0 ? 1 : 0);
        user.setUpdatedAt(LocalDateTime.now());

        // 保存到数据库
        userRepository.save(user);

        // 重定向到用户管理页面
        return "redirect:/admin/users";
    }

    /**
     * 重置用户密码 - 处理 GET 请求
     * 将密码重置为 123456
     *
     * @param id      用户ID
     * @param session 会话对象
     * @return 重定向到用户管理页面
     */
    @GetMapping("/admin/user/reset/{id}")
    public String userResetPassword(@PathVariable Long id, HttpSession session) {
        // 检查是否已登录
        if (session.getAttribute("loginUser") == null) {
            return "redirect:/";
        }

        // 检查是否为管理员
        if (!isAdmin(session)) {
            return "redirect:/main";
        }

        // 查询用户
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return "redirect:/admin/users";
        }

        User user = userOpt.get();

        // 重置密码为默认密码
        user.setPassword("123456");
        user.setUpdatedAt(LocalDateTime.now());

        // 保存到数据库
        userRepository.save(user);

        // 重定向到用户管理页面
        return "redirect:/admin/users";
    }
}

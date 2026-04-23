package com.example.mywebsite;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
 */
@Controller
public class UserController {

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

        // -------------------- 简单验证 --------------------
        // 实际项目中应该去数据库验证，这里做简单演示
        // 硬编码的用户名: admin，密码: 123456

        if ("admin".equals(username) && "123456".equals(password)) {
            // 登录成功，把用户信息存到 session
            // session 就像一张卡片，可以记录用户的状态
            session.setAttribute("loginUser", username);

            // 跳转到主页
            return "redirect:/main";
        } else {
            // 登录失败，返回登录页，并显示错误信息
            model.addAttribute("error", "用户名或密码错误");

            // forward 到 login.html，Thymeleaf 会渲染模板
            return "login";
        }
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
    public String main(HttpSession session, Model model) {
        // 检查是否已登录
        String username = (String) session.getAttribute("loginUser");

        if (username == null) {
            // 未登录，重定向到登录页
            return "redirect:/";
        }

        // 已登录，把用户名传给页面显示
        model.addAttribute("username", username);

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
}

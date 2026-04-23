package com.example.mywebsite;

/**
 * User.java - 用户实体类
 *
 * 什么是实体类？
 * - 用来描述一个"用户"的数据结构
 * - 包含用户的属性（用户名、密码）
 * - 提供 get/set 方法让其他代码读取或修改这些属性
 *
 * 通俗理解：User 就像一张"用户卡片"，记录用户的各种信息
 */
public class User {

    // 用户名
    private String username;

    // 密码
    private String password;

    // 无参构造函数 - JavaBean 必须有
    public User() {
    }

    // 有参构造函数 - 方便快速创建用户对象
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // -------------------- getter 和 setter --------------------
    // 作用：让其他代码可以读取（get）或修改（set）私有属性

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

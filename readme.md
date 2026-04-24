mywebsite/
├── pom.xml                                    # 项目依赖配置
└── src/main/
    ├── java/com/example/mywebsite/
    │   ├── MywebsiteApplication.java          # 程序入口
    │   ├── User.java                         # 用户数据类
    │   └── UserController.java               # 控制器（处理请求）
    └── resources/
        ├── application.properties             # 配置文件
        └── templates/
            ├── login.html                     # 登录页面
            └── main.html                      # 主页

在wsl中执行以下命令启动系统，默认账号密码admin:123456
mvn -U clean spring-boot:run
地址：http://localhost:8080
使用以下命令可以后台无窗口启动及停止
启动：nohup mvn -U clean spring-boot:run > spring-boot.log 2>&1 & echo $!
停止：pkill -f "spring-boot:run"

pom.xml
作用： 告诉 Maven 需要下载哪些库
<dependency>...spring-boot-starter-web</dependency>  <!-- 网站核心功能 -->
<dependency>...spring-boot-starter-thymeleaf</dependency>  <!-- 模板引擎 -->
就像你装修房子前要列清单，说需要什么材料。


MywebsiteApplication.java
作用： 程序的入口，main() 方法
@SpringBootApplication  // 标记这是Spring Boot程序
public class MywebsiteApplication {
    public static void main(String[] args) {
        SpringApplication.run(...);  // 启动网站
    }
}
就像汽车的点火开关，按下去整个系统就启动了。


User.java
作用： 用户的数据结构
public class User {
    private String username;  // 用户名
    private String password;  // 密码
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    // ... get/set方法
}
就像一张用户卡片，定义用户有哪些属性。


UserController.java
作用： 处理用户请求（登录、主页、退出）
@GetMapping("/")        // 用户访问 http://localhost:8080/ 时调用
public String home() {
    return "login";      // 返回 login.html 页面
}

@PostMapping("/login")  // 用户提交登录表单时调用
public String login(...) {
    // 验证用户名密码
    return "redirect:/main";  // 跳转到主页
}
就像餐厅服务员：
客人点菜（请求）→ 服务员接收（Controller）
厨房做菜（数据库）→ 服务员端菜回来（返回页面）


application.properties
作用： 配置网站参数
server.port=8080           # 网站端口
spring.thymeleaf.prefix=...  # 模板文件在哪
就像房子的水电配置文件。


 login.html
作用： 登录页面
用户输入用户名密码
表单提交到 /login
如果错误显示错误提示


main.html
作用： 主页
显示"欢迎，XXX"
提供退出登录按钮
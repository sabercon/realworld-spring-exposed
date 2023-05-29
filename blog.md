# Kotlin Spring 实战项目：一个简单的博客后端

> 稀土掘金文章地址：<https://juejin.cn/post/7238519458625683517>

自从用 Kotlin 来编写 Spring Boot 项目后，我一直在寻找一个更能发挥 Kotlin 优势的数据层框架。刚好最近学习了 [Exposed](https://github.com/JetBrains/Exposed)，Spring Boot 也发行了最新的 3.1.0 版本，于是就想着写一个基于 [Realworld](https://github.com/gothinkster/realworld) 的后端实战项目来练习一下。

项目地址：<https://github.com/sabercon/realworld-spring-exposed>

# Realworld 项目介绍

[Realworld](https://github.com/gothinkster/realworld) 是一个帮助开发者学习和实验各种技术框架的项目。他定义了如何实现一个类似于 Medium 的简化版博客网站的规范和接口，开发者可以自由选择自己想要的前后端框架去实现。

Realworld 的接口包括了用户注册登录，用户信息维护，文章和评论的 CRUD 等等，完整接口列表可以参考 [这里](https://realworld-docs.netlify.app/docs/specs/backend-specs/endpoints)。

[CodebaseShow](https://codebase.show/projects/realworld) 里可以看到很多不同框架实现的前后端。（本项目收录于 Backend-Kotlin 下）

详情请参考项目仓库，可以通过他们的 [demo](https://demo.realworld.io/) 体验这个博客网站。

# 为什么要用 Kotlin

Kotlin 是由 Jetbrains 开发的基于 JVM 的现代语言，与 Java 完全兼容，同时又解决了很多 Java 遗留的问题。Kotlin 的函数式支持，空指针安全等等现代语言特性都能极大地提高我们的开发效率，最最重要的是，Kotlin 可以使用任何已有的 Java 代码和框架，也能输出代码在 Java 语言中调用。基本上一个 Java 程序员学习一个周末就能完全上手 Kotlin。

Spring 很早就开始宣布对 Kotlin 的 first class 支持，所有文档都同时有 Java 和 Kotlin 的实例代码。Spring 的各种框架也都针对 Kotlin 提供了一些扩展的方法和函数。使用 Kotlin 可以很轻松地编写 Spring Boot 项目。

强烈建议每位 Java 程序员都去了解一下 Kotlin，可以学习一下 [官网](https://kotlinlang.org/docs/getting-started.html) 的入门教程，一定会对你有所启发。

# 为什么要用 Exposed

在选择用 Kotlin+Spring 构建项目后，我尝试过很多数据层框架，像 MyBatis，Spring Data JPA， Spring Data JDBC，Spring Data R2DBC 等等。因为 Kotlin 与 Java 无缝兼容，所以这些框架使用起来没有任何问题。但因为这些框架都是面向 Java 的，面对稍微复杂一点的数据查询就总是需要手写 SQL 语句，没有很好地发挥 Kotlin 强大的 DSL 能力，所以我希望寻找一个基于 Kotlin 的原生数据层框架来使我避免大量的 SQL 编写。

Exposed 是 Jetbrains 官方开发的基于 JDBC 的数据层框架，基于 Kotlin 提供了 DSL 和 DAO 的支持，不用写 SQL 就能实现各种复杂的联表查询。缺点就是最近维护得不是很积极，如果生产环境使用可能会遇到各种坑 😂。

详情可以参考他们的 [wiki](https://github.com/JetBrains/Exposed/wiki)。

# Spring Boot 3.1 新功能

本项目用到了两个 Spring Boot 3.1 新增的模块：

## spring-boot-docker-compose

这个库可以根据项目中的 `docker-compose.yml` 文件管理容器，在应用启动时启动容器并在应用关闭时停止容器，方便了我们本地的开发和调试。

## spring-boot-testcontainers

Spring 终终终于提供了对 Testcontainers 的支持，现在只要将 Testcontainers 注册成 beans 并加上 `@ServiceConnection` 注解就可以轻松将容器的地址端口等信息注入 context 的配置参数里。这是我项目里编写的容器配置类：

```kotlin
class ContainerConfiguration {
    @Bean
    @ServiceConnection
    fun postgresContainer() = PostgreSQLContainer("postgres")
}
```

写测试时在 `@SpringBootTest` 中加入这个类就可以了：

```kotlin
@SpringBootTest(classes = [RealworldApplication::class, ContainerConfiguration::class])
```

其他使用方法可以参考 [官方文档](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing.testcontainers)。

# 项目设计

下面是设计的部分说明和介绍，像 [Detekt](https://detekt.dev/) 和 [Kover](https://github.com/Kotlin/kotlinx-kover) 这些就没有仔细介绍了，具体请参考项目代码和官方文档。

## 目录结构

我个人不是很喜欢将 web，service 和 dao 分成三个目录的传统做法。本项目主要采用 domain 来划分项目：

*   用户相关的类放在 `user` 包下
*   文章相关的类放在 `article` 包下
*   评论相关的类放在 `comment` 包下

## 路由

本项目没有使用 Spring MVC 传统的 controller 来实现 web 层，而是采用了函数式路由，配合上 Spring MVC 提供的 Kotlin DSL 类。感觉两者差别没有很大，函数式路由会更加灵活和清晰，而且比较帅 😁。

下面是一个函数式路由的例子：

```kotlin
@Configuration(proxyBeanMethods = false)
class UserRouterConfiguration {

    @Bean
    fun userRouter(service: UserService) = router {
        POST("/users/login") {
            val request = it.body<LoginRequest>()
            val response = service.login(request)
            ok().body(response)
        }
    }
}
```

## 鉴权

本项目的鉴权比较简单，只是给 `ServerRequest` 添加了一个扩展方法去获取用户的 ID，每次获取时就会解析请求头里的 JWT，失败时会抛出 401 异常。

## 测试

我用 Kotest 和 Testcontainers 写了几个登录注册的集成测试。然后，就没了。。。抱歉，我太懒了 🥲。

# 结语

希望本项目对大家了解 Kotlin 和 Spring 有帮助，有任何问题都可以直接联系我。

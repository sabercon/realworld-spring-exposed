package cn.sabercon.realworld.user

import cn.sabercon.realworld.web.userId
import cn.sabercon.realworld.web.userIdOrNull
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.function.body
import org.springframework.web.servlet.function.router

@Configuration(proxyBeanMethods = false)
class UserRouterConfiguration {

    @Bean
    fun userRouter(service: UserService) = router {
        POST("/users/login") {
            val payload = it.body<LoginRequest>().user
            val model = service.login(payload)
            ok().body(UserResponse(model))
        }

        POST("/users") {
            val payload = it.body<RegisterRequest>().user
            val model = service.register(payload)
            ok().body(UserResponse(model))
        }

        GET("/user") {
            val userId = it.userId()
            val model = service.getCurrentUser(userId)
            ok().body(UserResponse(model))
        }

        PUT("/user") {
            val userId = it.userId()
            val payload = it.body<UserUpdateRequest>().user
            val model = service.updateUser(userId, payload)
            ok().body(UserResponse(model))
        }

        GET("/profiles/{username}") {
            val userId = it.userIdOrNull()
            val username = it.pathVariable("username")
            val model = service.getProfile(userId, username)
            ok().body(ProfileResponse(model))
        }

        POST("/profiles/{username}/follow") {
            val userId = it.userId()
            val username = it.pathVariable("username")
            val model = service.followUser(userId, username)
            ok().body(ProfileResponse(model))
        }

        DELETE("/profiles/{username}/follow") {
            val userId = it.userId()
            val username = it.pathVariable("username")
            val model = service.unfollowUser(userId, username)
            ok().body(ProfileResponse(model))
        }
    }
}

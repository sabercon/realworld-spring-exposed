package cn.sabercon.realworld.comment

import cn.sabercon.realworld.web.userId
import cn.sabercon.realworld.web.userIdOrNull
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.function.body
import org.springframework.web.servlet.function.router

@Configuration(proxyBeanMethods = false)
class CommentRouterConfiguration {

    @Bean
    fun commentRouter(service: CommentService) = router {
        POST("/articles/{slug}/comments") {
            val userId = it.userId()
            val slug = it.pathVariable("slug")
            val payload = it.body<CommentCreateRequest>().comment
            val model = service.createComment(userId, slug, payload)
            ok().body(CommentResponse(model))
        }

        GET("/articles/{slug}/comments") {
            val userId = it.userIdOrNull()
            val slug = it.pathVariable("slug")
            val comments = service.listComment(userId, slug)
            ok().body(CommentsResponse(comments))
        }

        DELETE("/articles/{slug}/comments/{id}") {
            val userId = it.userId()
            val id = it.pathVariable("id")
            service.deleteComment(userId, id)
            ok().build()
        }
    }
}

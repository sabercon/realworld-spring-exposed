package cn.sabercon.realworld.article

import cn.sabercon.realworld.web.pageParams
import cn.sabercon.realworld.web.userId
import cn.sabercon.realworld.web.userIdOrNull
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.function.body
import org.springframework.web.servlet.function.paramOrNull
import org.springframework.web.servlet.function.router

@Configuration(proxyBeanMethods = false)
class ArticleRouterConfiguration {

    @Bean
    fun articleRouter(service: ArticleService) = router {
        GET("/articles") {
            val userId = it.userIdOrNull()
            val tag = it.paramOrNull("tag")
            val author = it.paramOrNull("author")
            val favorited = it.paramOrNull("favorited")
            val pageParams = it.pageParams()
            val articles = service.listArticle(userId, tag, author, favorited, pageParams)
            val count = service.countArticle(userId, tag, author, favorited)
            ok().body(ArticlesResponse(articles, count))
        }

        GET("/articles/feed") {
            val userId = it.userId()
            val articles = service.listFeedArticle(userId)
            val count = service.countFeedArticle(userId)
            ok().body(ArticlesResponse(articles, count))
        }

        GET("/articles/{slug}") {
            val userId = it.userIdOrNull()
            val slug = it.pathVariable("slug")
            val model = service.getArticle(userId, slug)
            ok().body(ArticleResponse(model))
        }

        POST("/articles") {
            val userId = it.userId()
            val payload = it.body<ArticleCreateRequest>().article
            val model = service.createArticle(userId, payload)
            ok().body(ArticleResponse(model))
        }

        PUT("/articles/{slug}") {
            val userId = it.userId()
            val slug = it.pathVariable("slug")
            val payload = it.body<ArticleUpdateRequest>().article
            val model = service.updateArticle(userId, slug, payload)
            ok().body(ArticleResponse(model))
        }

        DELETE("/articles/{slug}") {
            val userId = it.userId()
            val slug = it.pathVariable("slug")
            service.deleteArticle(userId, slug)
            ok().build()
        }

        POST("/articles/{slug}/favorite") {
            val userId = it.userId()
            val slug = it.pathVariable("slug")
            val model = service.favoriteArticle(userId, slug)
            ok().body(ArticleResponse(model))
        }

        DELETE("/articles/{slug}/favorite") {
            val userId = it.userId()
            val slug = it.pathVariable("slug")
            val model = service.unfavoriteArticle(userId, slug)
            ok().body(ArticleResponse(model))
        }

        GET("/tags") {
            val tags = service.listTag()
            ok().body(TagsResponse(tags))
        }
    }
}

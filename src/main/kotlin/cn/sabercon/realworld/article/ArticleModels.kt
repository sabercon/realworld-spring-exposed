package cn.sabercon.realworld.article

import cn.sabercon.realworld.user.ProfileModel
import java.time.Instant

data class ArticleCreateRequest(val article: Article) {
    data class Article(
        val title: String,
        val description: String,
        val body: String,
        val tagList: List<String> = emptyList(),
    )
}

data class ArticleUpdateRequest(val article: Article) {
    data class Article(
        val title: String? = null,
        val description: String? = null,
        val body: String? = null,
    )
}

data class ArticleModel(
    val slug: String,
    val title: String,
    val description: String,
    val body: String,
    val tagList: List<String> = emptyList(),
    val createdAt: Instant,
    val updatedAt: Instant,
    val favorited: Boolean,
    val favoritesCount: Long,
    val author: ProfileModel,
) {
    companion object {
        fun from(article: Article, favorited: Boolean, favoritesCount: Long, authorFollowed: Boolean): ArticleModel {
            return ArticleModel(
                slug = article.slug,
                title = article.title,
                description = article.description,
                body = article.body,
                tagList = article.tags.map { it.name }.sorted(),
                createdAt = article.createdAt,
                updatedAt = article.updatedAt,
                favorited = favorited,
                favoritesCount = favoritesCount,
                author = ProfileModel.fromUser(article.author, authorFollowed),
            )
        }
    }
}

data class ArticleResponse(val article: ArticleModel)

data class ArticlesResponse(val articles: List<ArticleModel>, val articlesCount: Long)

data class TagsResponse(val tags: List<String>)

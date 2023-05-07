package cn.sabercon.realworld.article

import cn.sabercon.realworld.web.PageParams
import org.springframework.stereotype.Service

@Service
class ArticleService {
    fun listArticle(
        userId: String?,
        tag: String?,
        author: String?,
        favorited: String?,
        pageParams: PageParams,
    ): List<ArticleModel> {
        TODO()
    }

    fun countArticle(userId: String?, tag: String?, author: String?, favorited: String?): Long {
        TODO()
    }

    fun listFeedArticle(userId: String): List<ArticleModel> {
        TODO()
    }

    fun countFeedArticle(userId: String): Long {
        TODO()
    }

    fun getArticle(userId: String?, slug: String): ArticleModel {
        TODO()
    }

    fun createArticle(userId: String, payload: ArticleCreateRequest.Article): ArticleModel {
        TODO()
    }

    fun updateArticle(userId: String, slug: String, payload: ArticleUpdateRequest.Article): ArticleModel {
        TODO()
    }

    fun deleteArticle(userId: String, slug: String) {
        TODO()
    }

    fun favoriteArticle(userId: String, slug: String): ArticleModel {
        TODO()
    }

    fun unfavoriteArticle(userId: String, slug: String): ArticleModel {
        TODO()
    }

    fun listTag(): List<String> {
        TODO()
    }
}

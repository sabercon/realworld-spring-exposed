package cn.sabercon.realworld.article

import cn.sabercon.realworld.user.UserRepository
import cn.sabercon.realworld.util.forbidden
import cn.sabercon.realworld.util.isUniqueConstraintException
import cn.sabercon.realworld.util.slugify
import cn.sabercon.realworld.util.tx
import cn.sabercon.realworld.web.PageParams
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.springframework.stereotype.Service

@Service
class ArticleService(private val repository: ArticleRepository, private val userRepository: UserRepository) {

    fun listArticle(
        userId: String?,
        tag: String?,
        author: String?,
        favorited: String?,
        pageParams: PageParams,
    ): List<ArticleModel> = tx {
        repository.list(tag, author, favorited, pageParams).map { toModel(userId, it) }
    }

    fun countArticle(tag: String?, author: String?, favorited: String?): Long = tx {
        repository.count(tag, author, favorited)
    }

    fun listFeedArticle(userId: String, pageParams: PageParams): List<ArticleModel> = tx {
        repository.listFeed(userId, pageParams).map { toModel(userId, it) }
    }

    fun countFeedArticle(userId: String): Long = tx {
        repository.countFeed(userId)
    }

    fun getArticle(userId: String?, slug: String): ArticleModel = tx {
        toModel(userId, repository.getBySlug(slug))
    }

    fun createArticle(userId: String, payload: ArticleCreateRequest.Article): ArticleModel {
        return doCreateArticle(userId, payload, false)
    }

    private fun doCreateArticle(
        userId: String,
        payload: ArticleCreateRequest.Article,
        slugSuffix: Boolean,
    ): ArticleModel = try {
        tx {
            val slug = payload.title.slugify(slugSuffix)
            val author = userRepository.getById(userId)
            val tags = repository.upsertTags(payload.tagList)
            val article = repository.create(slug, author, tags, payload)
            toModel(userId, article)
        }
    } catch (e: ExposedSQLException) {
        when {
            e.isUniqueConstraintException("article_slug_key") -> doCreateArticle(userId, payload, true)
            else -> throw e
        }
    }

    fun updateArticle(userId: String, slug: String, payload: ArticleUpdateRequest.Article): ArticleModel {
        return doUpdateArticle(userId, slug, payload, false)
    }

    private fun doUpdateArticle(
        userId: String,
        slug: String,
        payload: ArticleUpdateRequest.Article,
        slugSuffix: Boolean,
    ): ArticleModel = try {
        tx {
            val article = repository.getBySlug(slug)
            if (article.author.id.value != userId) forbidden("Not author")

            val newSlug = payload.title?.slugify(slugSuffix)
            repository.update(article, newSlug, payload)
            toModel(userId, article)
        }
    } catch (e: ExposedSQLException) {
        when {
            e.isUniqueConstraintException("article_slug_key") -> doUpdateArticle(userId, slug, payload, true)
            else -> throw e
        }
    }

    fun deleteArticle(userId: String, slug: String): Unit = tx {
        val (articleId, authorId) = repository.getArticleIdAndAuthorId(slug)
        if (authorId != userId) forbidden("Not author")

        repository.delete(articleId)
    }

    fun favoriteArticle(userId: String, slug: String): ArticleModel = tx {
        val article = repository.getBySlug(slug)
        repository.favoriteArticle(userId, article)
        toModel(userId, article, true)
    }

    fun unfavoriteArticle(userId: String, slug: String): ArticleModel = tx {
        val article = repository.getBySlug(slug)
        repository.unfavoriteArticle(userId, article)
        toModel(userId, article, false)
    }

    fun listTag(): List<String> = tx {
        repository.listTag().map { it.name }
    }

    private fun toModel(
        userId: String?,
        article: Article,
        favorited: Boolean = repository.isFavorited(userId, article),
    ): ArticleModel {
        return ArticleModel.from(
            article = article,
            favorited = favorited,
            favoritesCount = repository.countFavorite(article),
            authorFollowed = userRepository.isFollowed(userId, article.author),
        )
    }
}

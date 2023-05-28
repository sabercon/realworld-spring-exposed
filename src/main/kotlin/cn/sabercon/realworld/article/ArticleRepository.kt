package cn.sabercon.realworld.article

import cn.sabercon.realworld.user.User
import cn.sabercon.realworld.user.UserFollows
import cn.sabercon.realworld.user.Users
import cn.sabercon.realworld.util.exists
import cn.sabercon.realworld.util.tx
import cn.sabercon.realworld.web.PageParams
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.springframework.stereotype.Repository

@Repository
class ArticleRepository {

    fun getBySlug(slug: String): Article {
        return Article.find { Articles.slug eq slug }.single()
    }

    fun isFavorited(userId: String?, article: Article): Boolean {
        return userId != null && ArticleFavorites.exists { this.userId eq userId and (articleId eq article.id) }
    }

    fun countFavorite(article: Article): Long {
        return ArticleFavorites.select { ArticleFavorites.articleId eq article.id }.count()
    }

    fun list(tag: String?, author: String?, favorited: String?, pageParams: PageParams): List<Article> {
        return queryPage(buildQuery(tag, author, favorited), pageParams)
    }

    fun count(tag: String?, author: String?, favorited: String?): Long {
        return buildQuery(tag, author, favorited).count()
    }

    fun listFeed(userId: String, pageParams: PageParams): List<Article> {
        return queryPage(buildFeedQuery(userId), pageParams)
    }

    fun countFeed(userId: String): Long = tx {
        buildFeedQuery(userId).count()
    }

    private fun buildQuery(tag: String?, author: String?, favorited: String?): Query {
        var columnSet: ColumnSet = Articles
        if (tag != null) {
            columnSet = columnSet.innerJoin(ArticleTags, { Articles.id }, { articleId })
                .innerJoin(Tags, { ArticleTags.tagId }, { id }) { Tags.name eq tag }
        }
        if (author != null) {
            val authors = Users.alias("authors")
            val authorId = authors[Users.id]
            val authorUsername = authors[Users.username]
            columnSet = columnSet.innerJoin(authors, { Articles.authorId }, { authorId }) { authorUsername eq author }
        }
        if (favorited != null) {
            columnSet = columnSet.innerJoin(ArticleFavorites, { Articles.id }, { articleId })
                .innerJoin(Users, { ArticleFavorites.userId }, { id }) { Users.username eq favorited }
        }
        return columnSet.slice(Articles.columns).selectAll()
    }

    private fun buildFeedQuery(userId: String): Query {
        return Articles.innerJoin(UserFollows, { authorId }, { followeeId })
            .slice(Articles.columns)
            .select { UserFollows.followerId eq userId }
    }

    private fun queryPage(query: Query, pageParams: PageParams): List<Article> {
        return query.orderBy(Articles.createdAt to SortOrder.DESC)
            .limit(pageParams.limit, pageParams.offset)
            .let { Article.wrapRows(it).toList() }
            .with(Article::author, Article::tags)
    }

    fun create(
        slug: String,
        author: User,
        tags: SizedIterable<Tag>,
        payload: ArticleCreateRequest.Article,
    ): Article {
        return Article.new {
            title = payload.title
            description = payload.description
            body = payload.body
            this.slug = slug
            this.author = author
            this.tags = tags
        }
    }

    /**
     * [slug] should not be null when the title in [payload] is not null.
     */
    fun update(article: Article, slug: String?, payload: ArticleUpdateRequest.Article) {
        if (payload.title != null && payload.title != article.title) {
            article.title = payload.title
            article.slug = slug!!
        }
        payload.description?.let { article.description = it }
        payload.body?.let { article.body = it }
    }

    fun getArticleIdAndAuthorId(slug: String): Pair<Long, String> {
        return Articles.slice(Articles.id, Articles.authorId)
            .select { Articles.slug eq slug }
            .single().let { it[Articles.id].value to it[Articles.authorId].value }
    }

    fun delete(articleId: Long) {
        Articles.deleteWhere { id eq articleId }
        ArticleTags.deleteWhere { this.articleId eq articleId }
        ArticleFavorites.deleteWhere { this.articleId eq articleId }
    }

    fun favoriteArticle(userId: String, article: Article) {
        ArticleFavorites.insertIgnore {
            it[this.userId] = userId
            it[articleId] = article.id
        }
    }

    fun unfavoriteArticle(userId: String, article: Article) {
        ArticleFavorites.deleteWhere { this.userId eq userId and (articleId eq article.id) }
    }

    fun listTag(): SizedIterable<Tag> {
        return Tag.all()
    }

    fun upsertTags(tags: List<String>): SizedIterable<Tag> {
        Tags.batchInsert(tags, ignore = true) { tag -> this[Tags.name] = tag }
        return Tag.find { Tags.name inList tags }
    }
}

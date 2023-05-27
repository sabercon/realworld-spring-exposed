package cn.sabercon.realworld.article

import cn.sabercon.realworld.user.User
import cn.sabercon.realworld.user.Users
import cn.sabercon.realworld.util.*
import org.jetbrains.exposed.dao.id.EntityID

object Articles : BaseLongIdTable("article") {
    val slug = string("slug").uniqueIndex()
    val title = string("title")
    val description = string("description")
    val body = string("body")
    val authorId = reference("author_id", Users)
}

object Tags : BaseLongIdTable("tag") {
    val name = string("name").uniqueIndex()
}

object ArticleTags : BaseTable("article_tag") {
    val articleId = reference("article_id", Articles)
    val tagId = reference("tag_id", Tags)
    override val primaryKey = PrimaryKey(articleId, tagId)
}

object ArticleFavorites : BaseTable("article_favorite") {
    val userId = reference("user_id", Users)
    val articleId = reference("article_id", Articles)
    override val primaryKey = PrimaryKey(userId, articleId)
}

class Article(id: EntityID<Long>) : BaseLongEntity(id, Articles) {

    companion object : BaseLongEntityClass<Article>(Articles)

    var slug by Articles.slug
    var title by Articles.title
    var description by Articles.description
    var body by Articles.body
    var author by User referencedOn Articles.authorId
    var tags by Tag via ArticleTags
}

class Tag(id: EntityID<Long>) : BaseLongEntity(id, Tags) {
    companion object : BaseLongEntityClass<Tag>(Tags)

    var name by Tags.name
}

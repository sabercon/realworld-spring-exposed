package cn.sabercon.realworld.comment

import cn.sabercon.realworld.article.Article
import cn.sabercon.realworld.article.Articles
import cn.sabercon.realworld.user.User
import cn.sabercon.realworld.user.Users
import cn.sabercon.realworld.util.BaseLongEntity
import cn.sabercon.realworld.util.BaseLongEntityClass
import cn.sabercon.realworld.util.BaseLongIdTable
import cn.sabercon.realworld.util.string
import org.jetbrains.exposed.dao.id.EntityID

object Comments : BaseLongIdTable("comment") {
    val body = string("body")
    val authorId = reference("author_id", Users)
    val articleId = reference("article_id", Articles)
}

class Comment(id: EntityID<Long>) : BaseLongEntity(id, Comments) {
    companion object : BaseLongEntityClass<Comment>(Comments)

    var body by Comments.body
    var author by User referencedOn Comments.authorId
    var article by Article referencedOn Comments.articleId
}

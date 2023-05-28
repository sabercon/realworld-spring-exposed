package cn.sabercon.realworld.comment

import cn.sabercon.realworld.article.Article
import cn.sabercon.realworld.user.User
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.springframework.stereotype.Repository

@Repository
class CommentRepository {

    fun create(author: User, article: Article, payload: CommentCreateRequest.Comment): Comment {
        return Comment.new {
            body = payload.body
            this.author = author
            this.article = article
        }
    }

    fun list(article: Article): SizedIterable<Comment> {
        return Comment.find { Comments.articleId eq article.id }
            .with(Comment::author)
            .orderBy(Comments.createdAt to SortOrder.ASC)
    }

    fun getAuthorId(commentId: Long): String {
        return Comments.slice(Comments.authorId)
            .select { Comments.id eq commentId }
            .single()[Comments.authorId].value
    }

    fun delete(commentId: Long) {
        Comments.deleteWhere { id eq commentId }
    }
}

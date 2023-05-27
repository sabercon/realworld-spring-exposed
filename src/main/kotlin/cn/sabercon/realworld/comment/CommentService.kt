package cn.sabercon.realworld.comment

import cn.sabercon.realworld.article.ArticleService
import cn.sabercon.realworld.user.UserService
import cn.sabercon.realworld.util.forbidden
import cn.sabercon.realworld.util.tx
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.springframework.stereotype.Service

@Service
class CommentService(private val userService: UserService, private val articleService: ArticleService) {

    fun createComment(userId: String, articleSlug: String, payload: CommentCreateRequest.Comment): CommentModel = tx {
        val comment = Comment.new {
            body = payload.body
            author = userService.getById(userId)
            article = articleService.getBySlug(articleSlug)
        }
        toModel(userId, comment)
    }

    fun listComment(userId: String?, articleSlug: String): List<CommentModel> = tx {
        val article = articleService.getBySlug(articleSlug)
        Comment.find { Comments.articleId eq article.id }
            .with(Comment::author)
            .sortedBy { it.createdAt }
            .map { toModel(userId, it) }
    }

    fun deleteComment(userId: String, commentId: Long): Unit = tx {
        val authorId = Comments.slice(Comments.authorId)
            .select { Comments.id eq commentId }
            .single()[Comments.authorId]
        if (authorId.value != userId) forbidden("Not author")

        Comments.deleteWhere { id eq commentId }
    }

    private fun toModel(userId: String?, comment: Comment): CommentModel {
        return CommentModel.from(comment, userService.isFollowed(userId, comment.author))
    }
}

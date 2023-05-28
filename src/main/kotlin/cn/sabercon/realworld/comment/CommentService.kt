package cn.sabercon.realworld.comment

import cn.sabercon.realworld.article.ArticleRepository
import cn.sabercon.realworld.user.UserRepository
import cn.sabercon.realworld.util.forbidden
import cn.sabercon.realworld.util.tx
import org.springframework.stereotype.Service

@Service
class CommentService(
    private val repository: CommentRepository,
    private val userRepository: UserRepository,
    private val articleRepository: ArticleRepository,
) {

    fun createComment(userId: String, articleSlug: String, payload: CommentCreateRequest.Comment): CommentModel = tx {
        val author = userRepository.getById(userId)
        val article = articleRepository.getBySlug(articleSlug)
        val comment = repository.create(author, article, payload)
        toModel(userId, comment)
    }

    fun listComment(userId: String?, articleSlug: String): List<CommentModel> = tx {
        val article = articleRepository.getBySlug(articleSlug)
        repository.list(article).map { toModel(userId, it) }
    }

    fun deleteComment(userId: String, id: Long): Unit = tx {
        val authorId = repository.getAuthorId(id)
        if (authorId != userId) forbidden("Not author")

        repository.delete(id)
    }

    private fun toModel(userId: String?, comment: Comment): CommentModel {
        return CommentModel.from(comment, userRepository.isFollowed(userId, comment.author))
    }
}

package cn.sabercon.realworld.comment

import org.springframework.stereotype.Service

@Service
class CommentService {
    fun createComment(userId: String, articleSlug: String, payload: CommentCreateRequest.Comment): CommentModel {
        TODO()
    }

    fun listComment(userId: String?, slug: String): List<CommentModel> {
        TODO()
    }

    fun deleteComment(userId: String, id: String) {
        TODO()
    }
}

package cn.sabercon.realworld.comment

import cn.sabercon.realworld.user.ProfileModel
import java.time.Instant

data class CommentCreateRequest(val comment: Comment) {
    data class Comment(val body: String)
}

data class CommentModel(
    val id: Long,
    val createdAt: Instant,
    val updatedAt: Instant,
    val body: String,
    val author: ProfileModel,
) {
    companion object {
        fun from(comment: Comment, authorFollowed: Boolean): CommentModel {
            return CommentModel(
                id = comment.id.value,
                createdAt = comment.createdAt,
                updatedAt = comment.updatedAt,
                body = comment.body,
                author = ProfileModel.fromUser(comment.author, authorFollowed),
            )
        }
    }
}

data class CommentResponse(val comment: CommentModel)

data class CommentsResponse(val comments: List<CommentModel>)

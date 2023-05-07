package cn.sabercon.realworld.comment

import cn.sabercon.realworld.user.ProfileModel
import java.time.Instant

data class CommentCreateRequest(val comment: Comment) {
    data class Comment(val body: String)
}

data class CommentModel(
    val id: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val body: String,
    val author: ProfileModel,
)

data class CommentResponse(val comment: CommentModel)

data class CommentsResponse(val comments: List<CommentModel>)

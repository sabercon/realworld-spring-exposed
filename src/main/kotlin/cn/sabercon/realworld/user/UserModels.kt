package cn.sabercon.realworld.user

data class LoginRequest(val user: User) {
    data class User(val email: String, val password: String)
}

data class RegisterRequest(val user: User) {
    data class User(
        val username: String,
        val email: String,
        val password: String,
    )
}

data class UserUpdateRequest(val user: User) {
    data class User(
        val email: String? = null,
        val username: String? = null,
        val password: String? = null,
        val bio: String? = null,
        val image: String? = null,
    )
}

data class UserModel(
    val email: String,
    val token: String,
    val username: String,
    val bio: String? = null,
    val image: String? = null,
) {
    companion object {
        fun fromUser(user: User, token: String) = UserModel(
            email = user.email,
            token = token,
            username = user.username,
            bio = user.bio,
            image = user.image,
        )
    }
}

data class ProfileModel(
    val username: String,
    val bio: String? = null,
    val image: String? = null,
    val following: Boolean,
) {
    companion object {
        fun fromUser(user: User, followed: Boolean) = ProfileModel(
            username = user.username,
            bio = user.bio,
            image = user.image,
            following = followed,
        )
    }
}

data class UserResponse(val user: UserModel)

data class ProfileResponse(val profile: ProfileModel)

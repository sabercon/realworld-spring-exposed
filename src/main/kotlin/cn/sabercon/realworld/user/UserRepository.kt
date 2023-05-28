package cn.sabercon.realworld.user

import cn.sabercon.realworld.util.exists
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnore
import org.springframework.stereotype.Repository

@Repository
class UserRepository {

    fun getById(id: String): User {
        return User[id]
    }

    fun getByUsername(username: String): User {
        return User.find { Users.username eq username }.single()
    }

    fun findByEmail(email: String): User? {
        return User.find { Users.email eq email }.firstOrNull()
    }

    fun isFollowed(followerId: String?, user: User): Boolean {
        return followerId != null && UserFollows.exists { this.followerId eq followerId and (followeeId eq user.id) }
    }

    fun create(payload: RegisterRequest.User): User {
        return User.new {
            email = payload.email
            username = payload.username
            password = payload.password
        }
    }

    fun update(user: User, payload: UserUpdateRequest.User) {
        payload.email?.let { user.email = it }
        payload.username?.let { user.username = it }
        payload.password?.let { user.password = it }
        payload.bio?.let { user.bio = it }
        payload.image?.let { user.image = it }
    }

    fun followUser(userId: String, followee: User) {
        UserFollows.insertIgnore {
            it[followerId] = userId
            it[followeeId] = followee.id
        }
    }

    fun unfollowUser(userId: String, followee: User) {
        UserFollows.deleteWhere { followerId eq userId and (followeeId eq followee.id) }
    }
}

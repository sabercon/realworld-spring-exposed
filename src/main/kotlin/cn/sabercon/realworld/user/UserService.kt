package cn.sabercon.realworld.user

import cn.sabercon.realworld.util.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnore
import org.springframework.stereotype.Service

@Service
class UserService(private val jwt: Jwt) {

    fun login(payload: LoginRequest.User): UserModel = tx {
        val user = User.find { Users.email eq payload.email }.firstOrNull()
            ?.takeIf { PasswordEncoder.matches(payload.password, it.password) }
            ?: unprocessable("Email or password is invalid")
        toModel(user)
    }

    fun register(payload: RegisterRequest.User): UserModel = try {
        tx {
            val user = User.new {
                email = payload.email
                username = payload.username
                password = PasswordEncoder.encode(payload.password)
            }
            toModel(user)
        }
    } catch (e: ExposedSQLException) {
        when {
            e.isUniqueConstraintException("user_email_key") -> unprocessable("Email already exists")
            e.isUniqueConstraintException("user_username_key") -> unprocessable("Username already exists")
            else -> throw e
        }
    }

    fun getCurrentUser(userId: String): UserModel = tx {
        toModel(getById(userId))
    }

    fun updateUser(userId: String, payload: UserUpdateRequest.User): UserModel = try {
        tx {
            val user = getById(userId)
            payload.email?.let { user.email = it }
            payload.username?.let { user.username = it }
            payload.password?.let { user.password = PasswordEncoder.encode(it) }
            payload.bio?.let { user.bio = it }
            payload.image?.let { user.image = it }
            toModel(user)
        }
    } catch (e: ExposedSQLException) {
        when {
            e.isUniqueConstraintException("user_email_key") -> unprocessable("Email already exists")
            e.isUniqueConstraintException("user_username_key") -> unprocessable("Username already exists")
            else -> throw e
        }
    }

    fun getProfile(followerId: String?, username: String): ProfileModel = tx {
        val user = getByUsername(username)
        ProfileModel.fromUser(user, isFollowed(followerId, user))
    }

    fun followUser(userId: String, followeeUsername: String): ProfileModel = tx {
        val followee = getByUsername(followeeUsername)
        UserFollows.insertIgnore {
            it[followerId] = EntityID(userId, Users)
            it[followeeId] = followee.id
        }
        ProfileModel.fromUser(followee, true)
    }

    fun unfollowUser(userId: String, followeeUsername: String): ProfileModel = tx {
        val followee = getByUsername(followeeUsername)
        UserFollows.deleteWhere {
            followerId eq EntityID(userId, Users) and (followeeId eq followee.id)
        }
        ProfileModel.fromUser(followee, false)
    }

    fun getById(id: String): User {
        return User[id]
    }

    fun getByUsername(username: String): User {
        return User.find { Users.username eq username }.single()
    }

    fun isFollowed(followerId: String?, user: User): Boolean {
        return followerId != null && UserFollows
            .exists { UserFollows.followerId eq EntityID(followerId, Users) and (followeeId eq user.id) }
    }

    private fun toModel(user: User): UserModel = UserModel.fromUser(user, jwt.generateToken(user.id.value))
}

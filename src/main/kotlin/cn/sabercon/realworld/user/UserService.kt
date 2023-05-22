package cn.sabercon.realworld.user

import cn.sabercon.realworld.util.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.springframework.stereotype.Service

@Service
class UserService(private val jwt: Jwt) {

    fun login(payload: LoginRequest.User): UserModel {
        val user = tx {
            User.find { Users.email eq payload.email }.firstOrNull()
                ?.takeIf { PasswordEncoder.matches(payload.password, it.password) }
                ?: unprocessable("Email or password is invalid")
        }
        return toModel(user)
    }

    fun register(payload: RegisterRequest.User): UserModel {
        val user = try {
            tx {
                User.new {
                    email = payload.email
                    username = payload.username
                    password = PasswordEncoder.encode(payload.password)
                }
            }
        } catch (e: ExposedSQLException) {
            when {
                e.isUniqueConstraintException("user_email_key") -> unprocessable("Email already exists")
                e.isUniqueConstraintException("user_username_key") -> unprocessable("Username already exists")
                else -> throw e
            }
        }
        return toModel(user)
    }

    fun getCurrentUser(userId: String): UserModel {
        val user = findById(userId)
        return toModel(user)
    }

    fun updateUser(userId: String, payload: UserUpdateRequest.User): UserModel {
        val user = try {
            tx {
                val user = findById(userId)
                payload.email?.let { user.email = it }
                payload.username?.let { user.username = it }
                payload.password?.let { user.password = PasswordEncoder.encode(it) }
                payload.bio?.let { user.bio = it }
                payload.image?.let { user.image = it }
                user
            }
        } catch (e: ExposedSQLException) {
            when {
                e.isUniqueConstraintException("user_email_key") -> unprocessable("Email already exists")
                e.isUniqueConstraintException("user_username_key") -> unprocessable("Username already exists")
                else -> throw e
            }
        }
        return toModel(user)
    }

    fun getProfile(userId: String?, username: String): ProfileModel {
        TODO()
    }

    fun followUser(userId: String, username: String): ProfileModel {
        TODO()
    }

    fun unfollowUser(userId: String, username: String): ProfileModel {
        TODO()
    }

    private fun findById(id: String) = tx {
        User.findById(id) ?: notFound("User not found")
    }

    private fun findByUsername(username: String) = tx {
        User.find { Users.username eq username }.firstOrNull() ?: notFound("User not found")
    }

    private fun toModel(user: User) = UserModel.fromUser(user, jwt.generateToken(user.id.value))
}

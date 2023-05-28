package cn.sabercon.realworld.user

import cn.sabercon.realworld.util.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.springframework.stereotype.Service

@Service
class UserService(private val jwt: Jwt, private val repository: UserRepository) {

    fun login(payload: LoginRequest.User): UserModel = tx {
        val user = repository.findByEmail(payload.email)
            ?.takeIf { PasswordEncoder.matches(payload.password, it.password) }
            ?: unprocessable("Email or password is invalid")
        toModel(user)
    }

    fun register(payload: RegisterRequest.User): UserModel = try {
        tx {
            val encodedPwd = PasswordEncoder.encode(payload.password)
            toModel(repository.create(payload.copy(password = encodedPwd)))
        }
    } catch (e: ExposedSQLException) {
        when {
            e.isUniqueConstraintException("user_email_key") -> unprocessable("Email already exists")
            e.isUniqueConstraintException("user_username_key") -> unprocessable("Username already exists")
            else -> throw e
        }
    }

    fun getCurrentUser(userId: String): UserModel = tx {
        toModel(repository.getById(userId))
    }

    fun updateUser(userId: String, payload: UserUpdateRequest.User): UserModel = try {
        tx {
            val user = repository.getById(userId)
            val encodedPwd = payload.password?.let { PasswordEncoder.encode(it) }
            repository.update(user, payload.copy(password = encodedPwd))
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
        val user = repository.getByUsername(username)
        ProfileModel.fromUser(user, repository.isFollowed(followerId, user))
    }

    fun followUser(userId: String, followeeUsername: String): ProfileModel = tx {
        val followee = repository.getByUsername(followeeUsername)
        repository.followUser(userId, followee)
        ProfileModel.fromUser(followee, true)
    }

    fun unfollowUser(userId: String, followeeUsername: String): ProfileModel = tx {
        val followee = repository.getByUsername(followeeUsername)
        repository.unfollowUser(userId, followee)
        ProfileModel.fromUser(followee, false)
    }

    private fun toModel(user: User): UserModel = UserModel.fromUser(user, jwt.generateToken(user.id.value))
}

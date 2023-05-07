package cn.sabercon.realworld.user

import org.springframework.stereotype.Service

@Service
class UserService {

    fun login(payload: LoginRequest.User): UserModel {
        TODO()
    }

    fun register(payload: RegisterRequest.User): UserModel {
        TODO()
    }

    fun getCurrentUser(userId: String): UserModel {
        TODO()
    }

    fun updateUser(userId: String, payload: UserUpdateRequest.User): UserModel {
        TODO()
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
}

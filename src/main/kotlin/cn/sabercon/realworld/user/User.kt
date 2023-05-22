package cn.sabercon.realworld.user

import cn.sabercon.realworld.util.BaseUUIDEntity
import cn.sabercon.realworld.util.BaseUUIDEntityClass
import cn.sabercon.realworld.util.BaseUUIDTable
import cn.sabercon.realworld.util.string
import org.jetbrains.exposed.dao.id.EntityID

object Users : BaseUUIDTable("user") {
    val email = string("email").uniqueIndex()
    val username = string("username").uniqueIndex()
    val password = string("password")
    val bio = string("bio").nullable()
    val image = string("image").nullable()
}

class User(id: EntityID<String>) : BaseUUIDEntity(id, Users) {
    companion object : BaseUUIDEntityClass<User>(Users)

    var email by Users.email
    var username by Users.username
    var password by Users.password
    var bio by Users.bio
    var image by Users.image
}

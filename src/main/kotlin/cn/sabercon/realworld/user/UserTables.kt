package cn.sabercon.realworld.user

import cn.sabercon.realworld.util.*
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

object Users : BaseIdTable<String>("user") {
    override val id = string("id").clientDefault { UUID.randomUUID().toString() }.entityId()
    override val primaryKey = PrimaryKey(id)
    val email = string("email").uniqueIndex()
    val username = string("username").uniqueIndex()
    val password = string("password")
    val bio = string("bio").nullable()
    val image = string("image").nullable()
}

object UserFollows : BaseTable("user_follow") {
    val followerId = reference("follower_id", Users)
    val followeeId = reference("followee_id", Users)
    override val primaryKey = PrimaryKey(followerId, followeeId)
}

class User(id: EntityID<String>) : BaseEntity<String>(id, Users) {

    companion object : BaseEntityClass<String, User>(Users)

    var email by Users.email
    var username by Users.username
    var password by Users.password
    var bio by Users.bio
    var image by Users.image
}

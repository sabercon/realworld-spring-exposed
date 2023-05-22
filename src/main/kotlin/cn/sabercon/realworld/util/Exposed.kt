package cn.sabercon.realworld.util

import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.*

inline fun <T> tx(crossinline statement: Transaction.() -> T): T {
    return transaction {
        addLogger(StdOutSqlLogger)
        statement()
    }
}

fun Table.string(name: String, collate: String? = null): Column<String> {
    return varchar(name, Int.MAX_VALUE, collate)
}

fun ExposedSQLException.isUniqueConstraintException(key: String? = null): Boolean {
    return sqlState == "23505" && (key == null || key in message!!)
}

abstract class BaseLongIdTable(name: String = "", columnName: String = "id") : LongIdTable(name, columnName) {
    val createdAt = timestamp("created_at").clientDefault { Instant.now() }
    val updatedAt = timestamp("updated_at").nullable()
}

abstract class BaseLongEntity(id: EntityID<Long>, table: BaseLongIdTable) : LongEntity(id) {
    val createdAt by table.createdAt
    var updatedAt by table.updatedAt
}

abstract class BaseLongEntityClass<E : BaseLongEntity>(table: BaseLongIdTable) : LongEntityClass<E>(table) {
    init {
        EntityHook.subscribe { action ->
            if (action.changeType == EntityChangeType.Updated) {
                action.toEntity(this)!!.updatedAt = Instant.now()
            }
        }
    }
}

abstract class BaseUUIDTable(name: String = "", columnName: String = "id") : IdTable<String>(name) {
    final override val id = varchar(columnName, 10).clientDefault { UUID.randomUUID().toString() }.entityId()
    final override val primaryKey = PrimaryKey(id)

    val createdAt = timestamp("created_at").clientDefault { Instant.now() }
    val updatedAt = timestamp("updated_at").nullable()
}

abstract class BaseUUIDEntity(id: EntityID<String>, table: BaseUUIDTable) : Entity<String>(id) {
    val createdAt by table.createdAt
    var updatedAt by table.updatedAt
}

abstract class BaseUUIDEntityClass<E : BaseUUIDEntity>(table: BaseUUIDTable) : EntityClass<String, E>(table) {
    init {
        EntityHook.subscribe { action ->
            if (action.changeType == EntityChangeType.Updated) {
                action.toEntity(this)!!.updatedAt = Instant.now()
            }
        }
    }
}

package cn.sabercon.realworld.util

import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

inline fun <T> tx(crossinline statement: Transaction.() -> T): T {
    return transaction {
        addLogger(StdOutSqlLogger)
        statement()
    }
}

fun Table.string(name: String, collate: String? = null): Column<String> {
    return varchar(name, Int.MAX_VALUE, collate)
}

fun <T : Table> T.exists(op: T.(ISqlExpressionBuilder) -> Op<Boolean>): Boolean {
    return slice(Op.TRUE).select { op(this) }.empty().not()
}

fun ExposedSQLException.isUniqueConstraintException(key: String? = null): Boolean {
    return sqlState == "23505" && (key == null || key in message!!)
}

abstract class BaseTable(name: String = "") : Table(name) {
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp())
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp())
}

abstract class BaseIdTable<T : Comparable<T>>(name: String = "") : IdTable<T>(name) {
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp())
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp())
}

abstract class BaseEntity<ID : Comparable<ID>>(id: EntityID<ID>, table: BaseIdTable<ID>) : Entity<ID>(id) {
    val createdAt by table.createdAt
    var updatedAt by table.updatedAt
}

abstract class BaseEntityClass<ID : Comparable<ID>, out T : BaseEntity<ID>>(table: BaseIdTable<ID>) :
    EntityClass<ID, T>(table) {
    init {
        // To Autofill updated columns on entity change.
        // See [here](https://github.com/paulkagiri/ExposedDatesAutoFill/blob/master/src/main/kotlin/app/Models.kt).
        // This approach is not perfect because the update is separated from other update statements.
        // In the many-to-many relationship, it may update the timestamp even if the entity is not changed.
        // Also, it will not work if the entity is updated by raw SQL.
        EntityHook.subscribe { action ->
            if (action.changeType == EntityChangeType.Updated) {
                action.toEntity(this)?.updatedAt = Instant.now()
            }
        }
    }
}

abstract class BaseLongIdTable(name: String = "", columnName: String = "id") : BaseIdTable<Long>(name) {
    final override val id: Column<EntityID<Long>> = long(columnName).autoIncrement().entityId()
    final override val primaryKey = PrimaryKey(id)
}

abstract class BaseLongEntity(id: EntityID<Long>, table: BaseLongIdTable) : BaseEntity<Long>(id, table)

abstract class BaseLongEntityClass<out T : BaseLongEntity>(table: BaseLongIdTable) : BaseEntityClass<Long, T>(table)

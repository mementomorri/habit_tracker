package model.mainClasses

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
open class User (
        open val name: String,
        open var id: Int= -1

)

class UserTable: IntIdTable(){
    val name= varchar("name", 255)

    fun fill(builder: UpdateBuilder<Int>, item: User) {
        builder[name] = item.name
    }

    fun readResult(result: ResultRow): User? =
            User(
                    result[name],
                    result[id].value
            )
    fun addUser(user:User)= transaction {
        userTable.insertAndGetId { userTable.fill(it, user) }.value
        true
    }
}

val userTable= UserTable()
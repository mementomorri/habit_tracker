package model.main_classes

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
open class User (
        open val name: String,
        val password: String,
        val email: String? = null,
        open var id: Int= -1

)

class UserTable: IntIdTable(){
    val name= varchar("name", 255)
    val password= varchar("password", 50)
    val email= varchar("email", 255).nullable()

    fun fill(builder: UpdateBuilder<Int>, item: User) {
        builder[name] = item.name
        builder[password] = item.password
        builder[email] = item.email
    }

    fun readResult(result: ResultRow): User? =
            User(
                    result[name],
                    result[password],
                    result[email],
                    result[id].value
            )
    fun addUser(user:User)= transaction {
        userTable.insertAndGetId { userTable.fill(it, user) }.value
        true
    }
}

val userTable= UserTable()
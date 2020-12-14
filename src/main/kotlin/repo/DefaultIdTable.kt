package repo

import model.items.Item
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.update

abstract class DefaultIdTable<T>
    : IntIdTable(){
    abstract fun fill(builder: UpdateBuilder<Int>, item: T): Unit
    abstract fun readResult(result: ResultRow): T?

    fun insertAndGetIdItem(item: T) =
            insertAndGetId {
                fill(it, item)
            }

    fun updateItem(id: Int, dto: T) =
            update({
                this@DefaultIdTable.id eq id
            }){
                fill(it, dto)
            }
}
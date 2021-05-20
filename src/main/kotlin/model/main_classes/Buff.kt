package model.main_classes

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.`java-time`.date
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import java.time.LocalDate

@Serializable
open class Buff(
        val name:String,
        @Serializable(with=DateSerializer::class)
        val duration: LocalDate = LocalDate.now().plusDays(1),
        val character_id: Int
)

class BuffTable: Table(){
        val name = varchar("name", 50)
        val duration= date("duration")
        val character_id= reference("character_id", characterTable)
        fun fill(builder: UpdateBuilder<Int>, item: Buff) {
                builder[name] = item.name
                builder[duration] = item.duration
                builder[character_id] = item.character_id
        }

        fun readResult(result: ResultRow): Buff? =
                Buff(
                        result[name],
                        result[duration],
                        result[character_id].value
                )
}

val buffTable= BuffTable()
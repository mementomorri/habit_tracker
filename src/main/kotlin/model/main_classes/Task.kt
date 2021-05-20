package model.main_classes

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.`java-time`.date
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import java.time.LocalDate

@Serializer(forClass = LocalDate::class)
object DateSerializer : KSerializer<LocalDate> {

    override fun serialize(output: Encoder, obj: LocalDate) {
        output.encodeString(obj.toString())
    }

    override fun deserialize(input: Decoder): LocalDate {
        return LocalDate.parse(input.decodeString())
    }
}

@Serializable
open class Task (
        open var name: String,
        open var description: String,
        open var difficulty: String = "MEDIUM",
        val type: String,
        open val characterId: Int,
        @Serializable(with=DateSerializer::class)
        open var deadline: LocalDate?= null,
        @Serializable(with=DateSerializer::class)
        open val startDate: LocalDate?= null,
        open var completionCount: Int?= 0,
){

    fun checkDeadline():Boolean{ //true if there is time left, false otherwise
        return LocalDate.now()<deadline
    }


    override fun toString(): String {
        return "Task with: name=${name}, difficulty=${difficulty},type=${type}, characterId=${characterId}, completionCount=${completionCount}"
    }
}

fun Task.getDifficulty(): TaskDifficulty {
    return TaskDifficulty.valueOf(this.difficulty.toUpperCase())
}

fun Task.getType(): TaskType {
    return TaskType.valueOf(this.type.toUpperCase())
}

class TaskTable(): Table(){
    val name= varchar("name", 50)
    val description= varchar("description", 510)
    val difficulty= varchar("difficulty", 10)
    val type= varchar("type", 50)
    val characterId= reference("characterId", characterTable)
    val deadline= date("deadline").nullable()
    val startDate= date("startDate").nullable()
    val completionCount= integer("completionCount").nullable()

    fun fill(builder: UpdateBuilder<Int>, item: Task) {
        builder[name] = item.name
        builder[description] = item.description
        builder[difficulty] = item.difficulty
        builder[type] = item.type
        builder[characterId] = item.characterId
        builder[deadline] = item.deadline
        builder[startDate] = item.startDate
        builder[completionCount] = item.completionCount
    }

    fun readResult(result: ResultRow): Task? =
            Task(
                    result[name],
                    result[description],
                    result[difficulty],
                    result[type],
                    result[characterId].value,
                    result[deadline],
                    result[startDate],
                    result[completionCount]
            )
}

val taskTable= TaskTable()
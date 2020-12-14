package model.mainClasses

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
        open val name: String,
        open val description: String,
        open val difficulty: String = "MEDIUM",
        val type: String,
        open val characterId: Int,
        @Serializable(with=DateSerializer::class)
        open var deadline: LocalDate= LocalDate.MIN,
        @Serializable(with=DateSerializer::class)
        open val startDate: LocalDate= LocalDate.MIN,
        open var completionCount: Int= 0,
){
    val difficultyToInt: Int
        get() = getIntOfDifficulty()

    fun getIntOfDifficulty(): Int{
        return when(difficulty){
            "VERYEASY" -> 1
            "EASY" -> 2
            "MEDIUM" -> 3
            "HARD" -> 4
            "VERYHARD" -> 5
            else -> 1
        }
    }

    open val rewards= Reward(2*getIntOfDifficulty(),2*getIntOfDifficulty(),null)

    fun checkDeadline():Boolean{ //true if there is time left, false otherwise
        return LocalDate.now()<deadline
    }

//    fun getIntOfDifficulty(): Int{
//        return when(TaskDifficulty.valueOf(difficulty.toUpperCase())){
//            TaskDifficulty.VERYEASY -> 1
//            TaskDifficulty.EASY -> 2
//            TaskDifficulty.MEDIUM -> 3
//            TaskDifficulty.HARD -> 4
//            TaskDifficulty.VERYHARD -> 5
//        }
//    }

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
    var deadline= date("deadline")
    val startDate= date("startDate")
    var completionCount= integer("completionCount")

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
package model.main_classes

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.`java-time`.date
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import java.time.LocalDate

open class Task (
        open val name: String,
        open val description: String,
        open val difficulty: String,
        val type: String,
        open val characterId: Int,
        open var deadline: LocalDate= LocalDate.MIN,
        open val startDate: LocalDate= LocalDate.MIN,
        open var completionCount: Int= 0,
        open val difficultyToInt: Int= when(TaskDifficulty.valueOf(difficulty.toUpperCase())){
            TaskDifficulty.VERYEASY -> 1
            TaskDifficulty.EASY -> 2
            TaskDifficulty.MEDIUM -> 3
            TaskDifficulty.HARD -> 4
            TaskDifficulty.VERYHARD -> 5
        }
){
    override fun toString(): String {
        return "name=${name}, difficulty=${difficulty},type=${type}, characterId=${characterId}, completionCount=${completionCount}"
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
    val difficultyToInt= integer("difficultyToInt")

    fun fill(builder: UpdateBuilder<Int>, item: Task) {
        builder[name] = item.name
        builder[description] = item.description
        builder[difficulty] = item.difficulty
        builder[type] = item.type
        builder[characterId] = item.characterId
        builder[deadline] = item.deadline
        builder[startDate] = item.startDate
        builder[completionCount] = item.completionCount
        builder[difficultyToInt] = item.difficultyToInt
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
                    result[completionCount],
                    result[difficultyToInt]
            )
}

val taskTable= TaskTable()
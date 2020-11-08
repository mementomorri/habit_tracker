package model

import java.time.LocalDate

class Daily (
    override val name: String,
    override val description: String,
    override val difficulty: TaskDifficulty

): Task{
    override val type: TaskType=TaskType.Daily
    val deadline: LocalDate = LocalDate.now().plusDays(1)
    var completionCount: Int = 0
    override val rewards: Reward=Reward(2*difficultyToInt,2*difficultyToInt,null)

    fun checkDeadline():Boolean{ //true if there is time left, false otherwise
        return LocalDate.now()<deadline
    }
}
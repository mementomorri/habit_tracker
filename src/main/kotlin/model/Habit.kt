package model

import java.time.LocalDate


class Habit(
        override val name: String,
        override val description: String,
        override val difficulty: TaskDifficulty,
        val startDay: LocalDate = LocalDate.now()
): Task{
    var completionCount: Int = 0
    override val type: TaskType= TaskType.Habit
    override val rewards: Reward= Reward(2*difficultyToInt,2*difficultyToInt,null)
}

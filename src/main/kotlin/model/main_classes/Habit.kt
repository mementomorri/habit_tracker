package model.main_classes

import java.time.LocalDate

class Habit(
        override val name: String,
        override val description: String,
        override val difficulty: String,
        override val characterId: Int,
        override var completionCount: Int= 0
): Task(name,description,difficulty,"HABIT",characterId,startDate = LocalDate.now(),completionCount = completionCount) {
    val rewards: Reward = Reward(2*difficultyToInt,2*difficultyToInt,null)


}
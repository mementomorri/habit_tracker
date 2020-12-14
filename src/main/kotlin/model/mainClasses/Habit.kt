package model.mainClasses

import kotlinx.serialization.Serializable
import java.time.LocalDate

class Habit(
        override val name: String,
        override val description: String,
        override val difficulty: String,
        override val characterId: Int,
        override var completionCount: Int= 0
): Task(name,description,difficulty,"HABIT",characterId,startDate = LocalDate.now(),completionCount = completionCount) {

    override val rewards: Reward = Reward(2*getIntOfDifficulty(),2*getIntOfDifficulty(),null)


}
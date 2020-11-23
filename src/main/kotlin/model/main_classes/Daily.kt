package model.main_classes

import java.time.LocalDate

class Daily (
    override val name: String,
    override val description: String,
    override val difficulty: String,
    override val characterId: Int,
    override var completionCount: Int= 0
): Task(name,description,difficulty,"DAILY",characterId, LocalDate.now().plusDays(1),completionCount = completionCount) {
    val rewards: Reward = Reward(2*difficultyToInt,2*difficultyToInt,null)

    fun checkDeadline():Boolean{ //true if there is time left, false otherwise
        return LocalDate.now()<deadline
    }
}
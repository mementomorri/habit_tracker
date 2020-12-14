package model.mainClasses

import java.time.LocalDate

class Daily (
    override val name: String,
    override val description: String,
    override val difficulty: String,
    override val characterId: Int,
    override var completionCount: Int= 0
): Task(name,description,difficulty,"DAILY",characterId, LocalDate.now().plusDays(1),completionCount = completionCount) {
    override val rewards: Reward = Reward(2*getIntOfDifficulty(),2*getIntOfDifficulty(),null)

//    fun checkDeadline():Boolean{ //true if there is time left, false otherwise
//        return LocalDate.now()<deadline
//    }
}
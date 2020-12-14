package model.quests

import model.mainClasses.Reward
import model.mainClasses.Task

open class Quest(
    override val name: String,
    override val description: String,
    override val difficulty: String,
    override val characterId: Int,
): Task(name,description,difficulty,"QUEST",characterId){
//    val rewardList: List<Item>? = when(TaskDifficulty.valueOf(difficulty)){
//        TaskDifficulty.MEDIUM -> listOf<Item>(HealingPotion(2))
//        TaskDifficulty.HARD -> listOf(HealingPotion(2), GreenTea(2))
//        TaskDifficulty.VERYHARD -> listOf(HealingPotion(2), GreenTea(2), Coffee(2))
//        else -> listOf(GreenTea(1))
//    }
override val rewards: Reward = Reward(3* getIntOfDifficulty(),3* getIntOfDifficulty(), null)
}
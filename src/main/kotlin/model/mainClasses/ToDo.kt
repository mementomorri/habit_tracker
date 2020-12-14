package model.mainClasses

import model.items.Coffee
import model.items.GreenTea
import model.items.HealingPotion
import model.items.Item
import java.time.LocalDate

class ToDo (
    override val name: String,
    override val description: String,
    override val difficulty: String,
    override val characterId: Int,
    override var deadline: LocalDate = LocalDate.now().plusWeeks(1),
    val subTasks: List<String>?= null
): Task(name,description,difficulty,"TODO",characterId, deadline = deadline) {
    private val rewardList= when(TaskDifficulty.valueOf(difficulty)){
        TaskDifficulty.MEDIUM -> listOf<Item>(HealingPotion(1))
        TaskDifficulty.HARD -> listOf(HealingPotion(1), GreenTea(1))
        TaskDifficulty.VERYHARD -> listOf(HealingPotion(1), GreenTea(1), Coffee(1))
        else -> null
    }
    override val rewards: Reward = Reward((2.5*getIntOfDifficulty()).toInt(),(2.5*getIntOfDifficulty()).toInt(), rewardList)

//    fun checkDeadline():Boolean{ //true if there is time left, false otherwise
//        return LocalDate.now()<deadline
//    }
}
package model

import items.Coffee
import items.GreenTea
import items.HealingPotion
import items.Item
import java.time.LocalDate

class ToDo (
    override val name: String,
    override val description: String,
    override val difficulty: TaskDifficulty,
    val subTasks: List<String>?
): Task{
    override val type: TaskType=TaskType.ToDo
    val deadline: LocalDate = LocalDate.now().plusWeeks(1)
    private val rewardList= when(difficulty){
        TaskDifficulty.Medium -> listOf<Item>(HealingPotion(1))
        TaskDifficulty.Hard -> listOf(HealingPotion(1),GreenTea(1))
        TaskDifficulty.Veru_Hard -> listOf(HealingPotion(1),GreenTea(1), Coffee(1))
        else -> null
    }
    override val rewards: Reward= Reward((2.5*difficultyToInt).toInt(),(2.5*difficultyToInt).toInt(), rewardList)

    fun checkDeadline():Boolean{ //true if there is time left, false otherwise
        return LocalDate.now()<deadline
    }
}
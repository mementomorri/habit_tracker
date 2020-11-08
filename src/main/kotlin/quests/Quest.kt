package quests

import items.Coffee
import items.GreenTea
import items.HealingPotion
import items.Item
import model.Reward
import model.Task
import model.TaskDifficulty
import model.TaskType

interface Quest: Task {
    override val name: String
    override val description: String
    override val difficulty: TaskDifficulty
    override val rewards: Reward
    override val type: TaskType
        get() = TaskType.Quest
    override val difficultyToInt: Int
        get() = when(difficulty){
            TaskDifficulty.Very_easy -> 1
            TaskDifficulty.Easy -> 2
            TaskDifficulty.Medium -> 3
            TaskDifficulty.Hard -> 4
            TaskDifficulty.Veru_Hard -> 5
        }
    val rewardList: List<Item>?
        get()= when(difficulty){
            TaskDifficulty.Medium -> listOf<Item>(HealingPotion(2))
            TaskDifficulty.Hard -> listOf(HealingPotion(2), GreenTea(2))
            TaskDifficulty.Veru_Hard -> listOf(HealingPotion(2), GreenTea(2), Coffee(2))
            else -> null
    }
}
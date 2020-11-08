package quests

import model.Reward
import model.TaskDifficulty
import model.TaskType

class DustRabbits (
        override val difficulty: TaskDifficulty
): Quest{
    override val name: String= "Dust rabbits"
    override val description: String= "Word on the streets is: there is dust rabbits at the desert near the town, they eat dust, reproduce them self and spread dust around, " +
            "your mission is to clean up every corner at your home that'll make them stop and leave our great town of productive folks."
    override val rewards: Reward= Reward(3*difficultyToInt,3*difficultyToInt, rewardList)
}
package quests

import model.Reward
import model.TaskDifficulty

class DishDisaster (
        override val difficulty: TaskDifficulty
): Quest{
    override val name: String= "Dish disaster"
    override val description: String= "You walk around Creaky-Clean Lake for some well-earned relaxation." +
            "But the lake is polluted with unwashed dishes! How did this happen? Well, you simply cannot allow the lake to be in such a state. " +
            "There is only one thing you can do: clean the dishes and save your vacation spot! Better find some soap to clean up this mess. A lot of soap..."
    override val rewards: Reward = Reward(3*difficultyToInt,3*difficultyToInt, rewardList)
}
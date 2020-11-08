package quests

import model.Reward
import model.TaskDifficulty

class MagicInBottle (
        override val difficulty: TaskDifficulty
):Quest{
    override val name: String= "Magic in a bottle"
    override val description: String= "You've been at the beach looking at the waves before a sleep as usual, but you accidently saw bottle drift at the beach. " +
            "Once you open that bottle you found out a message there that says:'You need to stretch every two hours when sit at a table for a week', once you read it magic sparks " +
            "came out and you felt that there is a spell on you. This spell reminds you to always stretch every two hours and to get rid of it you need to just keep that habit " +
            "for a week."
    override val rewards: Reward= Reward(3*difficultyToInt,3*difficultyToInt, rewardList)
}
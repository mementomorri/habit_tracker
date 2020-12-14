package model.quests

import model.mainClasses.Reward

class MagicInBottle (
        override val difficulty: String,
        override val characterId: Int
): Quest(
        "Magic in a bottle",
        "You've been at the beach looking at the waves before a sleep as usual, but you accidently saw bottle drift at the beach. " +
        "Once you open that bottle you found out a message there that says:'You need to stretch every two hours when sit at a table for a week', once you read it magic sparks " +
        "came out and you felt that there is a spell on you. This spell reminds you to always stretch every two hours and to get rid of it you need to just keep that habit " +
        "for a week.",
        difficulty,
        characterId
) {
    override val rewards: Reward = Reward(3* getIntOfDifficulty(),3* getIntOfDifficulty(), null)
}
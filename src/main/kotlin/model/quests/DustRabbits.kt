package model.quests

import model.main_classes.Reward

class DustRabbits (
        override val characterId: Int,
        override val difficulty: String="VERYHARD"
): Quest(
        "Dust rabbits",
        "Word on the streets is: there is dust rabbits at the desert near the town, they eat dust, reproduce them self and spread dust around, " +
        "your mission is to clean up every corner at your home that'll make them stop and leave our great town of productive folks.",
        difficulty,
        characterId
) {
    override val rewards: Reward = Reward(3* difficultyToInt,3* difficultyToInt, null)
}
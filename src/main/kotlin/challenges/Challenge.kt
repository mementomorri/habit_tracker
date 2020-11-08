package challenges

import model.Character
import model.Reward

interface Challenge {
    val name: String
    val description: String
    val rewards: Reward

    fun checkChallengeCondition(character: Character): Boolean //true if condition passed, false otherwise
    fun getReward(character: Character): Boolean //true if reward added to character, false otherwise
}
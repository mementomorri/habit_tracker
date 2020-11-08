package challenges

import model.Character
import model.Reward

class SolidHabit (): Challenge{
    override val name: String= "Solid habit"
    override val description: String= "Get at least one solid habit, complete it at least 90 times"
    override val rewards: Reward= Reward(0, 15, null)

    override fun checkChallengeCondition(character: Character): Boolean {
        return character.habits.firstOrNull{it.completionCount>= 90} != null
    }

    override fun getReward(character: Character): Boolean{
        return if (checkChallengeCondition(character)) {
            rewards.getReward(character)
            true
        } else false
    }
}

val solidHabit= SolidHabit()
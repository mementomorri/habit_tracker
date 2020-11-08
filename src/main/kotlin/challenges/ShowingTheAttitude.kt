package challenges

import model.Character
import model.Reward

class ShowingTheAttitude (): Challenge{
    override val name: String= "Showing the attitude"
    override val description: String= "Complete very hard daily at least 5 times"
    override val rewards: Reward= Reward(0, 20, null)

    override fun checkChallengeCondition(character: Character): Boolean {
        return character.dailies.firstOrNull{it.completionCount>= 5} != null
    }

    override fun getReward(character: Character): Boolean{
        return if (checkChallengeCondition(character)) {
            rewards.getReward(character)
            true
        } else false
    }
}

val showingTheAttitude= ShowingTheAttitude()
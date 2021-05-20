package model.challenges

import model.main_classes.Reward
import model.main_classes.Character

class ShowingTheAttitude ():Challenge(
        "Showing the attitude",
        "Complete very hard daily at least 5 times"
) {
    override val rewards: Reward = Reward(0, 20, null)

    override fun checkChallengeCondition(character: Character): Boolean {
        return character.dailies.firstOrNull{it.completionCount!!>= 5} != null
    }
}

val showingTheAttitude= ShowingTheAttitude()
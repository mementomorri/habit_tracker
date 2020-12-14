package model.challenges

import model.mainClasses.Character
import model.mainClasses.Reward

class SolidHabit ():Challenge(
        "Solid habit",
        "Get at least one solid habit, complete it at least 90 times"
) {
    override val rewards: Reward = Reward(0, 15, null)

    override fun checkChallengeCondition(character: Character): Boolean {
        return character.habits.firstOrNull{it.completionCount>= 90} != null
    }
}

val solidHabit= SolidHabit()
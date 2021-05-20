package model.challenges

import model.main_classes.Character
import model.main_classes.Reward

class SolidHabit ():Challenge(
        "Solid habit",
        "Get at least one solid habit, complete it at least 90 times"
) {
    override val rewards: Reward = Reward(0, 15, null)

    override fun checkChallengeCondition(character: Character): Boolean {
        return character.habits.firstOrNull{ it.completionCount!! >= 90} != null
    }
}

val solidHabit= SolidHabit()
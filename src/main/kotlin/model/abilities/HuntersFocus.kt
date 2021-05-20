package model.abilities

import model.main_classes.charactersRepo
import model.main_classes.Character

class HuntersFocus (): Ability(
        "Hunter's focus",
        "ARCHER",
        "Archer breathes deeply and focuses on theirs tasks, that makes them confident and raise up the spirit",
        20,
        3
) {
    fun useAbility(character: Character): Boolean {
        return if (character.energyPoints>= energyRequired){
            character.experiencePoints+=(character.level*2.5).toInt()
            character.checkExperience()
            character.experiencePoints.minus(energyRequired)
            charactersRepo.update(character.id, character)
            true
        } else false
    }
}

val huntersFocus= HuntersFocus()
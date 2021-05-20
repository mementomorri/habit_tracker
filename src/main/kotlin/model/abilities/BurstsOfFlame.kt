package model.abilities

import model.main_classes.charactersRepo
import model.main_classes.Character

class BurstsOfFlame(): Ability(
        "Bursts of flame",
        "MAGICIAN",
        "Magician focuses at they're target so there is little sparks around it",
        20,
        3
) {
    fun useAbility(character: Character): Boolean {
        return if (character.energyPoints >= energyRequired){
            character.experiencePoints+=(character.level*3)
            character.checkExperience()
            character.energyPoints.minus(energyRequired)
            charactersRepo.update(character.id, character)
            true
        } else false
    }
}

val burstsOfFlame = BurstsOfFlame()
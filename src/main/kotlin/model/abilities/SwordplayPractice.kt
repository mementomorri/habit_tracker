package model.abilities

import model.main_classes.charactersRepo
import model.main_classes.Character

class SwordplayPractice(): Ability(
        "Swordplay practice",
        "WARRIOR",
        "Warrior focuses on they're swordplay skill while swinging it at the dummy ",
        20,
        3
) {
    fun useAbility(character: Character): Boolean {
        return if (character.energyPoints>= energyRequired){
            character.experiencePoints+=(character.level*2)
            character.checkExperience()
            character.energyPoints.minus(energyRequired)
            charactersRepo.update(character.id, character)
            true
        }else false
    }
}

val swordplayPractice= SwordplayPractice()
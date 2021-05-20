package model.abilities

import model.main_classes.Character

class FrostChill(): Ability(
        "Frost chill",
        "MAGICIAN",
        "Magician spins around them and makes air around become cold, time freezes and stops for a while",
        40,
        9
) {
    fun useAbility(character: Character): Boolean {
        return if (character.energyPoints >= energyRequired){
            character.dailies.forEach {
                it.deadline?.plusDays(1)
            }
            character.toDos.forEach {
                it.deadline?.plusDays(1)
            }
            character.energyPoints.minus(energyRequired)
            true
        } else false
    }
}

val frostChill= FrostChill()
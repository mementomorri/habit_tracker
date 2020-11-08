package abilities

import model.CharacterClass
import model.Character

class FrostChill(): Ability{
    override val name: String= "Frost chill"
    override val characterClass: CharacterClass= CharacterClass.Magician
    override val description: String= "Magician spins around them and makes air around become cold, time freezes and stops for a while"
    override val energyRequired: Int= 40
    override val levelRequired: Int= 9

    override fun useAbility(character: Character): Boolean {
        return if (character.energyPoints >= energyRequired){
            character.dailies.forEach {
                it.deadline.plusDays(1)
            }
            character.toDos.forEach {
                it.deadline.plusDays(1)
            }
            character.energyPoints.minus(energyRequired)
            true
        } else false
    }
}

val frostChill= FrostChill()
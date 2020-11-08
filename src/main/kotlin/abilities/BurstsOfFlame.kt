package abilities

import model.CharacterClass
import model.Character

class BurstsOfFlame():Ability{
    override val name: String= "Bursts of flame"
    override val characterClass: CharacterClass= CharacterClass.Magician
    override val description: String= "Magician focuses at they're target so there is little sparks around it"
    override val energyRequired: Int= 20
    override val levelRequired: Int= 3

    override fun useAbility(character: Character): Boolean {
        return if (character.energyPoints >= energyRequired){
            character.experiencePoints+=(character.level*3)
            character.checkExperience()
            character.energyPoints.minus(energyRequired)
            true
        } else false
    }
}

val burstsOfFlame = BurstsOfFlame()
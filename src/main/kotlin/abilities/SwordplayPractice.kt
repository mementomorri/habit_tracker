package abilities

import model.CharacterClass
import model.Character

class SwordplayPractice(): Ability{
    override val name: String= "Swordplay practice"
    override val characterClass: CharacterClass= CharacterClass.Warrior
    override val description: String= "Warrior focuses on they're swordplay skill while swinging it at the dummy "
    override val energyRequired: Int= 20
    override val levelRequired: Int= 3

    override fun useAbility(character: Character): Boolean {
        return if (character.energyPoints>= energyRequired){
            character.experiencePoints+=(character.level*2)
            character.checkExperience()
            character.energyPoints.minus(energyRequired)
            true
        }else false
    }
}

val swordplayPractice= SwordplayPractice()
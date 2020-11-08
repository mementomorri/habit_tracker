package abilities

import model.CharacterClass
import model.Character

class HuntersFocus ():Ability{
    override val name: String= "Hunter's focus"
    override val characterClass: CharacterClass= CharacterClass.Archer
    override val description: String= "Archer breathes deeply and focuses on theirs tasks, that makes them confident and raise up the spirit"
    override val energyRequired: Int= 20
    override val levelRequired: Int= 3

    override fun useAbility(character: Character): Boolean {
        return if (character.energyPoints>= energyRequired){
            character.experiencePoints+=(character.level*2.5).toInt()
            character.checkExperience()
            character.experiencePoints.minus(energyRequired)
            true
        } else false
    }
}

val huntersFocus= HuntersFocus()
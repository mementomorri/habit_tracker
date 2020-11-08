package abilities

import model.CharacterClass
import model.Character

class CheeringWords(): Ability{
    override val name: String= "Cheering words"
    override val characterClass: CharacterClass= CharacterClass.Magician
    override val description: String= "Magician says some inspiring words to other person that heals they're heart or inspires themself by thinking about good things"
    override val energyRequired: Int= 40
    override val levelRequired: Int= 6

    override fun useAbility(character: Character): Boolean {
        return if (character.maximumHP == character.healthPoints || character.energyPoints< energyRequired){
            false
        } else {
            character.healthPoints.plus((character.level*4))
            if (character.healthPoints>character.maximumHP) character.healthPoints=character.maximumHP
            character.energyPoints.minus(energyRequired)
            true
        }
    }

    fun healOtherPerson(caster: Character, personToHeal: Character): Boolean{
        return if (personToHeal.maximumHP == personToHeal.healthPoints || caster.energyPoints< energyRequired){
            false
        } else {
            personToHeal.healthPoints.plus(caster.level*4)
            if (personToHeal.healthPoints> personToHeal.maximumHP) personToHeal.healthPoints=(personToHeal.maximumHP)
            caster.energyPoints.minus(energyRequired)
            true
        }
    }
}

val cheeringWords= CheeringWords()
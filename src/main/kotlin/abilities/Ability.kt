package abilities

import model.CharacterClass
import model.Character

interface Ability{
    val name: String
    val characterClass: CharacterClass
    val description: String
    val energyRequired: Int
    val levelRequired: Int

    fun useAbility(character: Character): Boolean //true if ability is successfully used, false otherwise
}
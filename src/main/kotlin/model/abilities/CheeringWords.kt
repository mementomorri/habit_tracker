package model.abilities

import model.main_classes.charactersRepo
import model.main_classes.Character

class CheeringWords(): Ability(
        "Cheering words",
        "MAGICIAN",
        "Magician says some inspiring words to other person that heals they're heart or inspires themself by thinking about good things",
        40,
        6
) {
    fun useAbility(character: Character): Boolean {
        return if (character.maximumHP == character.healthPoints || character.energyPoints< energyRequired){
            false
        } else {
            character.healthPoints.plus((character.level*4))
            if (character.healthPoints>character.maximumHP) character.healthPoints=character.maximumHP
            character.energyPoints.minus(energyRequired)
            charactersRepo.update(character.id, character)
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
            charactersRepo.update(caster.id, caster)
            charactersRepo.update(personToHeal.id, personToHeal)
            true
        }
    }
}

val cheeringWords= CheeringWords()
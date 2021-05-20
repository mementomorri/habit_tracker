package model.abilities

import model.main_classes.charactersRepo
import model.main_classes.Buff
import model.main_classes.Character
import model.main_classes.buffTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

class FriendlyProtection (): Ability(
        "Friendly protection",
        "WARRIOR",
        "Warrior stands up for they're nearest friend and protects them from suffer of fail or protects themself with a shield",
        40,
        6
) {
    fun useAbility(character: Character): Boolean {
        return if (character.energyPoints>= energyRequired){
            transaction {
                buffTable.insert { fill(it, Buff("Friendly protection", character_id = character.id)) }
            }
            character.energyPoints.minus(energyRequired)
            charactersRepo.update(character.id, character)
            true
        } else false
    }

    fun protectOtherPerson(caster: Character, personToProtect: Character):Boolean{
        return if (caster.energyPoints>= energyRequired){
            transaction {
                buffTable.insert { fill(it, Buff("Friendly protection", character_id = personToProtect.id)) }
            }
            caster.energyPoints.minus(energyRequired)
            charactersRepo.update(caster.id, caster)
            charactersRepo.update(personToProtect.id, personToProtect)
            true
        } else false
    }
}

val friendlyProtection= FriendlyProtection()
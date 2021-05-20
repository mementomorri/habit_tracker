package model.abilities

import model.main_classes.charactersRepo
import model.main_classes.Buff
import model.main_classes.Character
import model.main_classes.buffTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

class GreedyProfit (): Ability(
        "Greedy profit",
        "ARCHER",
        "Archer tries shoot every target possible as quickly as they're swiftness let them",
        40,
        6
) {
    fun useAbility(character: Character): Boolean {
        return if (character.energyPoints>=energyRequired){
            transaction {
                buffTable.insert { fill(it, Buff("Greedy", character_id = character.id)) }
            }
            character.energyPoints.minus(40)
            charactersRepo.update(character.id, character)
            true
        } else false
    }
}

val greedyProfit= GreedyProfit()
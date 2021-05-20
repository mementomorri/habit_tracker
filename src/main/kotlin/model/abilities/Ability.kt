package model.abilities

import kotlinx.serialization.Serializable
import model.main_classes.characterTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import repo.DefaultIdTable

@Serializable
open class Ability(
    val name: String,
    val characterClass: String,
    val description: String,
    val energyRequired: Int,
    val levelRequired: Int,
    val id: Int = -1
){
    override fun toString(): String {
        return "name=${name}, characterClass=${characterClass}, energyRequired=${energyRequired}, levelRequired=${levelRequired}, id=${id}"
    }
}

class AbilityTable: DefaultIdTable<Ability>(){
    val name= varchar("name", 50)
    val characterClass=  varchar("characterClass",50)
    val description= varchar("description", 255)
    val energyRequired= integer("energyRequired")
    val levelRequired= integer("levelRequired")
    override fun fill(builder: UpdateBuilder<Int>, item: Ability) {
        builder[name]= item.name
        builder[characterClass]= item.characterClass
        builder[description]= item.description
        builder[energyRequired]= item.energyRequired
        builder[levelRequired]= item.levelRequired
    }

    override fun readResult(result: ResultRow): Ability?=
            Ability(
                    result[name],
                    result[characterClass],
                    result[description],
                    result[energyRequired],
                    result[levelRequired],
                    result[id].value
            )
}

val abilityTable= AbilityTable()

class CharacterAbilityFiller(
        val ability_id: Int,
        val character_id: Int,
        val id: Int= -1
){
    override fun toString(): String {
        return "id=${id}, ability_id=${ability_id}, character_id=${character_id}"
    }
}

class CharacterAbilityTable: DefaultIdTable<CharacterAbilityFiller>(){
    val ability_id= reference("ability_id", abilityTable)
    val character_id= reference("character_id", characterTable)

    override fun fill(builder: UpdateBuilder<Int>, item: CharacterAbilityFiller) {
        builder[ability_id]= item.ability_id
        builder[character_id]= item.character_id
    }

    override fun readResult(result: ResultRow)=
            CharacterAbilityFiller(
                    result[ability_id].value,
                    result[character_id].value,
                    result[id].value
            )
}

val characterAbilityTable= CharacterAbilityTable()
package model.challenges

import model.main_classes.charactersRepo
import kotlinx.serialization.Serializable
import model.main_classes.Reward
import model.main_classes.Character
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.UpdateBuilder

@Serializable
open class Challenge (
    val name: String,
    val description: String
) {
    open val rewards: Reward = Reward(0, 20, null)

    open fun checkChallengeCondition(character: Character): Boolean=true //true if condition passed, false otherwise

    open fun getReward(character: Character){
        if (checkChallengeCondition(character)) {
            rewards.getReward(character)
        }
    }
}

fun Challenge.checkShowingTheAttitudeCond(characterId: Int) :Boolean{
    val character= charactersRepo.read(characterId)
    return if (character == null) false else {
        character.dailies.firstOrNull { it.completionCount!! >= 5 } != null
    }
}

class ChallengeTable: Table(){
    val name = varchar("name", 50)
    val description= varchar("description", 255)
    fun fill(builder: UpdateBuilder<Int>, item: Challenge) {
        builder[name] = item.name
        builder[description] = item.description
    }

     fun readResult(result: ResultRow): Challenge? =
             Challenge(
                    result[name],
                    result[description]
            )
}

val challenges= ChallengeTable()
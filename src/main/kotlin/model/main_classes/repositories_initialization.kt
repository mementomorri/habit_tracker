package model.main_classes

import model.abilities.abilityTable
import model.items.itemTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import repo.AppRepo

val charactersRepo= AppRepo(characterTable)
val shopRepo= AppRepo(itemTable)
val abilitiesRepo= AppRepo(abilityTable)

class UserCharacterFiller(
        val userId: Int,
        val characterId: Int,
        val id:Int = -1
)

class UserCharacterTable: IntIdTable(){
    val userId= reference("userId", userTable)
    val characterId= reference("characterId", characterTable)

    fun fill (builder: UpdateBuilder<Int>, item: UserCharacterFiller){
        builder[userId] = item.userId
        builder[characterId] = item.characterId
    }

    fun readResult(result: ResultRow): UserCharacterFiller? =
            UserCharacterFiller(
                    result[userId].value,
                    result[characterId].value,
                    result[id].value
            )
    fun addPossession(userId: Int, characterId: Int)= transaction{
        userCharacterTable.insertAndGetId { userCharacterTable.fill(it, UserCharacterFiller(userId, characterId)) }.value
        true
    }
}

val userCharacterTable= UserCharacterTable()
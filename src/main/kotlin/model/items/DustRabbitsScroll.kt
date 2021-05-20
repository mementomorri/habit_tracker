package model.items

import model.main_classes.charactersRepo
import model.main_classes.taskTable
import model.quests.DustRabbits
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

class DustRabbitsScroll (
        override var quantity: Int
): Item(quantity,"Dust rabbits scroll", "Some old scroll describing the story about Dust rabbits disaster", 6) {

    fun useItem(characterId: Int) {
        val character= charactersRepo.read(characterId)
        if (character == null){
            return
        } else {
            if (quantity >= 1) {
                transaction {
                    taskTable.insert {
                        fill(it, DustRabbits(characterId))
                    }
                }
                transaction {
                    characterItemTable.updateItem(this@DustRabbitsScroll.id, CharacterItemFiller(this@DustRabbitsScroll.id, characterId, this@DustRabbitsScroll.quantity--, this@DustRabbitsScroll.id))
                }
                if (quantity-- <= 0) transaction {
                    characterItemTable.deleteWhere { (characterItemTable.character_id eq characterId) and  (characterItemTable.item_id eq this@DustRabbitsScroll.id)}
                }
            }
        }
    }

    override fun create(quantity: Int): Item {
        return DustRabbitsScroll(quantity)
    }
}
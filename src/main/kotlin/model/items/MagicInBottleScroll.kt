package model.items

import model.main_classes.charactersRepo
import model.main_classes.taskTable
import model.quests.MagicInBottle
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

class MagicInBottleScroll(
        override var quantity: Int
): Item(quantity, "Magic in bottle scroll", "Some old scroll describing the story of Magic in bottle", 5) {

    fun useItem(characterId: Int) {
        val character= charactersRepo.read(characterId)
        if (character== null){
            return
        }else {
            if (quantity >= 1) {
                transaction {
                    taskTable.insert {
                        fill(it, MagicInBottle("MEDIUM", characterId))
                    }
                }
                quantity--
                if (quantity <= 0) transaction {
                    characterItemTable.deleteWhere { (characterItemTable.character_id eq characterId) and  (characterItemTable.item_id eq this@MagicInBottleScroll.id)}
                }
            }
        }
    }

    override fun create(quantity: Int): Item {
        return MagicInBottleScroll(quantity)
    }
}
package model.items

import model.main_classes.charactersRepo
import model.main_classes.taskTable
import model.quests.DishDisaster
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

class DishDisasterScroll (
        override var quantity: Int
): Item(quantity,"Dish disaster scroll","Some old scroll describing the story of Dish disaster",7) {

    fun useItem(characterId: Int){
        val character= charactersRepo.read(characterId)
        if (character == null){
            return
        }else {
            if (quantity >= 1) {
                transaction {
                    taskTable.insert {
                        fill(it, DishDisaster("VERYHARD", characterId))
                    }
                }
                quantity--
                if (quantity <= 0) transaction {
                    characterItemTable.deleteWhere { (characterItemTable.character_id eq characterId) and  (characterItemTable.item_id eq this@DishDisasterScroll.id)}
                }
            }
        }
    }

    override fun create(quantity: Int): Item {
        return DishDisasterScroll(quantity)
    }
}
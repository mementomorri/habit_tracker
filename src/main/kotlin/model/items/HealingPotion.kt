package model.items

import model.main_classes.charactersRepo
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction

class HealingPotion (
        override var quantity: Int
): Item(quantity,"Healing potion","This little warm potion restores little amount of your life energy, tastes like cough syrup", 12) {

    fun useItem(characterId: Int) {
        val character= charactersRepo.read(characterId)
        if (character== null){
            return
        }else {
            if (quantity >= 1) {
                character.healthPoints.plus(15)
                if (character.healthPoints > character.maximumHP) character.healthPoints = character.maximumHP
                quantity--
                if (quantity <= 0) transaction {
                    characterItemTable.deleteWhere { (characterItemTable.character_id eq characterId) and  (characterItemTable.item_id eq this@HealingPotion.id)}
                }
            }
        }
    }

    override fun create(quantity: Int): Item {
        return HealingPotion(quantity)
    }
}
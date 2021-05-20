package model.items

import model.main_classes.charactersRepo
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction

class Coffee (
        override var quantity: Int,
        override val id: Int = -1
): Item(quantity,"Coffee", "Nice and warm cup of coffee to raise an adventurer spirit", 20, id = id ) {

    fun useItem(characterId: Int) {
        val character= charactersRepo.read(characterId)
         if (character == null){
            return
        }else {
            if (quantity >= 1) {
                character.energyPoints.plus(30)
                if (character.energyPoints > 100) character.energyPoints = 100
                quantity--
                if (quantity <= 0) transaction {
                 characterItemTable.deleteWhere { (characterItemTable.character_id eq characterId) and  (characterItemTable.item_id eq this@Coffee.id)}
                }
            }
        }
    }

    override fun create(quantity: Int): Item {
        return Coffee(quantity)
    }
}
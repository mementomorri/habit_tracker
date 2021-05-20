package model.items

import model.main_classes.charactersRepo
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction

class GreenTea (
        override var quantity: Int
): Item(quantity,"Green tea","A cup of tasty green tea, after a few sips it restores some of your energy",9) {

    fun useItem(characterId: Int){
        val character= charactersRepo.read(characterId)
        if (character == null){
            return
        }else {
            if (quantity >= 1) {
                character.energyPoints.plus(15)
                if (character.energyPoints > 100) character.energyPoints = 100
                quantity--
                if (quantity <= 0) transaction {
                    characterItemTable.deleteWhere { (characterItemTable.character_id eq characterId) and  (characterItemTable.item_id eq this@GreenTea.id)}
                }
            }
        }
    }

    override fun create(quantity: Int): Item {
        return GreenTea(quantity)
    }
}
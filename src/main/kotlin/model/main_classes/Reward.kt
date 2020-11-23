package model.main_classes

import charactersRepo
import model.items.CharacterItemFiller
import model.items.Item
import model.items.characterItemTable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class Reward(
    val experiencePoints: Int,
    val coins: Int,
    val items: List<Item>?
){
    fun getReward(character: Character){
        character.experiencePoints+=experiencePoints
        character.checkExperience()
        character.coins+=coins
        items?.forEach { item ->
            val itemFromInventory = character.inventory.firstOrNull { it.name ==  item.name}
            if (itemFromInventory != null) {
                itemFromInventory.quantity+=item.quantity
                val t = transaction {
                    characterItemTable.selectAll().mapNotNull { characterItemTable.readResult(it) }
                }.firstOrNull { it.character_id == character.id && it.item_id == itemFromInventory.id}
                transaction {
                    characterItemTable.updateItem(t!!.id, CharacterItemFiller(t.item_id, t.character_id, itemFromInventory.quantity, t.id)) >0
                }
            }else transaction {
                characterItemTable.insertAndGetIdItem(CharacterItemFiller(item.id,character.id, item.quantity)).value
                true
            }
        }
        charactersRepo.update(character.id, character)
    }
    fun getGreedyReward(character: Character){
        character.experiencePoints+=(experiencePoints*1.5).toInt()
        character.checkExperience()
        character.coins+= (coins*1.5).toInt()
        items?.forEach { item ->
            val itemFromInventory = character.inventory.firstOrNull { it.name ==  item.name}
            if (itemFromInventory != null) {
                itemFromInventory.quantity+=item.quantity
                val t = transaction {
                    characterItemTable.selectAll().mapNotNull { characterItemTable.readResult(it) }
                }.firstOrNull { it.character_id == character.id && it.item_id == itemFromInventory.id}
                transaction {
                    characterItemTable.updateItem(t!!.id, CharacterItemFiller(t.item_id, t.character_id, itemFromInventory.quantity, t.id)) >0
                }
            }else transaction {
                characterItemTable.insertAndGetIdItem(CharacterItemFiller(item.id,character.id, item.quantity)).value
                true
            }
        }
        charactersRepo.update(character.id, character)
    }
}
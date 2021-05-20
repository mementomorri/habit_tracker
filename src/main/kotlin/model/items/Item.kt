package model.items

import kotlinx.serialization.Serializable
import model.main_classes.characterTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import repo.DefaultIdTable

@Serializable
open class Item(
        open var quantity: Int,
        val name: String,
        val description: String,
        val price: Int,
        open val id: Int= -1
){

    open fun create(quantity: Int): Item?=Item(quantity, name, description, price)

    override fun toString(): String {
        return "Item with: id=${id}, name=${name}, quantity=${quantity}"
    }
}

fun Item.useCoffee(characterId: Int){
    Coffee(this.quantity).useItem(characterId)
}

fun Item.useDishDisasterScroll(characterId: Int){
    DishDisasterScroll(this.quantity).useItem(characterId)
}

fun Item.useDustRabbitsScroll(characterId: Int){
    DustRabbitsScroll(this.quantity).useItem(characterId)
}

fun Item.useGreenTea(characterId: Int){
    GreenTea(this.quantity).useItem(characterId)
}

fun Item.useHealingPotion(characterId: Int){
    HealingPotion(this.quantity).useItem(characterId)
}

fun Item.useMagicInBottleScroll(characterId: Int){
    MagicInBottleScroll(this.quantity).useItem(characterId)
}

class CharacterItemFiller(
        val item_id: Int,
        val character_id: Int,
        var quantity: Int= 1,
        val id: Int= -1
){
    override fun toString(): String {
        return "id=$id, item_id=$item_id, character_id=$character_id, quantity=$quantity"
    }
}

class ItemsTable: DefaultIdTable<Item>(){
    var quantity= integer("quantity")
    val name= varchar("name", 50)
    val description= varchar("description", 510)
    val price= integer("price")
    override fun fill(builder: UpdateBuilder<Int>, item: Item) {
        builder[quantity]= item.quantity
        builder[name]= item.name
        builder[description]= item.description
        builder[price]= item.price
    }

    override fun readResult(result: ResultRow): Item?=
            Item(
                    result[quantity],
                    result[name],
                    result[description],
                    result[price],
                    result[id].value
            )
}

val itemTable= ItemsTable()

class CharacterItemTable: DefaultIdTable<CharacterItemFiller>(){
    val item_id= reference("item_id", itemTable)
    val character_id= reference("character_id", characterTable)
    var quantity= integer("quantity")

    override fun fill(builder: UpdateBuilder<Int>, item: CharacterItemFiller) {
        builder[item_id]= item.item_id
        builder[character_id]= item.character_id
        builder[quantity]= item.quantity
    }

    override fun readResult(result: ResultRow)=
            CharacterItemFiller(
                    result[item_id].value,
                    result[character_id].value,
                    result[quantity],
                    result[id].value
            )
}

val characterItemTable= CharacterItemTable()
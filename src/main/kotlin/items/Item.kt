package items

import model.Character

interface Item{
    val name: String
    var quantity: Int
    val description: String
    val price: Int

    fun useItem(character: Character):Boolean // true if item is successfully used, false otherwise
    fun create(quantity: Int):Item
}
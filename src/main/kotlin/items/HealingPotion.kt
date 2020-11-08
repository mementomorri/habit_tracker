package items

import model.Character

class HealingPotion (
        override var quantity: Int
): Item{
    override val description: String= "This little warm potion restores little amount of your life energy, tastes like cough syrup"
    override val name: String= "Healing potion"
    override val price: Int= 12

    override fun useItem(character: Character): Boolean {
        return if (quantity>= 1) {
            character.healthPoints.plus(15)
            if (character.healthPoints> character.maximumHP) character.healthPoints= character.maximumHP
            quantity--
            if (quantity<=0) character.personalRewards.removeIf { it.name =="Healing potion" }
            true
        } else false
    }

    override fun create(quantity: Int):Item {
        return HealingPotion(quantity)
    }
}
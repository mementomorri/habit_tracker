package items

import model.Character

class Coffee (
        override var quantity: Int
): Item{
    override val name: String= "Coffee"
    override val description: String= "Nice and warm cup of coffee to raise an adventurer spirit"
    override val price: Int= 20

    override fun useItem(character: Character): Boolean {
        return if (quantity>= 1) {
            character.energyPoints.plus(30)
            if (character.energyPoints> 100) character.energyPoints= 100
            quantity--
            if (quantity<=0) character.personalRewards.removeIf { it.name =="Coffee" }
            true
        } else false
    }

    override fun create(quantity: Int):Item {
        return Coffee(quantity)
    }
}
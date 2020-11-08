package items

import model.Character

class GreenTea (
        override var quantity: Int
):Item{
    override val name: String= "Green tea"
    override val description: String= "A cup of tasty green tea, after a few sips it restores some of your energy"
    override val price: Int= 9

    override fun useItem(character: Character): Boolean {
        return if (quantity>= 1) {
            character.energyPoints.plus(15)
            if (character.energyPoints> 100) character.energyPoints= 100
            quantity--
            if (quantity<=0) character.personalRewards.removeIf { it.name =="Green tea" }
            true
        } else false
    }

    override fun create(quantity: Int):Item {
        return GreenTea(quantity)
    }
}
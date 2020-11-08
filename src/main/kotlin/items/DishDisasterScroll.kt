package items

import model.Character
import model.TaskDifficulty
import quests.DishDisaster
import quests.MagicInBottle

class DishDisasterScroll (
        override var quantity: Int
): Item{
    override val description: String= "Some old scroll describing the story of Dish disaster"
    override val name: String= "Dish disaster scroll"
    override val price: Int= 7

    override fun useItem(character: Character): Boolean {
        return if (quantity>=1){
            character.quests.add(DishDisaster(TaskDifficulty.Veru_Hard))
            quantity--
            if (quantity<=0) character.personalRewards.removeIf { it.name =="Dish disaster scroll" }
            true
        }else false
    }

    override fun create(quantity: Int):Item {
        return DishDisasterScroll(quantity)
    }
}
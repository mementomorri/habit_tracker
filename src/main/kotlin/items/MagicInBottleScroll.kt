package items

import model.Character
import model.TaskDifficulty
import quests.MagicInBottle

class MagicInBottleScroll(
        override var quantity: Int
):Item{
    override val description: String= "Some old scroll describing the story of Magic in bottle"
    override val name: String= "Magic in bottle scroll"
    override val price: Int= 5

    override fun useItem(character: Character): Boolean {
        return if (quantity>=1){
            character.quests.add(MagicInBottle(TaskDifficulty.Medium))
            quantity--
            if (quantity<=0) character.personalRewards.removeIf { it.name =="Magic in bottle scroll" }
            true
        }else false
    }

    override fun create(quantity: Int):Item {
        return MagicInBottleScroll(quantity)
    }
}
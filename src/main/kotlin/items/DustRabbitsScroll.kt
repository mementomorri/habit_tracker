package items

import model.Character
import model.TaskDifficulty
import quests.DustRabbits

class DustRabbitsScroll (
        override var quantity: Int
):Item{
    override val description: String= "Some old scroll describing the story of Dust rabbits"
    override val name: String= "Dust rabbits scroll"
    override val price: Int= 6

    override fun useItem(character: Character): Boolean {
        return if (quantity>=1){
            character.quests.add(DustRabbits(TaskDifficulty.Hard))
            quantity--
            if (quantity<=0) character.personalRewards.removeIf { it.name =="Dust rabbits scroll" }
            true
        }else false
    }

    override fun create(quantity: Int):Item {
        return DustRabbitsScroll(quantity)
    }
}
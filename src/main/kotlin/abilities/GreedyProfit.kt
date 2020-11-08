package abilities

import model.Buff
import model.CharacterClass
import model.Character

class GreedyProfit ():Ability{
    override val name: String= "Greedy profit"
    override val characterClass: CharacterClass= CharacterClass.Archer
    override val description: String= "Archer tries shoot every target possible as quickly as they're swiftness let them"
    override val energyRequired: Int= 40
    override val levelRequired: Int= 6

    override fun useAbility(character: Character): Boolean {
        return if (character.energyPoints>=energyRequired){
            character.buffs.add(Buff("Greedy",))
            character.energyPoints.minus(40)
            true
        } else false
    }
}

val greedyProfit= GreedyProfit()
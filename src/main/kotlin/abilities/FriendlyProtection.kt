package abilities

import model.Buff
import model.CharacterClass
import model.Character

class FriendlyProtection ():Ability{
    override val name: String= "Friendly protection"
    override val characterClass: CharacterClass= CharacterClass.Warrior
    override val description: String= "Warrior stands up for they're nearest friend and protects them from suffer of fail or protects themself with a shield"
    override val energyRequired: Int= 40
    override val levelRequired: Int= 6

    override fun useAbility(character: Character): Boolean {
        return if (character.energyPoints>= energyRequired){
            character.buffs.add(Buff("Shield protection"))
            character.energyPoints.minus(energyRequired)
            true
        } else false
    }

    fun protectOtherPerson(caster: Character, personToProtect: Character):Boolean{
        return if (caster.energyPoints>= energyRequired){
            personToProtect.buffs.add(Buff("Friendly protection"))
            caster.energyPoints.minus(energyRequired)
            true
        } else false
    }
}

val friendlyProtection= FriendlyProtection()
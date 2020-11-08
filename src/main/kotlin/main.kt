import abilities.Ability
import items.Item
import model.Character
import repo.AbilityListRepo
import repo.ItemListRepo
import repo.CharacterListRepo

val charaterRepo= CharacterListRepo<Character>()
val abilityRepo= AbilityListRepo<Ability>()
val shop= ItemListRepo<Item>()
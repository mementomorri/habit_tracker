package repo

import abilities.Ability
import items.Item
import model.Character

class CharacterListRepo<T: Character> : Repo<T> {
    private val shadow = ArrayList<T>()

    override fun add(element: T) =
        shadow.add(element)

    override operator fun get(name: String) =
        shadow.find { it.name == name }

    override fun all(): List<T> = shadow
}

class AbilityListRepo<T: Ability> : Repo<T> {
    private val shadow = ArrayList<T>()

    override fun add(element: T) =
            shadow.add(element)

    override operator fun get(name: String) =
            shadow.find { it.name == name }

    override fun all(): List<T> = shadow
}

class ItemListRepo<T:Item> : Repo<T> {
    private val shadow = ArrayList<T>()

    override fun add(element: T) =
            shadow.add(element)

    override operator fun get(name: String) =
            shadow.find { it.name == name }

    override fun all(): List<T> = shadow
}
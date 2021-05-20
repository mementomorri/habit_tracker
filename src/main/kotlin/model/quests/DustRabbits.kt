package model.quests

class DustRabbits (
        override val characterId: Int,
        override var difficulty: String="VERYHARD"
): Quest(
        "Dust rabbits",
        "Word on the streets: there is dust rabbits at the desert near our town, they're eating dust, reproduce themself and spread dust around " +
        "to populate the area, your mission is to clean up every corner at your home, that should make them stop and leave our great town of productive folks.",
        characterId,
        difficulty
) {

}
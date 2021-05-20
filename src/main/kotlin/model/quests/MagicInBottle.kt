package model.quests

class MagicInBottle (
        override var difficulty: String,
        override val characterId: Int
): Quest(
        "Magic in a bottle",
        "You've been at the beach, looking at the waves taking a walk before you're sleep, everything's as usual, but you accidentally saw bottle drifting at the beach. " +
        "Once you have opened that bottle you found out that's there is a message in it, that message says:'You need to stretch every two hours when sit at a table for a week'," +
        " once you read it magic sparks came out and you felt that there is a spell on you. This spell reminds you to always stretch every two hours and to get rid of this spell" +
        " you need to just keep that habit for a week.",
        characterId,
        difficulty
) {

}
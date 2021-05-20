package model.quests

import model.main_classes.Task

open class Quest(
        override var name: String,
        override var description: String,
        override val characterId: Int,
        override var difficulty: String = "MEDIUM"
): Task(name,description,difficulty,"QUEST",characterId,null,null,null){

}
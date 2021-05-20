package model.main_classes

import java.time.LocalDate

class Habit(
        override var name: String,
        override var description: String,
        override var difficulty: String= "MEDIUM",
        override val characterId: Int,
        override var completionCount: Int?= 0
): Task(name,description,difficulty,"HABIT",characterId, null, LocalDate.now(), completionCount) {

}
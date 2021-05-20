package model.main_classes

import java.time.LocalDate

class Daily (
    override var name: String,
    override var description: String,
    override var difficulty: String,
    override val characterId: Int,
    override var completionCount: Int?= 0
): Task(name,description,difficulty,"DAILY",characterId, LocalDate.now().plusDays(1), LocalDate.now(), completionCount) {

}
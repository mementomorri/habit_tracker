package model.main_classes

import java.time.LocalDate

class ToDo (
    override var name: String,
    override var description: String,
    override var difficulty: String,
    override val characterId: Int,
    override var deadline: LocalDate? = LocalDate.now().plusWeeks(1)
//    val subTasks: List<String>?= null
): Task(name,description,difficulty,"TODO",characterId, deadline, null, null) {

}
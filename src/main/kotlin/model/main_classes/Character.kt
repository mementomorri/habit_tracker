package model.main_classes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import model.abilities.Ability
import model.abilities.CharacterAbilityFiller
import model.abilities.characterAbilityTable
import model.items.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import repo.DefaultIdTable
import java.time.LocalDate

@Serializable
class Character (
        val name: String,
        val characterClass: String,
        var id: Int=-1,
        @SerialName("maximumHP")
        var maximumHP_:Int? =null,
        @SerialName("healthPoints")
        var healthPoints_: Int? = null,
        var energyPoints: Int = 100,
        var experiencePoints: Int = 0,
        var coins: Int = 0,
        var level: Int = 1
) {
    @kotlinx.serialization.Transient
    @SerialName("maximumHP_public")
    var maximumHP = maximumHP_ ?: when (CharacterClass.valueOf(characterClass.toUpperCase())) {
        CharacterClass.MAGICIAN -> 30
        CharacterClass.ARCHER -> 40
        CharacterClass.WARRIOR -> 50
    }

    @kotlinx.serialization.Transient
    @SerialName("healthPoints_public")
    var healthPoints = healthPoints_ ?: maximumHP

    val abilities: List<Ability>
        get() = getAbilitiesList()

    private fun getAbilitiesList(): List<Ability> {
        //reads abilities table and returns abilities this character learned
        val abilitiesIdList = transaction {
            characterAbilityTable.selectAll().mapNotNull { characterAbilityTable.readResult(it) }
        }.filter { it.character_id == this.id }
        val result = mutableListOf<Ability>()
        abilitiesIdList.forEach {
            result.add(abilitiesRepo.read(it.ability_id)!!)
        }
        return result.toList()
    }

    val buffs: List<Buff>
        get() = getBuffList()

    private fun getBuffList(): List<Buff> {
        //reads buffs table and returns buffs this character affected by
        return transaction {
            buffTable.selectAll().mapNotNull { buffTable.readResult(it) }
        }.filter { it.character_id == this.id }
    }

    val habits: List<Task>
        get() = getHabitsList()

    private fun getHabitsList(): List<Task> {
        //reads tasks table and returns habits this character owns
        val result = mutableListOf<Task>()
        transaction {
            taskTable.selectAll().mapNotNull { taskTable.readResult(it) }
        }.forEach { task ->
            if (task.type == "HABIT"
                    && task.characterId == this.id) result
                    .add(Task(task.name, task.description, task.difficulty, "HABIT",task.characterId, startDate = task.startDate, completionCount = task.completionCount,))
        }
        return result.toList()
    }

    val dailies: List<Task>
        get() = getDailiesList()

    private fun getDailiesList(): List<Task> {
        //reads tasks table and returns dailies this character owns
        val result = mutableListOf<Task>()
        transaction {
            taskTable.selectAll().mapNotNull { taskTable.readResult(it) }
        }.forEach { task ->
            if (task.type == "DAILY"
                    && task.characterId == this.id) result
                    .add(Task(task.name, task.description, task.difficulty,"DAILY", task.characterId, task.deadline, task.startDate, task.completionCount))
        }
        return result.toList()
    }

    val toDos: List<Task>
        get() = getToDoList()

    private fun getToDoList(): List<Task> {
        //reads tasks table and returns todos this character owns
        val result = mutableListOf<Task>()
        transaction {
            taskTable.selectAll().mapNotNull { taskTable.readResult(it) }
        }.forEach { task ->
            if (task.type == "TODO"
                    && task.characterId == this.id) result
                    .add(Task(task.name, task.description, task.difficulty, "TODO", task.characterId, task.deadline, task.startDate))
        }
        return result.toList()
    }

    val quests: List<Task>
        get() = getQuestList()

    private fun getQuestList(): List<Task> {
        //reads tasks table and returns quests this character owns
        val result = mutableListOf<Task>()
        transaction {
            taskTable.selectAll().mapNotNull { taskTable.readResult(it) }
        }.forEach { task ->
            if (task.type == "QUEST"
                    && task.characterId == this.id) result
                    .add(Task(task.name, task.description, task.difficulty,"QUEST", task.characterId, task.deadline))
        }
        return result.toList()
    }

    val inventory: List<Item>
        get() = getInventoryList()

    private fun getInventoryList(): List<Item> {
        //reads characterItemTable and returns list of items this character owns
        val result = mutableListOf<Item>()
        transaction {
            characterItemTable.selectAll().mapNotNull { characterItemTable.readResult(it) }
        }.filter { it.character_id == this.id }.forEach { t ->
            val item = shopRepo.read(t.item_id)!!
            result.add(Item(t.quantity, item.name, item.description, item.price, item.id))
        }
        return result.toList()
    }

    private val levelMap: List<Int>
        get() = calculateLevelMap()

    fun addTask(task: Task): Boolean {
        //adds task to task table, returns true if added successfully, false otherwise
        //shouldn't let users create any duplicates
        val duplicateCheck = transaction {
            taskTable.selectAll().mapNotNull { taskTable.readResult(it) }
        }.firstOrNull {
            it.characterId == task.characterId
                    && TaskType.valueOf(it.type.toUpperCase()) == TaskType.valueOf(task.type.toUpperCase())
                    && it.name == task.name
        }
        if (duplicateCheck == null) {
            transaction {
                taskTable.insert {
                    fill(it, Task(task.name, task.description, task.difficulty, task.type, task.characterId, task.deadline, task.startDate, task.completionCount))
                }
                true
            }
        }
        val resultCheck= transaction {
            taskTable.selectAll().mapNotNull { taskTable.readResult(it) }
        }.firstOrNull {
            it.characterId == task.characterId
                    && TaskType.valueOf(it.type.toUpperCase()) == TaskType.valueOf(task.type.toUpperCase())
                    && it.name == task.name
        }
        return resultCheck != null
    }

    fun removeTask(taskName: String, taskType: TaskType): Boolean {
        //removes task depends on its type, returns true if removed successfully, false otherwise
        val existanceCheck = transaction {
            taskTable.selectAll().mapNotNull { taskTable.readResult(it) }
        }.firstOrNull {
            it.characterId == this.id
                    && TaskType.valueOf(it.type.toUpperCase()) == taskType
                    && it.name == taskName
        }
        if (existanceCheck != null) {
            transaction {
                taskTable.deleteWhere { taskTable.characterId eq this@Character.id and (taskTable.name eq taskName) and (taskTable.type eq taskType.toString()) } > 0
            }
        }
        val checkResults= transaction {
            taskTable.selectAll().mapNotNull { taskTable.readResult(it) }
        }.firstOrNull {
            it.characterId == this.id
                    && TaskType.valueOf(it.type.toUpperCase()) == taskType
                    && it.name == taskName
        }
        return checkResults == null
    }

    fun completeTask(taskName: String, taskType: TaskType) {
        //completes task depends on its type, raise completion count of habit, removes quest on complete and gives reward
        if (buffs.isNotEmpty()) transaction {
            buffTable.deleteWhere { buffTable.duration less LocalDate.now() }
        }
        val isGreedy = this.buffs.firstOrNull { it.name == "Greedy" }
        val task = transaction {
            taskTable.selectAll().mapNotNull { taskTable.readResult(it) }
        }.firstOrNull {
            it.characterId == this.id
                    && it.name == taskName
                    && TaskType.valueOf(it.type) == taskType
        }
        if (task != null) {
            when (taskType) {
                TaskType.HABIT -> if (this.habits.firstOrNull { it.name == taskName } != null) {
                    try {
                        val updateHabit = this.habits.find { it.name == taskName }
                        val updateCount = task.completionCount?.plus(1)
                        task.completionCount?.plus(1)
                        transaction {
                            taskTable.update({
                                (taskTable.characterId eq this@Character.id) and (taskTable.name eq task.name)
                            }) {
                                fill(it, Task(task.name, task.description, task.difficulty, task.type, task.characterId,task.deadline, task.startDate, updateCount))
                            } > 0
                        }
                        if (isGreedy == null) {
                            Reward(2* taskDifficultyToInt(updateHabit!!), 2* taskDifficultyToInt(updateHabit), null).getReward(this)
                        } else {
                            Reward(2* taskDifficultyToInt(updateHabit!!), 2* taskDifficultyToInt(updateHabit), null).getGreedyReward(this)
                        }
                    } catch (e: Exception) {
                        throw Exception("can't get Habit with taskName:$taskName while trying to complete it", e)
                    }
                }
                TaskType.DAILY -> if (this.dailies.firstOrNull { it.name == taskName } != null) {
                    try {
                        val updateDaily = this.dailies.find { it.name == taskName }
                        if (updateDaily!!.checkDeadline()) {
                            task.completionCount = task.completionCount?.plus(1)
                            if (isGreedy == null) {
                                Reward(2* taskDifficultyToInt(updateDaily), 2* taskDifficultyToInt(updateDaily), null).getReward(this)
                            } else {
                                Reward(2* taskDifficultyToInt(updateDaily), 2* taskDifficultyToInt(updateDaily), null).getGreedyReward(this)
                            }
                            task.deadline?.plusDays(1)
                            transaction {
                                taskTable.update({
                                    (taskTable.characterId eq this@Character.id) and (taskTable.name eq taskName)
                                }) {
                                    fill(it, task)
                                } > 0
                            }
                        } else {
                            if (this.buffs.firstOrNull {
                                        it.name == "Friendly protection"
                                                || it.name == "Shield protection"
                                    } == null) {
                                this.healthPoints.minus(taskDifficultyToInt(updateDaily) * 3)
                                if (taskDifficultyToInt(updateDaily) == 4
                                        || taskDifficultyToInt(updateDaily) == 5) this.experiencePoints.minus(taskDifficultyToInt(updateDaily))
                            } else return
                        }
                    } catch (e: Exception) {
                        throw Exception("can't get Daily with taskName:$taskName while trying to complete it", e)
                    }
                }
                TaskType.TODO -> if (this.toDos.firstOrNull { it.name == taskName } != null) {
                    try {
                        val updateToDo = this.toDos.find { it.name == taskName }
                        if (updateToDo!!.checkDeadline()) {
                            if (isGreedy == null) {
                                Reward((2.5* taskDifficultyToInt(updateToDo)).toInt(), (2.5* taskDifficultyToInt(updateToDo)).toInt(), null).getReward(this)
                            } else {
                                Reward((2.5* taskDifficultyToInt(updateToDo)).toInt(), (2.5* taskDifficultyToInt(updateToDo)).toInt(), null).getGreedyReward(this)
                            }
                            removeTask(updateToDo.name, TaskType.TODO)
                        } else {
                            if (this.buffs.firstOrNull {
                                        it.name == "Friendly protection"
                                                || it.name == "Shield protection"
                                    } == null) {
                                this.healthPoints.minus((taskDifficultyToInt(updateToDo) * 3.5).toInt())
                                if (taskDifficultyToInt(updateToDo)  == 4
                                        || taskDifficultyToInt(updateToDo)  == 5) this.experiencePoints.minus(taskDifficultyToInt(updateToDo))
                                removeTask(taskName, TaskType.TODO)
                            } else return
                        }
                    } catch (e: Exception) {
                        throw Exception("can't get ToDo with taskName:$taskName while trying to complete it", e)
                    }
                }
                TaskType.QUEST -> if (this.quests.firstOrNull { it.name == taskName } != null) {
                    try {
                        val updateQuest = this.quests.find { it.name == taskName }
                        val reward = when(TaskDifficulty.valueOf(updateQuest?.difficulty!!)){
                            TaskDifficulty.MEDIUM -> listOf<Item>(HealingPotion(2))
                            TaskDifficulty.HARD -> listOf(HealingPotion(2), GreenTea(2))
                            TaskDifficulty.VERYHARD -> listOf(HealingPotion(2), GreenTea(2), Coffee(2))
                            else -> listOf(GreenTea(1))
                        }
                        if (isGreedy == null) {
                            Reward(3* taskDifficultyToInt(updateQuest), 3* taskDifficultyToInt(updateQuest), reward).getReward(this)
                        } else {
                            Reward(3* taskDifficultyToInt(updateQuest), 3* taskDifficultyToInt(updateQuest), reward).getGreedyReward(this)
                        }
                        removeTask(updateQuest.name, TaskType.QUEST)
                    } catch (e: Exception) {
                        throw Exception("can't get Quest with taskName:$taskName while trying to complete it", e)
                    }
                }
            }
        }
        checkExperience()
        charactersRepo.update(this.id, this)
    }

    fun learnAbility(abilityId: Int): Boolean {
        //reads abilities repository, checks whether character can learn ability or not and makes changes respectively
        //return true if ability learned successfully, false otherwise
        val ability = abilitiesRepo.read(abilityId)
        return if (ability == null) {
            false
        } else {
            if (this.abilities.contains(ability)) {
                true
            } else {
                if (this.characterClass == ability.characterClass
                        && this.level >= ability.levelRequired) transaction {
                    characterAbilityTable.insertAndGetIdItem(CharacterAbilityFiller(abilityId, this@Character.id)).value
                    true
                }
                true
            }
        }
    }

    private fun calculateLevelMap(): List<Int> {
        //calculates threshold of each level sequentially and returns it as a list of integers
        val n = 4181
        var t1 = 8
        var t2 = 13
        val result = mutableListOf<Int>()

        while (t1 <= n) {
            result.add(t1)
            val sum = t1 + t2
            t1 = t2
            t2 = sum
        }
        return result.toList()
    }

    fun checkExperience() {
        //checks whether character has enough experience points, levels character up respectively
        val i = levelMap.indexOf(levelMap.firstOrNull { it <= this.experiencePoints })
        if (this.level < (i + 2)) {
            when (CharacterClass.valueOf(this.characterClass.toUpperCase())) {
                CharacterClass.MAGICIAN -> this.maximumHP += ((i + 2) - this.level) * 3
                CharacterClass.ARCHER -> this.maximumHP += ((i + 2) - this.level) * 4
                CharacterClass.WARRIOR -> this.maximumHP += ((i + 2) - this.level) * 5
            }
            this.level = i + 2
            this.energyPoints = 100
        } else return
    }

    fun buyItem(itemId: Int, quantity: Int) {
        //reads shop repository, checks whether character has enough coins and makes changes respectively
        val itemToBuy = shopRepo.read().firstOrNull { it.id == itemId }
        if (itemToBuy != null && itemToBuy.quantity >= quantity) {
            if (this.coins >= (itemToBuy.price * quantity)) {
                val item = this.inventory.find { it.name == itemToBuy.name }
                if (item != null) {
                    item.quantity += quantity
                    val itemToUptdate = transaction {
                        characterItemTable.selectAll().mapNotNull { characterItemTable.readResult(it) }
                    }.find { it.item_id == item.id && it.character_id == this.id }
                    transaction {
                        characterItemTable.updateItem(itemToUptdate!!.id, CharacterItemFiller(itemToUptdate.item_id, itemToUptdate.character_id, item.quantity, itemToUptdate.id))
                    }
                } else transaction {
                    characterItemTable.insertAndGetIdItem(CharacterItemFiller(itemToBuy.id, this@Character.id, quantity))
                    true
                }
            }
        } else return
    }

    fun addItemToInventory(item: Item): Boolean {
        //adds item possession to characterItemTable, returns true if added successfully, false otherwise
        val itemFromInventory = this.inventory.firstOrNull { it.name == item.name }
        return if (itemFromInventory != null) {
            itemFromInventory.quantity += item.quantity
            val t = transaction {
                characterItemTable.selectAll().mapNotNull { characterItemTable.readResult(it) }
            }.firstOrNull { it.character_id == this.id && it.item_id == itemFromInventory.id }
            transaction {
                characterItemTable.updateItem(t!!.id, CharacterItemFiller(t.item_id, t.character_id, itemFromInventory.quantity, t.id)) > 0
            }
            true
        } else {
            val neededId= transaction {
                itemTable.selectAll().mapNotNull { itemTable.readResult(it) }
            }.firstOrNull{ it.name == item.name }
            transaction {
                if (neededId != null) {
                    characterItemTable.insertAndGetIdItem(CharacterItemFiller(neededId.id, this@Character.id, item.quantity)).value
                }
                true
            }
        }
    }

    fun removeItemFromInventory(itemId: Int): Boolean {
        //removes item possession from characterItemTable, returns true if removed successfully, false otherwise
        val itemFromInventory = this.inventory.firstOrNull { it.id == itemId }
        return if (itemFromInventory != null) {
            transaction {
                characterItemTable.deleteWhere { (characterItemTable.character_id eq itemId) and (characterItemTable.item_id eq this@Character.id) } > 0
            }
        } else true
    }

    override fun toString(): String {
        return "Character whit: name-$name, class-$characterClass, id-$id"
    }
}

private fun taskDifficultyToInt(task: Task): Int{
    return when(TaskDifficulty.valueOf(task.difficulty)){
            TaskDifficulty.VERYEASY -> 1
            TaskDifficulty.EASY -> 2
            TaskDifficulty.MEDIUM -> 3
            TaskDifficulty.HARD -> 4
            TaskDifficulty.VERYHARD -> 5
        }
}

class CharacterTable: DefaultIdTable<Character>(){
    val name = varchar("name", 50)
    val characterClass= varchar("characterClass", 50)
    var level= integer("leve")
    var maximumHP= integer("maximumHP")
    var healthPoints= integer("healthPoints")
    var energyPoints= integer("energyPoints")
    var experiencePoints= integer("experiencePoints")
    var coins= integer("coins")

    override fun fill(builder: UpdateBuilder<Int>, item: Character) {
        builder[name] = item.name
        builder[characterClass] = item.characterClass
        builder[level] = item.level
        builder[maximumHP] = item.maximumHP
        builder[healthPoints] = item.healthPoints
        builder[energyPoints] = item.energyPoints
        builder[experiencePoints] = item.experiencePoints
        builder[coins] = item.coins
    }

    override fun readResult(result: ResultRow): Character? =
            Character(
                    result[name],
                    result[characterClass],
                    result[id].value,
                    result[maximumHP],
                    result[healthPoints],
                    result[energyPoints],
                    result[experiencePoints],
                    result[coins],
                    result[level]
            )
}

val characterTable= CharacterTable()
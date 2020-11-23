package model.main_classes

import abilitiesRepo
import charactersRepo
import model.abilities.Ability
import model.abilities.CharacterAbilityFiller
import model.abilities.characterAbilityTable
import model.items.CharacterItemFiller
import model.items.Item
import model.items.characterItemTable
import model.quests.Quest
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import repo.DefaultIdTable
import shopRepo
import java.time.LocalDate

class Character (
        override val  name: String,
        val characterClass: String,
        override val id: Int=-1,
        var maximumHP:Int = when (CharacterClass.valueOf(characterClass.toUpperCase())) {
            CharacterClass.MAGICIAN -> 30
            CharacterClass.ARCHER -> 40
            CharacterClass.WARRIOR -> 50
        },
        var healthPoints: Int = maximumHP,
        var energyPoints: Int = 100,
        var experiencePoints: Int = 0,
        var coins: Int = 0,
        var level: Int = 1
): User(name, id) {

    val abilities: List<Ability>
        get() = getAbilitiesList()
    private fun getAbilitiesList(): List<Ability> {
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
    private fun getBuffList(): List<Buff>{
        return transaction {
            buffTable.selectAll().mapNotNull { buffTable.readResult(it) }
        }.filter { it.character_id == this.id }
    }

    val habits: List<Habit>
        get() = getHabitsList()
    private fun getHabitsList():List<Habit>{
        val result= mutableListOf<Habit>()
        transaction {
            taskTable.selectAll().mapNotNull { taskTable.readResult(it) }
        }.forEach { task ->
            if (task.type == "HABIT"
                    && task.characterId == this.id) result
                    .add(Habit(task.name,task.description,task.difficulty,task.characterId, task.completionCount))
        }
        return result.toList()
    }

    val dailies: List<Daily>
        get() = getDailiesList()
    private fun getDailiesList():List<Daily>{
        val result= mutableListOf<Daily>()
        transaction {
            taskTable.selectAll().mapNotNull { taskTable.readResult(it) }
        }.forEach { task ->
            if (task.type == "DAILY"
                    && task.characterId == this.id) result
                    .add(Daily(task.name,task.description,task.difficulty,task.characterId, task.completionCount))
        }
        return result.toList()
    }

    val toDos: List<ToDo>
        get() = getToDoList()
    private fun getToDoList():List<ToDo>{
        val result= mutableListOf<ToDo>()
        transaction {
            taskTable.selectAll().mapNotNull { taskTable.readResult(it) }
        }.forEach { task ->
            if (task.type == "TODO"
                    && task.characterId == this.id) result
                    .add(ToDo(task.name,task.description,task.difficulty,task.characterId, task.deadline))
        }
        return result.toList()
    }

    val quests: List<Quest>
        get() = getQuestList()
    private fun getQuestList():List<Quest>{
        val result= mutableListOf<Quest>()
        transaction {
            taskTable.selectAll().mapNotNull { taskTable.readResult(it) }
        }.forEach { task ->
            if (task.type == "QUEST"
                    && task.characterId == this.id) result
                    .add(Quest(task.name,task.description,task.difficulty,task.characterId))
        }
        return result.toList()
    }

    val inventory: List<Item>
        get() = getInventoryList()
    private fun getInventoryList():List<Item> {
        val result = mutableListOf<Item>()
        transaction {
            characterItemTable.selectAll().mapNotNull { characterItemTable.readResult(it) }
        }.filter { it.character_id == this.id }.forEach { t ->
            val item = shopRepo.read(t.item_id)!!
            result.add(Item(t.quantity,item.name,item.description,item.price,item.id))
        }
        return result.toList()
    }

        private val levelMap: List<Int>
        get() = calculateLevelMap()

    fun addTask(task: Task) {
        val duplicateCheck= transaction {
            taskTable.selectAll().mapNotNull { taskTable.readResult(it) }
        }.firstOrNull{it.characterId == task.characterId
                && TaskType.valueOf(it.type.toUpperCase()) == TaskType.valueOf(task.type.toUpperCase())
                && it.name == task.name}
        if (duplicateCheck == null) {
            when (TaskType.valueOf(task.type.toUpperCase())) {
                TaskType.HABIT -> if (this.habits.firstOrNull { it.name == task.name } == null) {
                    transaction {
                        taskTable.insert{
                            fill(it, task)
                        }
                        true
                    }
                }
                TaskType.DAILY -> if (this.dailies.firstOrNull { it.name == task.name } == null) {
                    transaction {
                        taskTable.insert{
                            fill(it, task)
                        }
                        true
                    }
                }
                TaskType.TODO -> if (this.toDos.firstOrNull { it.name == task.name } == null) {
                    transaction {
                        taskTable.insert{
                            fill(it, task)
                        }
                        true
                    }
                }
                TaskType.QUEST -> if (this.quests.firstOrNull { it.name == task.name } == null) {
                    transaction {
                        taskTable.insert{
                            fill(it, task)
                        }
                        true
                    }
                }
            }
        }
    }

    fun removeTask(taskName: String, taskType: TaskType){
        val existanceCheck= transaction {
            taskTable.selectAll().mapNotNull { taskTable.readResult(it) }
        }.firstOrNull{it.characterId == this.id
                && TaskType.valueOf(it.type.toUpperCase()) == taskType
                && it.name == taskName}
        if (existanceCheck != null) {
            when (taskType) {
                TaskType.HABIT -> if (this.habits.firstOrNull { it.name == taskName } != null) {
                    transaction {
                        taskTable.deleteWhere { taskTable.characterId eq this@Character.id and (taskTable.name eq taskName) and (taskTable.type eq taskType.toString())} > 0
                    }
                }
                TaskType.DAILY -> if (this.dailies.firstOrNull { it.name == taskName } != null) {
                    transaction {
                        taskTable.deleteWhere { taskTable.characterId eq this@Character.id and (taskTable.name eq taskName) and (taskTable.type eq taskType.toString())} > 0
                    }
                }
                TaskType.TODO -> if (this.toDos.firstOrNull { it.name == taskName } != null) {
                    transaction {
                        taskTable.deleteWhere { taskTable.characterId eq this@Character.id and (taskTable.name eq taskName) and (taskTable.type eq taskType.toString())} > 0
                    }
                }
                TaskType.QUEST -> if (this.quests.firstOrNull { it.name == taskName } != null) {
                    transaction {
                        taskTable.deleteWhere { taskTable.characterId eq this@Character.id and (taskTable.name eq taskName) and (taskTable.type eq taskType.toString())} > 0
                    }
                }
            }
        }
    }

    fun completeTask(taskName: String, taskType: TaskType){
        if (buffs.isNotEmpty()) transaction {
            buffTable.deleteWhere { buffTable.duration less  LocalDate.now() }
        }
        val isGreedy = this.buffs.firstOrNull { it.name == "Greedy" }
        val task= transaction {
            taskTable.selectAll().mapNotNull { taskTable.readResult(it)}
        }.firstOrNull { it.characterId == this.id
                && it.name == taskName
                && TaskType.valueOf(it.type) == taskType }
        if (task != null) {
            when (taskType) {
                TaskType.HABIT -> if (this.habits.firstOrNull { it.name == taskName } != null) {
                    try {
                        val updateHabit = this.habits.find { it.name == taskName }
                        val updateCount = task.completionCount+1
                        task.completionCount++
                        transaction {
                            taskTable.update({
                                (taskTable.characterId eq this@Character.id) and ( taskTable.name eq task.name)
                            }){
                                fill(it, Task(task.name, task.description, task.difficulty, task.type, task.characterId, completionCount = updateCount))
                            } >0
                        }
                        if (isGreedy == null) {
                            updateHabit!!.rewards.getReward(this)
                        } else {
                            updateHabit!!.rewards.getGreedyReward(this)
                        }
                    } catch (e: Exception) {
                        throw Exception("can't get Habit with taskName:$taskName while trying to complete it", e)
                    }
                }
                TaskType.DAILY -> if (this.dailies.firstOrNull { it.name == taskName } != null) {
                    try {
                        val updateDaily = this.dailies.find { it.name == taskName }
                        if (updateDaily!!.checkDeadline()) {
                            task.completionCount+=1
                            if (isGreedy == null) {
                                updateDaily.rewards.getReward(this)
                            } else {
                                updateDaily.rewards.getGreedyReward(this)
                            }
                            task.deadline.plusDays(1)
                            transaction {
                                taskTable.update({
                                    (taskTable.characterId eq this@Character.id) and ( taskTable.name eq taskName)
                                }){
                                    fill(it, task)
                                } >0
                            }
                        } else {
                            if (this.buffs.firstOrNull {
                                        it.name == "Friendly protection"
                                                || it.name == "Shield protection"
                                    } == null) {
                                this.healthPoints.minus(updateDaily.difficultyToInt * 3)
                                if (updateDaily.difficultyToInt == 4
                                        || updateDaily.difficultyToInt == 5) this.experiencePoints.minus(updateDaily.difficultyToInt)
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
                                updateToDo.rewards.getReward(this)
                            } else {
                                updateToDo.rewards.getGreedyReward(this)
                            }
                            removeTask(updateToDo.name, TaskType.TODO)
                        } else {
                            if (this.buffs.firstOrNull {
                                        it.name == "Friendly protection"
                                                || it.name == "Shield protection"
                                    } == null) {
                                this.healthPoints.minus((updateToDo.difficultyToInt * 3.5).toInt())
                                if (updateToDo.difficultyToInt == 4
                                        || updateToDo.difficultyToInt == 5) this.experiencePoints.minus(updateToDo.difficultyToInt)
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
                        if (isGreedy == null) {
                            updateQuest!!.rewards.getReward(this)
                        } else {
                            updateQuest!!.rewards.getGreedyReward(this)
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

    fun learnAbility(abilityId:Int){
        val ability= abilitiesRepo.read(abilityId)
        if (ability==null){
            return
        } else{
            if (this.abilities.contains(ability)){
                return
            }else {
                if (this.characterClass == ability.characterClass
                        && this.level >= ability.levelRequired) transaction {
                    characterAbilityTable.insertAndGetIdItem(CharacterAbilityFiller(abilityId, this@Character.id)).value
                    true
                }
            }
        }
    }

    private fun calculateLevelMap(): List<Int>{
        val n = 4181
        var t1=8
        var t2=13
        val result = mutableListOf<Int>()

        while (t1<=n){
            result.add(t1)
            val sum = t1+t2
            t1= t2
            t2= sum
        }
        return result.toList()
    }

    fun checkExperience(){
        val i = levelMap.indexOf(levelMap.firstOrNull { it <= this.experiencePoints })
        if (i!= null){
            if (this.level< (i+2)){
                when (CharacterClass.valueOf(this.characterClass.toUpperCase())){
                    CharacterClass.MAGICIAN -> this.maximumHP+= ((i+2) - this.level)*3
                    CharacterClass.ARCHER -> this.maximumHP+= ((i+2) - this.level)*4
                    CharacterClass.WARRIOR -> this.maximumHP+= ((i+2) - this.level)*5
                }
                this.level= i+2
                this.energyPoints= 100
                } else return
        } else return
    }

    fun buyItem(itemId: Int, quantity: Int){
        val itemToBuy = shopRepo.read().firstOrNull{it.id == itemId}
        if (itemToBuy!=null && itemToBuy.quantity>= quantity) {
            if (this.coins >= (itemToBuy.price * quantity)){
                val item = this.inventory.find { it.name == itemToBuy.name }
                if (item!= null){
                    item.quantity+= quantity
                    val itemToUptdate= transaction {
                        characterItemTable.selectAll().mapNotNull { characterItemTable.readResult(it) }
                    }.find { it.item_id == item.id && it.character_id == this.id }
                    transaction {
                        characterItemTable.updateItem(itemToUptdate!!.id, CharacterItemFiller(itemToUptdate.item_id, itemToUptdate.character_id, item.quantity, itemToUptdate.id))
                    }
                } else transaction {
                    characterItemTable.insertAndGetIdItem(CharacterItemFiller(itemToBuy.id,this@Character.id,quantity))
                    true
                }
            }
        } else return
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
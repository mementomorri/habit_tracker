package model

import abilities.Ability
import abilityRepo
import items.Item
import quests.Quest
import shop
import java.time.LocalDate
import kotlin.Exception

class Character (
        name: String,
        val characterClass: CharacterClass
): User(name) {
    var level: Int = 1
    var abilities: ArrayList<Ability> = arrayListOf()
    var buffs: ArrayList<Buff> = arrayListOf()
    var maximumHP:Int = when (this.characterClass) {
        CharacterClass.Magician -> 30
        CharacterClass.Archer -> 40
        CharacterClass.Warrior -> 50
    }
    var healthPoints: Int = maximumHP
    var energyPoints: Int = 100
    var experiencePoints: Int = 0
    var coins: Int = 0
    val habits: ArrayList<Habit> = arrayListOf()
    val dailies: ArrayList<Daily> = arrayListOf()
    val toDos: ArrayList<ToDo> = arrayListOf()
    val quests: ArrayList<Quest> = arrayListOf()
    val personalRewards: ArrayList<Item> = arrayListOf()
    private val levelMap: List<Int>
        get() = calculateLevelMap()

    fun addTask(task: Task) {
        when (task) {
            is Habit -> if (this.habits.firstOrNull { it.name == task.name } == null) {
                    this.habits.add(task)
            }
            is Daily -> if (this.dailies.firstOrNull { it.name == task.name } == null) {
                    this.dailies.add(task)
            }
            is ToDo -> if (this.toDos.firstOrNull { it.name == task.name } == null) {
                    this.toDos.add(task)
            }
            is Quest -> if (this.quests.firstOrNull { it.name == task.name } == null) {
                    this.quests.add(task)
            }
        }
    }

    fun removeTask(taskName: String, taskType: TaskType){
        when (taskType) {
            TaskType.Habit -> if (this.habits.firstOrNull { it.name == taskName }!=null) {
                this.habits.removeIf { it.name == taskName  }
            }
            TaskType.Daily -> if (this.dailies.firstOrNull { it.name == taskName  }!=null) {
                this.dailies.removeIf { it.name == taskName  }
            }
            TaskType.ToDo -> if (this.toDos.firstOrNull { it.name == taskName  }!=null) {
                this.toDos.removeIf { it.name == taskName  }
            }
            TaskType.Quest -> if (this.quests.firstOrNull { it.name == taskName  }!=null) {
                this.quests.removeIf { it.name == taskName  }
            }
        }
    }

    fun completeTask(taskName: String, taskType: TaskType){
        if (buffs.isNotEmpty()) buffs.removeIf { it.duration<LocalDate.now() }
        val isGreedy = this.buffs.firstOrNull { it.name == "Greedy" }
        when(taskType){
            TaskType.Habit -> if (this.habits.firstOrNull { it.name == taskName } !=null) {
                try {
                    val updateHabit = this.habits.find { it.name == taskName }
                    updateHabit!!.completionCount+=1
                    if (isGreedy==null) {
                        updateHabit!!.rewards.getReward(this)
                    }else {
                        updateHabit!!.rewards.getGreedyReward(this)
                    }
                } catch (e:Exception){
                    throw Exception("can't get Habit with taskName:$taskName while trying to complete it",e)
                }
            }
            TaskType.Daily -> if (this.dailies.firstOrNull { it.name == taskName } !=null) {
                try {
                    val updateDaily = this.dailies.find { it.name == taskName }
                    if (updateDaily!!.checkDeadline()) {
                        updateDaily.completionCount+= 1
                        if (isGreedy==null) {
                            updateDaily.rewards.getReward(this)
                        }else {
                            updateDaily.rewards.getGreedyReward(this)
                        }
                        updateDaily.deadline.plusDays(1)
                    }else{
                        if (this.buffs.firstOrNull{it.name=="Friendly protection"
                                        ||it.name=="Shield protection"}==null) {
                            this.healthPoints.minus(updateDaily.difficultyToInt * 3)
                            if (updateDaily.difficultyToInt == 4
                                    || updateDaily.difficultyToInt == 5) this.experiencePoints.minus(updateDaily.difficultyToInt)
                        } else return
                    }
                } catch (e:Exception){
                    throw Exception("can't get Daily with taskName:$taskName while trying to complete it",e)
                }
            }
            TaskType.ToDo -> if (this.toDos.firstOrNull { it.name == taskName } !=null) {
                try {
                    val updateToDo = this.toDos.find { it.name == taskName }
                    if (updateToDo!!.checkDeadline()) {
                        if (isGreedy==null) {
                            updateToDo.rewards.getReward(this)
                        }else {
                            updateToDo.rewards.getGreedyReward(this)
                        }
                        removeTask(updateToDo.name, updateToDo.type)
                    } else{
                        if (this.buffs.firstOrNull{it.name=="Friendly protection"
                                        ||it.name=="Shield protection"}==null) {
                            this.healthPoints.minus((updateToDo.difficultyToInt * 3.5).toInt())
                            if (updateToDo.difficultyToInt == 4
                                    || updateToDo.difficultyToInt == 5) this.experiencePoints.minus(updateToDo.difficultyToInt)
                            removeTask(taskName, TaskType.ToDo)
                        } else return
                    }
                } catch (e:Exception){
                    throw Exception("can't get ToDo with taskName:$taskName while trying to complete it",e)
                }
            }
            TaskType.Quest -> if (this.quests.firstOrNull { it.name == taskName } !=null) {
                try {
                    val updateQuest = this.quests.find { it.name == taskName }
                    if (isGreedy==null) {
                        updateQuest!!.rewards.getReward(this)
                    }else {
                        updateQuest!!.rewards.getGreedyReward(this)
                    }
                    removeTask(updateQuest.name, updateQuest.type)
                } catch (e:Exception){
                    throw Exception("can't get Quest with taskName:$taskName while trying to complete it",e)
                }
            }
        }
    }

    fun learnAbility(abilityName:String):Boolean{ //true if ability been learn successfully, false otherwise
        val ability= abilityRepo[abilityName]
        return if (ability==null){
            false
        } else{
            if (this.characterClass==ability.characterClass
                    && this.level>=ability.levelRequired)this.abilities.add(ability)
            true
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
                when (this.characterClass){
                    CharacterClass.Magician -> this.maximumHP+= ((i+2) - this.level)*3
                    CharacterClass.Archer -> this.maximumHP+= ((i+2) - this.level)*4
                    CharacterClass.Warrior -> this.maximumHP+= ((i+2) - this.level)*5
                }
                this.level= i+2
                this.energyPoints= 100
                } else return
        } else return
    }

    fun buyItem(itemName: String, quantity: Int){
        val item = shop[itemName]
        if (item!=null && item.quantity>= quantity) {
            if (this.coins >= (item.price * quantity)){
                if (this.personalRewards.firstOrNull { it.name == item.name }!= null){
                    this.personalRewards.find { it.name == item.name }!!.quantity+= quantity
                } else this.personalRewards.add(item.create(quantity))
            }
        } else return
    }
}
package database_tests

import model.main_classes.abilitiesRepo
import model.main_classes.charactersRepo
import model.abilities.*
import model.challenges.challenges
import model.challenges.showingTheAttitude
import model.challenges.solidHabit
import model.items.*
import model.main_classes.*
import model.quests.Quest
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import model.main_classes.shopRepo
import model.main_classes.userCharacterTable
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

private fun setEvent(eventName: String, eventDescription: String, eventDifficulty: TaskDifficulty, deadline: LocalDate){
    //sets an event by adding new task to every character's list of tasks
    charactersRepo.read().forEach { character ->
        transaction {
            taskTable.insert {
                fill(it, ToDo(eventName, eventDescription, eventDifficulty.toString(), character.id, deadline))
            }
        }
    }
}

private fun addPersonalQuest(characterId: Int, quest: Quest){
    //add quest to someones quests list
    val character= charactersRepo.read(characterId)
    if (character == null){
        return
    } else{
        character.addTask(quest)
    }
}

fun initTest(){
    //test initial database configuration
    //it's a bit rough test, but it does what it supposed to

    assertEquals(3, charactersRepo.read().size)
    assertEquals(5, shopRepo.read().size)
    assertEquals(7, abilitiesRepo.read().size)
    assertEquals(9, transaction {
        taskTable.selectAll().mapNotNull { taskTable.readResult(it) }
    }.size)
}

fun addRemoveTaskTest(){
    //tests if 'addTask' and 'removeTask' works correctly,
    //there is one default task of each kind and we gonna add one more to each array, check if it's added,
    //remove it and check if it's removed correctly
    val alice= charactersRepo.read(1)?: fail()
    alice.addTask(Task("new habit","just a test habit","VERYEASY","HABIT",alice.id))
    alice.addTask(Task("new daily","just a test daily", "VERYEASY","DAILY",alice.id))
    alice.addTask(Task("new todo", "just a test todo", "VERYEASY", "TODO",alice.id))
    assertEquals(2, alice.habits.size)
    assertEquals(2, alice.dailies.size)
    assertEquals(2, alice.toDos.size)
    val addTaskCheck= alice.habits.firstOrNull{it.name == "new habit"}?: fail()
    println(alice.habits)
    assertEquals(0, addTaskCheck.completionCount)
    alice.removeTask("new habit", TaskType.HABIT)
    alice.removeTask("new daily", TaskType.DAILY)
    alice.removeTask("new todo", TaskType.TODO)
    assertEquals(1, alice.habits.size)
    assertEquals(1, alice.dailies.size)
    assertEquals(1, alice.toDos.size)
}

fun duplicateTaskCheck(){
    //tests if duplicate mechanism works correctly
    //addTask() shouldn't let users create duplicates
    val alice= charactersRepo.read(1)?: fail()
    assertEquals(1, alice.habits.size)
    alice.addTask(Habit("duplicatable task","just another task to duplicate", "MEDIUM", alice.id))
    alice.addTask(Habit("duplicatable task","just another task to duplicate", "MEDIUM", alice.id))
    assertEquals(2, alice.habits.size)
    alice.removeTask("duplicatable task",TaskType.HABIT)
    assertEquals(1, alice.habits.size)
}

fun completeTaskTest(){
    //tests if 'completeTask' function works correctly
    val alice= charactersRepo.read(1)?: fail()
    alice.completeTask("Brush the teeth", TaskType.HABIT)
    val habit = alice.habits.find { it.name == "Brush the teeth" }?: fail()
    //habit completion count went up by 1
    assertEquals(1, habit.completionCount)
    //Alice got 2 exp for a very easy habit completion
    assertEquals(2, alice.experiencePoints)
    alice.completeTask("Stretch at the morning", TaskType.DAILY)
    val daily= alice.dailies.find { it.name == "Stretch at the morning" }?: fail()
    //daily completion count went up by 1
    assertEquals(1, daily.completionCount)
    //Alice got 4 exp for an easy daily
    assertEquals(6, alice.experiencePoints)
    alice.completeTask("Prepare for test", TaskType.TODO)
    //once to do completed it's removed from character's array of todos, so there is only 1 left
    assertEquals(1, alice.toDos.size)
    //let's just complete habit a few times to level Alice up
    alice.completeTask("Brush the teeth", TaskType.HABIT)
    alice.completeTask("Brush the teeth", TaskType.HABIT)
    assertEquals(2, alice.level)
    //by now Alice should be level 2 and "Brush the teeth" habit completion counter should be equal to 3
    assertEquals(3, alice.habits.find { it.name == "Brush the teeth" }!!.completionCount)
}

fun buyItemTest(){
    //tests if 'buyItem' function works correctly
    val alice= charactersRepo.read(1)?: fail()
    alice.coins+=50
    //just buying one item of each kind
    shopRepo.read().forEach { alice.buyItem(it.id, 1) }
    assertEquals(5, alice.inventory.size)
    //Alice got at least one item of each kind from the shop
    assertEquals(1, alice.inventory.find { it.name == "Green tea" }?.quantity)
}

fun useQuestScrollTest(){
    //tests functionality of quest scrolls
    val alice= charactersRepo.read(1)?: fail()
    assertEquals(0, alice.quests.size)
    alice.coins+=10
    val dustRabbitsScroll = shopRepo.read().find { it.name == "Dust rabbits scroll" } ?: fail()
    alice.buyItem(dustRabbitsScroll.id,1)
    val questScroll = alice.inventory.find { it.name == "Dust rabbits scroll" }?: fail()
    questScroll.useDustRabbitsScroll(alice.id)
    assertEquals(1, alice.quests.size)
    alice.completeTask("Dust rabbits", TaskType.QUEST)
    assertEquals(0, alice.quests.size)
    assertEquals(3, alice.inventory.find { it.name == "Coffee" }?.quantity)
}

fun abilitiesTest(){
    //tests abilities of every class
    charactersRepo.read().forEach {
        charactersRepo.update(it.id, Character(it.name, it.characterClass, it.id, it.maximumHP, it.healthPoints, it.energyPoints, 4000, it.coins, 14))
    }
    charactersRepo.read().forEach { it.completeTask("Brush the teeth", TaskType.HABIT) }
    //make everyone's level high enough
    val alice= charactersRepo.read(1)?: fail()
    val bob= charactersRepo.read(2)?: fail()
    val charlie= charactersRepo.read(3)?: fail()
    charactersRepo.read().forEach {
        assertEquals(0, it.abilities.size)
    }
    alice.learnAbility(1)
    alice.learnAbility(2)
    alice.learnAbility(3)
    assertEquals(3, alice.abilities.size)
    //learn abilities and check if they're learned successfully
    bob.learnAbility(4)
    bob.learnAbility(7)
    assertEquals(2, bob.abilities.size)

    charlie.learnAbility(5)
    charlie.learnAbility(6)
    assertEquals(2, charlie.abilities.size)

    burstsOfFlame.useAbility(alice)
    assertEquals(4044, alice.experiencePoints)
    //use abilities and check results of ability usage
    swordplayPractice.useAbility(bob)
    assertEquals(4030, bob.experiencePoints)

    huntersFocus.useAbility(charlie)
    assertEquals(4037, charlie.experiencePoints)
    charlie.addTask(ToDo("second test todo","test if greedy profit works correctly", "VERYEASY",3))
    charlie.completeTask("second test todo",TaskType.TODO)
    assertEquals(4039, charlie.experiencePoints)
    greedyProfit.useAbility(charlie)
    charlie.completeTask("test todo", TaskType.TODO)
    assertEquals(4042, charlie.experiencePoints)
}

fun challengesTest(){
    //tests if challenges works correctly
    val alice= charactersRepo.read(1)?: fail()
    for (i in 0..90){
        alice.completeTask("Brush the teeth", TaskType.HABIT)
    }
    assertEquals(true, solidHabit.checkChallengeCondition(alice))
    println("Alice's amount of coins:")
    println(alice.coins)
//    solidHabit.getReward(alice)
    for (i in 0..5){
        alice.completeTask("Stretch at the morning", TaskType.DAILY)

    }
    assertEquals(true, showingTheAttitude.checkChallengeCondition(alice))
}

fun setEventTest(){
    //tests if admin can set an event
    setEvent("test event", "event just for test", TaskDifficulty.MEDIUM, LocalDate.now().plusWeeks(4))
    charactersRepo.read().forEach {
        assertEquals("test event", it.toDos.last().name)
    }
}

fun addPersonalQuestTest(){
    //tests if admin can sey personal quests
    val alice= charactersRepo.read(1)?: fail()

    class PersonalQuestForAlice():Quest(
            "Personal quest for Alice",
            "Test for personal quests",
            alice.id,
            "MEDIUM"
    )

    addPersonalQuest(alice.id, PersonalQuestForAlice())
    assertEquals(1, alice.quests.size)
    alice.completeTask("Personal quest for Alice", TaskType.QUEST)
    assertEquals(0, alice.quests.size)
}

class TestDB {
    @Test
    fun testAllUseCases() {
        //simulate all use cases from UML level and test them with database implementation
        Database.connect(
                "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
                driver = "org.h2.Driver"
        )
        transaction {
            SchemaUtils.create(userTable, userCharacterTable, taskTable, buffTable, characterItemTable, characterAbilityTable, characterTable, itemTable, abilityTable, challenges)
        }

        initDB()

        //tests start here
        initTest()
        addRemoveTaskTest()
        duplicateTaskCheck()
        completeTaskTest()
        buyItemTest()
        useQuestScrollTest()
        abilitiesTest()
        challengesTest()
        setEventTest()
        addPersonalQuestTest()

        transaction {
            SchemaUtils.drop(userCharacterTable, userTable, characterItemTable, taskTable, characterAbilityTable, buffTable, itemTable, abilityTable, challenges, characterTable)
        }
    }

    private fun initDB() {
        userTable.addUser(User("Alice", "4l1c3"))
        userTable.addUser(User("Bob", "b0bb1e"))
        userTable.addUser(User("Charlie", "ch4rl13"))

        charactersRepo.create(Character("Alice", "MAGICIAN", 1))
        charactersRepo.create(Character("Bob", "WARRIOR", 2))
        charactersRepo.create(Character("Charlie", "ARCHER", 3))

        userCharacterTable.addPossession(1, 1)
        userCharacterTable.addPossession(2, 2)
        userCharacterTable.addPossession(3, 3)

        shopRepo.create(HealingPotion(20))
        shopRepo.create(GreenTea(20))
        shopRepo.create(Coffee(20))
        shopRepo.create(DustRabbitsScroll(20))
        shopRepo.create(DishDisasterScroll(20))

        abilitiesRepo.create(burstsOfFlame)
        abilitiesRepo.create(cheeringWords)
        abilitiesRepo.create(frostChill)
        abilitiesRepo.create(friendlyProtection)
        abilitiesRepo.create(greedyProfit)
        abilitiesRepo.create(huntersFocus)
        abilitiesRepo.create(swordplayPractice)

        charactersRepo.read().forEach { character ->
            character.addTask(Habit("Brush the teeth", "Brush the teeth every morning", "VERYEASY", character.id))
            character.addTask(Daily("Stretch at the morning", "Stretch at least 5 minutes a day", "EASY", character.id))
            character.addTask(ToDo("test todo", "just a test todo", "VERYEASY", character.id))
        }

        transaction {
            challenges.insert {
                fill(it, solidHabit)
            }
        }
        transaction {
            challenges.insert {
                fill(it, showingTheAttitude)
            }
        }
        println("Database default state initialized")
        println("Start of use case tests")
    }
}
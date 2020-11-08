import abilities.*
import challenges.showingTheAttitude
import challenges.solidHabit
import items.DishDisasterScroll
import items.DustRabbitsScroll
import items.GreenTea
import items.HealingPotion
import model.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import quests.Quest

fun setEvent(event: ToDo){
    charaterRepo.all().forEach { it.toDos.add(event) }
}

fun addPersonalQuest(characterName: String, quest: Quest){
    if (charaterRepo[characterName] == null) {
        return
    }else{
        charaterRepo[characterName]!!.quests.add(quest)
    }
}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MainTest {

    @BeforeAll
    fun init(){
        charaterRepo.add(Character("Alice", CharacterClass.Magician))
        charaterRepo.add(Character("Bob", CharacterClass.Warrior))
        charaterRepo.add(Character("Charlie", CharacterClass.Archer))

        val testCharacters= listOf(
                charaterRepo["Alice"],
                charaterRepo["Bob"],
                charaterRepo["Charlie"]
        )

        listOf(
                HealingPotion(20),
                GreenTea(20),
                DustRabbitsScroll(10),
                DishDisasterScroll(10)
        ).forEach { shop.add(it) }

        listOf(
                burstsOfFlame,
                cheeringWords,
                frostChill,
                friendlyProtection,
                greedyProfit,
                huntersFocus,
                swordplayPractice
        ).forEach { abilityRepo.add(it) }


        testCharacters.forEach { it!!.addTask(Habit("Brush the teeth", "Brush the teeth every morning", TaskDifficulty.Very_easy))}
        testCharacters.forEach { it!!.addTask(Daily("Stretch at the morning","Stretch at least 5 minutes a day", TaskDifficulty.Easy))}
        testCharacters.forEach { it!!.addTask(ToDo("test todo", "just a test todo", TaskDifficulty.Very_easy, listOf("test","sublist")))}

    }

    @Test
    fun initTest(){
        assertEquals(3, charaterRepo.all().size)
        assertEquals(4, shop.all().size)
        assertEquals(7, abilityRepo.all().size)
        charaterRepo.all().forEach {
            assertEquals(1, it.habits.size)
            assertEquals(1, it.dailies.size)
            assertEquals(1, it.toDos.size)
        }
    }

    @Test
    fun addRemoveTaskTest(){
        //tests if 'addTask' and 'removeTask' works correctly,
        //there is one default task of each kind and we gonna add one more to each array, check if it's added,
        //remove it and check if it's removed correctly
        val alice= charaterRepo["Alice"]?: fail()
        alice.addTask(Habit("new habit","just a test habit", TaskDifficulty.Very_easy))
        alice.addTask(Daily("new daily","just a test daily", TaskDifficulty.Very_easy))
        alice.addTask(ToDo("new todo", "just a test todo", TaskDifficulty.Very_easy, listOf("test","sublist")))
        assertEquals(2, alice.habits.size)
        assertEquals(2, alice.dailies.size)
        assertEquals(2, alice.toDos.size)
        alice.removeTask("new habit", TaskType.Habit)
        alice.removeTask("new daily", TaskType.Daily)
        alice.removeTask("new todo", TaskType.ToDo)
        assertEquals(1, alice.habits.size)
        assertEquals(1, alice.dailies.size)
        assertEquals(1, alice.toDos.size)
    }

    @Test
    fun completeTaskTest(){
        //tests if 'completeTask' function works correctly
        val alice= charaterRepo["Alice"]?: fail()
        alice.completeTask("Brush the teeth",TaskType.Habit)
        val habit = alice.habits.find { it.name == "Brush the teeth" }?: fail()
        //habit completion count went up by 1
        assertEquals(1, habit.completionCount)
        //Alice got 2 exp for Very_easy habit
        assertEquals(2, alice.experiencePoints)
        alice.completeTask("Stretch at the morning", TaskType.Daily)
        val daily= alice.dailies.find { it.name == "Stretch at the morning" }?: fail()
        //daily completion count went up by 1
        assertEquals(1, daily.completionCount)
        //Alice got 4 exp for Easy daily
        assertEquals(6, alice.experiencePoints)
        alice.completeTask("Prepare for test", TaskType.ToDo)
        //once to do completed it's removed from character array of todos, so there is only 1 left
        assertEquals(1, alice.toDos.size)
        alice.completeTask("Brush the teeth",TaskType.Habit)
        alice.completeTask("Brush the teeth",TaskType.Habit)
        assertEquals(2, alice.level)
        //by now Alice should be level 2
    }

    @Test
    fun buyItemTest(){
        //tests 'buyItem' function
        val alice= charaterRepo["Alice"]?: fail()
        alice.coins+=50
        shop.all().forEach { alice.buyItem(it.name, 1) }
        assertEquals(4, alice.personalRewards.size)
        //after all tasks completed before and a little shopping Alice got every item in the shop
    }

    @Test
    fun useQuestScrollTest(){
        //tests functionality of quests
        val alice= charaterRepo["Alice"]?: fail()
        assertEquals(0, alice.quests.size)
        alice.coins+=10
        alice.buyItem("Dust rabbits scroll",1)
        val questScroll = alice.personalRewards.find { it.name == "Dust rabbits scroll" }?: fail()
        questScroll.useItem(alice)
        assertEquals(1, alice.quests.size)
        alice.completeTask("Dust rabbits", TaskType.Quest)
        assertEquals(2, alice.personalRewards.find { it.name == "Green tea" }!!.quantity)
        assertEquals(0, alice.quests.size)
    }

    @Test
    fun abilitiesTest(){
        //tests abilities of every class
        charaterRepo.all().forEach { it.experiencePoints=4000 }
        charaterRepo.all().forEach { it.level=14 }
        charaterRepo.all().forEach { it.completeTask("Brush the teeth", TaskType.Habit) }
        //make everyone's level high enough
        val alice= charaterRepo["Alice"]?: fail()
        val bob= charaterRepo["Bob"]?: fail()
        val charlie= charaterRepo["Charlie"]?: fail()
        charaterRepo.all().forEach {
            assertEquals(0, it.abilities.size)
        }
        alice.learnAbility("Bursts of flame")
        alice.learnAbility("Cheering words")
        alice.learnAbility("Frost chill")
        assertEquals(3, alice.abilities.size)
        //learn abilities and check if they're learned successfully
        bob.learnAbility("Friendly protection")
        bob.learnAbility("Swordplay practice")
        assertEquals(2, bob.abilities.size)

        charlie.learnAbility("Greedy profit")
        charlie.learnAbility("Hunter's focus")
        assertEquals(2, charlie.abilities.size)

        burstsOfFlame.useAbility(alice)
        assertEquals(4044, alice.experiencePoints)
        //use abilities and check results
        swordplayPractice.useAbility(bob)
        assertEquals(4030, bob.experiencePoints)

        huntersFocus.useAbility(charlie)
        assertEquals(4037, charlie.experiencePoints)
        greedyProfit.useAbility(charlie)
        charlie.completeTask("test todo", TaskType.ToDo)
        assertEquals(4040, charlie.experiencePoints)
    }

    @Test
    fun challengesTest(){
        //tests if challenges works correctly
        val alice= charaterRepo["Alice"]?: fail()
        for (i in 0..90){
            alice.completeTask("Brush the teeth",TaskType.Habit)
        }
        assertEquals(true, solidHabit.checkChallengeCondition(alice))
        for (i in 0..5){
            alice.completeTask("Stretch at the morning", TaskType.Daily)

        }
        assertEquals(true, showingTheAttitude.checkChallengeCondition(alice))
    }

    @Test
    fun setEventTest(){
        //tests if admin can set event
        val event = ToDo("test event", "event just for test", TaskDifficulty.Medium, null)
        setEvent(event)
        charaterRepo.all().forEach {
            assertEquals(2, it.toDos.size)
        }
    }

    @Test
    fun addPersonalQuestTest(){
        //tests if admin can sey personal quests
        val alice= charaterRepo["Alice"]?: fail()
        class TestQuest(
            override val difficulty: TaskDifficulty
        ):Quest{
            override val name: String= "test quest"
            override val description: String= "quest just for test"
            override val rewards: Reward= Reward(0,0,null)
        }
        addPersonalQuest(alice.name,TestQuest(TaskDifficulty.Medium))
        assertEquals(1, alice.quests.size)
    }
}
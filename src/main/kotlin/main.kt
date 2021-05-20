import io.ktor.application.*
import io.ktor.features.*
import io.ktor.features.CORS
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import model.abilities.*
import model.challenges.challenges
import model.challenges.showingTheAttitude
import model.challenges.solidHabit
import model.items.*
import model.main_classes.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import rest.*


fun main(args: Array<String>) {
    Database.connect(
            "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
            driver = "org.h2.Driver"
    )
    val port = System.getenv("PORT")?.toInt() ?: 8080
    embeddedServer(Netty, port) {
        install(CORS){
            anyHost()
        }
        initDB()
        install(CallLogging)
        install(ContentNegotiation) {
            jackson {}
        }
        abilityRest()
        challengeRest()
        shopRest()
        userRest()
        characterRest()
        routing {
            route("/") {
                get {
                    call.respond("Hello world")
                }
            }
        }
    }.start(wait = true)
}

private fun initDB() {
    transaction {
        SchemaUtils.create(userTable, userCharacterTable, taskTable, buffTable, characterItemTable, characterAbilityTable, characterTable, itemTable, abilityTable, challenges)
    }
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
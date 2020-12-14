import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.testing.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import model.abilities.*
import model.challenges.challenges
import model.items.*
import model.mainClasses.*
import model.quests.DishDisaster
import model.quests.DustRabbits
import model.quests.MagicInBottle
import model.quests.Quest
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test
import rest.characterRest
import kotlin.test.assertEquals

class CharacterRestTest {

    private val testPath = "/character"

    @Test
    fun restRepoMapTest() {
        Database.connect(
                "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
                driver = "org.h2.Driver"
        )
        transaction {
            SchemaUtils.create(userTable, userCharacterTable, taskTable, buffTable, characterItemTable, characterAbilityTable, characterTable, itemTable, abilityTable, challenges)
        }
        initDB()
        testRest  {
            characterRest(
                    charactersRepo,
                    testPath,
                    Character.serializer()
            )
        }
        transaction {
            SchemaUtils.drop(userCharacterTable, userTable, characterItemTable, taskTable, characterAbilityTable, buffTable, itemTable, abilityTable, challenges, characterTable)
        }
    }

    private fun testRest(
            restModule: Application.() -> Unit
    ) {
        withTestApplication({
            install(ContentNegotiation) {
                json()
            }
            restModule()
        }) {

            // Post
            val charactersJson =
                    mapOf("Alice" to "MAGICIAN",
                            "Bob" to "WARRIOR",
                            "Charlie" to "ARCHER")
                            .map {
                                Json.encodeToString(
                                        Character.serializer(),
                                        Character(it.key, it.value, level = 15)
                                )
                            }
            charactersJson.map {
                handleRequest(HttpMethod.Post, testPath) {
                    setBodyAndHeaders(it)
                }.apply {
                    assertStatus(HttpStatusCode.Created)
                }
            }
            handleRequest(HttpMethod.Post, testPath) {
                setBodyAndHeaders("Wrong JSON")
            }.apply {
                assertStatus(HttpStatusCode.BadRequest)
            }

            val itemsJson =
                    arrayOf(GreenTea(2), Coffee(3), HealingPotion(2))
                            .map {
                                Json.encodeToString(
                                        Item.serializer(),
                                        it
                                )
                            }
            itemsJson.map {
                handleRequest(HttpMethod.Post, "$testPath/1/inventory") {
                    setBodyAndHeaders(it)
                }.apply {
                    assertStatus(HttpStatusCode.Created)
                }
            }
            handleRequest(HttpMethod.Post, "$testPath/1/inventory") {
                setBodyAndHeaders("Wrong JSON")
            }.apply {
                assertStatus(HttpStatusCode.BadRequest)
            }

            val habitsJson=
                    arrayOf(
                            Task("Brush the teeth", "Brush the teeth every morning", "VERYEASY", "HABIT", 1 ),
                            Task("Brush the teeth twice", "Brush the teeth every morning", "VERYEASY", "HABIT", 1 )
                    ).map {
                        Json.encodeToString(
                                Task.serializer(),
                                it
                        )
                    }
            habitsJson.map {
                handleRequest(HttpMethod.Post, "$testPath/1/habits"){
                    setBodyAndHeaders(it)
                }.apply {
                    assertStatus(HttpStatusCode.Created)
                }
            }
            handleRequest(HttpMethod.Post, "$testPath/1/habits") {
                setBodyAndHeaders("Wrong JSON")
            }.apply {
                assertStatus(HttpStatusCode.BadRequest)
            }

            val dailiesJson=
                    arrayOf(
                            Task("Stretch at the morning", "Stretch at least 5 minutes a day", "EASY","DAILY", 1 ),
                            Task("Stretch at the evening", "Stretch at least 5 minutes a day", "EASY","DAILY", 1 )
                    ).map {
                        Json.encodeToString(
                                Task.serializer(),
                                it
                        )
                    }
            dailiesJson.map {
                handleRequest(HttpMethod.Post, "$testPath/1/dailies"){
                    setBodyAndHeaders(it)
                }.apply {
                    assertStatus(HttpStatusCode.Created)
                }
            }
            handleRequest(HttpMethod.Post, "$testPath/1/dailies") {
                setBodyAndHeaders("Wrong JSON")
            }.apply {
                assertStatus(HttpStatusCode.BadRequest)
            }

            val todoJson=
                    arrayOf(
                            Task("test todo", "just a test todo", "VERYEASY","TODO", 1),
                            Task("second test todo", "just a test todo", "VERYEASY", "TODO", 1)
                    ).map {
                        Json.encodeToString(
                                Task.serializer(),
                                it
                        )
                    }
            todoJson.map {
                handleRequest(HttpMethod.Post, "$testPath/1/todo"){
                    setBodyAndHeaders(it)
                }.apply {
                    assertStatus(HttpStatusCode.Created)
                }
            }
            handleRequest(HttpMethod.Post, "$testPath/1/todo") {
                setBodyAndHeaders("Wrong JSON")
            }.apply {
                assertStatus(HttpStatusCode.BadRequest)
            }

            val questsJson=
                    arrayOf(
                            Task("first test quest", "test quest", "MEDIUM","QUEST", 1),
                            Task("second test quest", "test quest", "MEDIUM","QUEST", 1),
                            Task("third test quest", "test quest", "MEDIUM","QUEST", 1)

                    ).map {
                        Json.encodeToString(
                                Task.serializer(),
                                it
                        )
                    }
            questsJson.map {
                handleRequest(HttpMethod.Post, "$testPath/1/quests"){
                    setBodyAndHeaders(it)
                }.apply {
                    assertStatus(HttpStatusCode.Created)
                }
            }
            handleRequest(HttpMethod.Post, "$testPath/1/quests") {
                setBodyAndHeaders("Wrong JSON")
            }.apply {
                assertStatus(HttpStatusCode.BadRequest)
            }

            val abilitiesJson =
                    arrayOf(burstsOfFlame, cheeringWords, frostChill)
                            .map {
                                Json.encodeToString(
                                        Ability.serializer(),
                                        it
                                )
                            }
            abilitiesJson.map {
                handleRequest(HttpMethod.Post, "$testPath/1/abilities") {
                    setBodyAndHeaders(it)
                }.apply {
                    assertStatus(HttpStatusCode.Created)
                }
            }
            handleRequest(HttpMethod.Post, "$testPath/1/abilities") {
                setBodyAndHeaders("Wrong JSON")
            }.apply {
                assertStatus(HttpStatusCode.BadRequest)
            }

            val buffsJson=
                    arrayOf(
                            Buff("sweet tooth",character_id = 1),
                            Buff("sweet ears", character_id = 1),
                            Buff("sweet nose", character_id = 1)
                    ).map {
                        Json.encodeToString(
                                Buff.serializer(),
                                it
                        )
                    }
            buffsJson.map {
                handleRequest(HttpMethod.Post, "$testPath/1/buffs"){
                    setBodyAndHeaders(it)
                }.apply {
                    assertStatus(HttpStatusCode.Created)
                }
            }
            handleRequest(HttpMethod.Post, "$testPath/1/buffs") {
                setBodyAndHeaders("Wrong JSON")
            }.apply {
                assertStatus(HttpStatusCode.BadRequest)
            }

            // Get
            val characters = handleRequest(HttpMethod.Get, testPath).run {
                assertStatus(HttpStatusCode.OK)
                parseResponse(
                        ListSerializer(Character.serializer())
                )
            }
            assertEquals(3, characters?.size)
            handleRequest(HttpMethod.Get, "$testPath/${characters?.first()?.id}").run {
                assertStatus(HttpStatusCode.OK)
                val character = parseResponse(Character.serializer())
                assertEquals(characters?.first()?.name, character?.name)
            }

            val inventory= handleRequest(HttpMethod.Get, "$testPath/1/inventory" ).run {
                assertStatus(HttpStatusCode.OK)
                parseResponse(
                        ListSerializer(Item.serializer())
                )
            }
            assertEquals(3, inventory?.size)

            val habits= handleRequest(HttpMethod.Get, "$testPath/1/habits").run {
                assertStatus(HttpStatusCode.OK)
                parseResponse(
                        ListSerializer(Task.serializer())
                )
            }
            assertEquals(2, habits?.size)

            val dailies= handleRequest(HttpMethod.Get, "$testPath/1/dailies").run {
                assertStatus(HttpStatusCode.OK)
                parseResponse(
                        ListSerializer(Task.serializer())
                )
            }
            assertEquals(2, dailies?.size)

            val todos= handleRequest(HttpMethod.Get, "$testPath/1/todo").run {
                assertStatus(HttpStatusCode.OK)
                parseResponse(
                        ListSerializer(Task.serializer())
                )
            }
            assertEquals(2, todos?.size)

            val quests= handleRequest(HttpMethod.Get, "$testPath/1/quests").run {
                assertStatus(HttpStatusCode.OK)
                parseResponse(
                        ListSerializer(Task.serializer())
                )
            }
            assertEquals(3, quests?.size)

            val abilities= handleRequest(HttpMethod.Get, "$testPath/1/abilities").run{
                assertStatus(HttpStatusCode.OK)
                parseResponse(
                        ListSerializer(Ability.serializer())
                )
            }
            assertEquals(3, abilities?.size)

            val buffs= handleRequest(HttpMethod.Get, "$testPath/1/buffs").run {
                assertStatus(HttpStatusCode.OK)
                parseResponse(
                        ListSerializer(Buff.serializer())
                )
            }
            assertEquals(3, buffs?.size)

            // Put

            val charlie = characters?.find { it.name == "Charlie" }!!
            val updatedCharlie = Character("Updated Charlie",charlie.characterClass, charlie.id)
            handleRequest(HttpMethod.Put, "$testPath/${updatedCharlie.id}") {
                setBodyAndHeaders(Json.encodeToString(Character.serializer(), updatedCharlie))
            }.run {
                assertStatus(HttpStatusCode.Accepted)
            }
            handleRequest(HttpMethod.Get, "$testPath/${updatedCharlie.id}").run {
                assertStatus(HttpStatusCode.OK)
                val character = parseResponse(Character.serializer())
                assertEquals("Updated Charlie", character?.name)
            }

            // Delete
            val bob = characters.find { it.name == "Bob" }!!
            handleRequest(HttpMethod.Delete, "$testPath/${bob.id}").run {
                assertStatus(HttpStatusCode.OK)
            }

            val cofee= inventory?.find { it.name == "Coffee" }!!
            handleRequest(HttpMethod.Delete, "$testPath/1/inventory/${cofee.id}").run {
                assertStatus(HttpStatusCode.OK)
            }

            val brushTooth= habits?.find { it.name == "Brush the teeth" }!!
            handleRequest(HttpMethod.Delete, "$testPath/1/habits/${brushTooth.name}").run {
                assertStatus(HttpStatusCode.OK)
            }

            val stretch= dailies?.find { it.name == "Stretch at the morning"}!!
            handleRequest(HttpMethod.Delete, "$testPath/1/dailies/${stretch.name}").run {
                assertStatus(HttpStatusCode.OK)
            }

            val testTdo= todos?.find { it.name == "test todo" }!!
            handleRequest(HttpMethod.Delete, "$testPath/1/todo/${testTdo.name}").run {
                assertStatus(HttpStatusCode.OK)
            }

            val dishieDisastr= quests?.find { it.name == "first test quest" }!!
            handleRequest(HttpMethod.Delete, "$testPath/1/quests/${dishieDisastr.name}").run {
                assertStatus(HttpStatusCode.OK)
            }

            val sweetTooth= buffs?.find { it.name == "sweet tooth" }!!
            handleRequest(HttpMethod.Delete, "$testPath/1/buffs/${sweetTooth.name}").run {
                assertStatus(HttpStatusCode.OK)
            }

            // Final check
            val updatedCharacters = handleRequest(HttpMethod.Get, testPath).run {
                assertStatus(HttpStatusCode.OK)
                parseResponse(
                        ListSerializer(Character.serializer())
                )
            }?.map { it.name }!!

            assert(updatedCharacters.size == 2)
            assert(updatedCharacters.contains("Alice"))
            assert(updatedCharacters.contains("Updated Charlie"))

            val alice= handleRequest(HttpMethod.Get, "$testPath/1").run {
                assertStatus(HttpStatusCode.OK)
                parseResponse(Character.serializer())
            }
            println(alice?.habits)
            assert(alice?.habits?.size == 1)
            assert(alice?.dailies?.size == 1)
            assert(alice?.toDos?.size == 1)
            assert(alice?.quests?.size == 2)

            assert(alice?.abilities?.size == 3)
            assert(alice?.buffs?.size == 2)
            assert(alice?.inventory?.size == 2)
        }
    }
}

private fun initDB() {
    transaction {
        itemTable.insert { fill(it, Coffee(20)) }
    }
    transaction {
        itemTable.insert { fill(it, GreenTea(20)) }
    }
    transaction {
        itemTable.insert { fill(it, HealingPotion(20)) }
    }
    transaction {
        itemTable.insert { fill(it, DustRabbitsScroll(20)) }
    }
    transaction {
        itemTable.insert { fill(it, DishDisasterScroll(20)) }
    }
    transaction {
        itemTable.insert { fill(it, MagicInBottleScroll(20)) }
    }

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
}
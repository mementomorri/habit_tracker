import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.testing.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import model.abilities.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test
import rest.abilityRest
import kotlin.test.assertEquals

class AbilityRestTest {

    private val testPath = "/ability"

    @Test
    fun restRepoMapTest() {
        Database.connect(
                "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
                driver = "org.h2.Driver"
        )
        transaction { SchemaUtils.create(abilityTable) }
        testRest  {
            abilityRest(
                    abilitiesRepo,
                    testPath,
                    Ability.serializer()
            )
        }
        transaction { SchemaUtils.drop(abilityTable) }
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

            val abilitiesJson =
                    arrayOf(burstsOfFlame, cheeringWords, friendlyProtection)
                            .map {
                                Json.encodeToString(
                                        Ability.serializer(),
                                        it
                                )
                            }
            abilitiesJson.map {
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

            // Get
            val abilities = handleRequest(HttpMethod.Get, testPath).run {
                assertStatus(HttpStatusCode.OK)
                parseResponse(
                        ListSerializer(Ability.serializer())
                )
            }
            assertEquals(3, abilities?.size)
            handleRequest(HttpMethod.Get, "$testPath/${abilities?.first()?.id}").run {
                assertStatus(HttpStatusCode.OK)
                val ability = parseResponse(Ability.serializer())
                assertEquals(abilities?.first()?.name, ability?.name)
            }

            // Put
            val bursts = abilities?.find { it.name == "Bursts of flame" }!!
            handleRequest(HttpMethod.Delete, "$testPath/${bursts.id}").run {
                assertStatus(HttpStatusCode.OK)
            }

            // Final check
            val updatedAbilities = handleRequest(HttpMethod.Get, testPath).run {
                assertStatus(HttpStatusCode.OK)
                parseResponse(
                        ListSerializer(Ability.serializer())
                )
            }?.map { it.name }!!

            assert(updatedAbilities.size == 2)
            assert(updatedAbilities.contains("Friendly protection"))

        }
    }
}
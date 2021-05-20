package end_point_tests

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.testing.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import model.challenges.Challenge
import model.challenges.showingTheAttitude
import model.challenges.solidHabit
import org.junit.Test
import repo.ChallengeRepoMap
import rest.challengeRest
import kotlin.test.assertEquals

class ChallengeRestTest {

    private val testPath = "/challenge"

    @Test
    fun restRepoMapTest() {
        testRest  {
            challengeRest(
                    ChallengeRepoMap(),
                    testPath,
                    Challenge.serializer()
            )
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
            val challengesJson =
                    arrayOf(showingTheAttitude, solidHabit,)
                            .map {
                                Json.encodeToString(
                                        Challenge.serializer(),
                                        it
                                )
                            }
            challengesJson.map {
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
            val challenges = handleRequest(HttpMethod.Get, testPath).run {
                assertStatus(HttpStatusCode.OK)
                parseResponse(
                        ListSerializer(Challenge.serializer())
                )
            }
            assertEquals(2, challenges?.size)

            // Final check
            val updatedChallenges = handleRequest(HttpMethod.Get, testPath).run {
                assertStatus(HttpStatusCode.OK)
                parseResponse(
                        ListSerializer(Challenge.serializer())
                )
            }?.map { it.name }!!

            assert(updatedChallenges.size == 2)
            assert(updatedChallenges.contains("Showing the attitude"))
            assert(updatedChallenges.contains("Solid habit"))

        }
    }
}
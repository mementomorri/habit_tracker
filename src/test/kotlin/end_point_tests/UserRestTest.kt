package end_point_tests

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.testing.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import model.main_classes.User
import org.junit.Test
import repo.UserRepoMap
import rest.userRest
import kotlin.test.assertEquals

class UserRestTest {

    private val testPath = "/user"

    @Test
    fun restRepoMapTest() {
        testRest  {
            userRest(
                UserRepoMap(),
                testPath,
                User.serializer()
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
            val usersJson =
                mapOf("Alice" to "4l1c3", "Bob" to "b0bb13", "Charlie" to "ch4r1i3")
                    .map {
                        Json.encodeToString(
                            User.serializer(),
                            User(it.key, it.value)
                        )
                    }
            usersJson.map {
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
            val users = handleRequest(HttpMethod.Get, testPath).run {
                assertStatus(HttpStatusCode.OK)
                parseResponse(
                    ListSerializer(User.serializer())
                )
            }
            assertEquals(3, users?.size)
            handleRequest(HttpMethod.Get, "$testPath/${users?.first()?.id}").run {
                assertStatus(HttpStatusCode.OK)
                val user = parseResponse(User.serializer())
                assertEquals(users?.first()?.name, user?.name)
            }

            // Put

            val charlie = users?.find { it.name == "Charlie" }!!
            val updatedCharlie = User("Updated Charlie", charlie.password, charlie.email, charlie.id)
            handleRequest(HttpMethod.Put, "$testPath/${updatedCharlie.id}") {
                setBodyAndHeaders(Json.encodeToString(User.serializer(), updatedCharlie))
            }.run {
                assertStatus(HttpStatusCode.Accepted)
            }
            handleRequest(HttpMethod.Get, "$testPath/${updatedCharlie.id}").run {
                assertStatus(HttpStatusCode.OK)
                val user = parseResponse(User.serializer())
                assertEquals("Updated Charlie", user?.name)
            }

            // Delete
            val bob = users.find { it.name == "Bob" }!!
            handleRequest(HttpMethod.Delete, "$testPath/${bob.id}").run {
                assertStatus(HttpStatusCode.OK)
            }

            // Final check
            val updatedUsers = handleRequest(HttpMethod.Get, testPath).run {
                assertStatus(HttpStatusCode.OK)
                parseResponse(
                    ListSerializer(User.serializer())
                )
            }?.map { it.name }!!

            assert(updatedUsers.size == 2)
            assert(updatedUsers.contains("Alice"))
            assert(updatedUsers.contains("Updated Charlie"))

        }
    }
}

fun TestApplicationCall.assertStatus(status: HttpStatusCode) =
    assertEquals(status, response.status())

fun TestApplicationRequest.setBodyAndHeaders(body: String) {
    setBody(body)
    addHeader("Content-Type", "application/json")
    addHeader("Accept", "application/json")
}

fun <T> TestApplicationCall.parseResponse(
    serializer: KSerializer<T>
) =
    try {
        Json.decodeFromString(
            serializer,
            response.content ?: ""
        )
    } catch (e: Throwable) {
        null
    }
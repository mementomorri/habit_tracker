package end_point_tests

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.testing.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import model.items.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test
import rest.shopRest
import model.main_classes.shopRepo
import kotlin.test.assertEquals

class ShopRestTest {

    private val testPath = "/shop"

    @Test
    fun restRepoMapTest() {
        Database.connect(
                "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
                driver = "org.h2.Driver"
        )
        transaction { SchemaUtils.create(itemTable) }
        testRest  {
            shopRest(
                    shopRepo,
                    testPath,
                    Item.serializer()
            )
        }
        transaction { SchemaUtils.drop(itemTable) }
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

            val itemsJson =
                    arrayOf(GreenTea(2), Coffee(3), HealingPotion(2))
                            .map {
                                Json.encodeToString(
                                        Item.serializer(),
                                        it
                                )
                            }
            itemsJson.map {
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
            val shop = handleRequest(HttpMethod.Get, testPath).run {
                assertStatus(HttpStatusCode.OK)
                parseResponse(
                        ListSerializer(Item.serializer())
                )
            }
            assertEquals(3, shop?.size)
            handleRequest(HttpMethod.Get, "$testPath/${shop?.first()?.id}").run {
                assertStatus(HttpStatusCode.OK)
                val item = parseResponse(Item.serializer())
                assertEquals(shop?.first()?.name, item?.name)
            }

            // Put
            val cofee = shop?.find { it.name == "Coffee" }!!
            val updatedCofee = Coffee(2, cofee.id)
            handleRequest(HttpMethod.Put, "$testPath/${updatedCofee.id}") {
                setBodyAndHeaders(Json.encodeToString(Item.serializer(), updatedCofee))
            }.run {
                assertStatus(HttpStatusCode.Accepted)
            }
            handleRequest(HttpMethod.Get, "$testPath/${updatedCofee.id}").run {
                assertStatus(HttpStatusCode.OK)
                val item = parseResponse(Item.serializer())
                assertEquals(2, item?.quantity)
            }

            // Final check
            val updatedShop = handleRequest(HttpMethod.Get, testPath).run {
                assertStatus(HttpStatusCode.OK)
                parseResponse(
                        ListSerializer(Item.serializer())
                )
            }?.map { it.name }!!

            assert(updatedShop.size == 3)
            assert(updatedShop.contains("Healing potion"))
        }
    }
}
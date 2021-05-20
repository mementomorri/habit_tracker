package rest

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import model.items.Item
import repo.Repo
import model.main_classes.shopRepo

fun Application.shopRest(
        repo: Repo<Item> = shopRepo,
        path:String= "/shop",
        serializer: KSerializer<Item> = Item.serializer()
){
    routing {
        route(path){
            get{
                call.respond(repo.read())
            }
            post {
                call.respond(
                        parseItemBody(serializer)?.let { elem ->
                            if (repo.create(elem))
                                HttpStatusCode.Created
                            else
                                HttpStatusCode.NotFound
                        } ?: HttpStatusCode.BadRequest
                )
            }
        }
        route("$path/{itemId}"){
            get {
                call.respond(
                        parseItemId()?.let { id ->
                            repo.read(id)?.let { elem ->
                                elem
                            } ?: HttpStatusCode.NotFound
                        } ?: HttpStatusCode.BadRequest
                )
            }
            put {
                call.respond(
                        parseItemBody(serializer)?.let { elem ->
                            parseItemId()?.let { id ->
                                if(repo.update(id, elem))
                                    HttpStatusCode.Accepted
                                else
                                    HttpStatusCode.NotFound
                            }
                        }?: HttpStatusCode.BadRequest
                )
            }
        }
    }
}

fun PipelineContext<Unit, ApplicationCall>.parseItemId(id: String = "itemId") =
        call.parameters[id]?.toIntOrNull()

suspend fun PipelineContext<Unit, ApplicationCall>.parseItemBody(
        serializer: KSerializer<Item>
) =
        try {
            Json.decodeFromString(
                    serializer,
                    call.receive()
            )
        } catch (e: Throwable) {
            null
        }
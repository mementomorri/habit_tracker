package rest

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import model.main_classes.User
import repo.Repo
import repo.UserRepoMap

fun Application.userRest(
    repo: Repo<User> = UserRepoMap(),
    path:String= "/user",
    serializer: KSerializer<User> = User.serializer()
){
    routing {
        route(path){
            get{
                call.respond(repo.read())
            }
            post {
                call.respond(
                    parseUserBody(serializer)?.let { elem ->
                        if (repo.create(elem))
                            HttpStatusCode.Created
                        else
                            HttpStatusCode.NotFound
                    } ?: HttpStatusCode.BadRequest
                )
            }
        }
        route("$path/{userId}"){
            get {
                call.respond(
                    parseUserId()?.let { id ->
                        repo.read(id)?.let { elem ->
                            elem
                        } ?: HttpStatusCode.NotFound
                    } ?: HttpStatusCode.BadRequest
                )
            }
            put {
                call.respond(
                    parseUserBody(serializer)?.let { elem ->
                        parseUserId()?.let { id ->
                            if(repo.update(id, elem))
                                HttpStatusCode.Accepted
                            else
                                HttpStatusCode.NotFound
                        }
                    }?: HttpStatusCode.BadRequest
                )
            }
            delete {
                call.respond(
                    parseUserId()?.let { i: Int ->
                        if (repo.delete(i))
                            HttpStatusCode.OK
                        else
                            HttpStatusCode.NotFound
                    }?: HttpStatusCode.BadRequest
                )
            }
        }
    }
}

fun PipelineContext<Unit, ApplicationCall>.parseUserId(id: String = "userId") =
    call.parameters[id]?.toIntOrNull()

suspend fun PipelineContext<Unit, ApplicationCall>.parseUserBody(
    serializer: KSerializer<User>
) =
    try {
        Json.decodeFromString(
            serializer,
            call.receive()
        )
    } catch (e: Throwable) {
        null
    }
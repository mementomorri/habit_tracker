package rest

import model.main_classes.abilitiesRepo
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import model.abilities.Ability
import repo.Repo

fun Application.abilityRest(
        repo: Repo<Ability> = abilitiesRepo,
        path:String= "/ability",
        serializer: KSerializer<Ability> = Ability.serializer()
){
    routing {
        route(path){
            get{
                call.respond(repo.read())
            }
            post {
                call.respond(
                    parseAbilityBody(serializer)?.let { elem ->
                        if (repo.create(elem))
                            HttpStatusCode.Created
                        else
                            HttpStatusCode.NotFound
                    } ?: HttpStatusCode.BadRequest
                )
            }
        }
        route("$path/{abilityId}"){
            get {
                call.respond(
                    parseAbilityId()?.let { id ->
                        repo.read(id)?.let { elem ->
                            elem
                        } ?: HttpStatusCode.NotFound
                    } ?: HttpStatusCode.BadRequest
                )
            }
            delete {
                call.respond(
                    parseAbilityId()?.let { i: Int ->
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

fun PipelineContext<Unit, ApplicationCall>.parseAbilityId(id: String = "abilityId") =
    call.parameters[id]?.toIntOrNull()

suspend fun PipelineContext<Unit, ApplicationCall>.parseAbilityBody(
    serializer: KSerializer<Ability>
) =
    try {
        Json.decodeFromString(
            serializer,
            call.receive()
        )
    } catch (e: Throwable) {
        null
    }
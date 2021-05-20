package rest

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import model.challenges.Challenge
import repo.ChallengeRepoMap

fun Application.challengeRest(
        repo: ChallengeRepoMap = ChallengeRepoMap(),
        path:String= "/challenge",
        serializer: KSerializer<Challenge> = Challenge.serializer()
){
    routing {
        route(path){
            get{
                call.respond(repo.read())
            }
            post {
                call.respond(
                        parseChallengeBody(serializer)?.let { elem ->
                            if (repo.create(elem))
                                HttpStatusCode.Created
                            else
                                HttpStatusCode.NotFound
                        } ?: HttpStatusCode.BadRequest
                )
            }
        }
    }
}

suspend fun PipelineContext<Unit, ApplicationCall>.parseChallengeBody(
        serializer: KSerializer<Challenge>
) =
        try {
            Json.decodeFromString(
                    serializer,
                    call.receive()
            )
        } catch (e: Throwable) {
            null
        }
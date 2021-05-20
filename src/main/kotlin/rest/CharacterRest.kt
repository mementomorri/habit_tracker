package rest

import model.main_classes.abilitiesRepo
import model.main_classes.charactersRepo
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import model.abilities.Ability
import model.abilities.CharacterAbilityFiller
import model.abilities.characterAbilityTable
import model.items.Item
import model.main_classes.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import repo.Repo

fun Application.characterRest(
        repo: Repo<Character> = charactersRepo,
        path:String= "/character",
        characterSerializer: KSerializer<Character> = Character.serializer(),
        itemSerializer: KSerializer<Item> = Item.serializer(),
        taskSerializer: KSerializer<Task> = Task.serializer(),
        abilitySerializer: KSerializer<Ability> = Ability.serializer(),
        buffSerializer: KSerializer<Buff> = Buff.serializer()
){
    routing {
        route(path){
            get{
                call.respond(repo.read())
                HttpStatusCode.OK
            }
            post {
                call.respond(
                        parseCharacterBody(characterSerializer)?.let { elem ->
                            if (repo.create(elem))
                                HttpStatusCode.Created
                            else
                                HttpStatusCode.NotFound
                        } ?: HttpStatusCode.BadRequest
                )
            }
        }
        route("$path/{characterId}"){
            get {
                call.respond(
                        parseCharacterId()?.let { id ->
                            repo.read(id)?.let { elem ->
                                elem
                            } ?: HttpStatusCode.NotFound
                        } ?: HttpStatusCode.BadRequest
                )
            }
            put {
                call.respond(
                        parseCharacterBody(characterSerializer)?.let { elem ->
                            parseCharacterId()?.let { id ->
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
                        parseCharacterId()?.let { i: Int ->
                            if (repo.delete(i))
                                HttpStatusCode.OK
                            else
                                HttpStatusCode.NotFound
                        }?: HttpStatusCode.BadRequest
                )
            }
        }
        route("$path/{characterId}/inventory"){
            get {
                call.respond(
                        parseCharacterId()?.let { id ->
                            repo.read(id)?.let { character ->
                                character.inventory
                            } ?: HttpStatusCode.NotFound
                        } ?: HttpStatusCode.BadRequest
                )
            }
            post {
                call.respond(
                        parseCharacterId()?.let { id ->
                            repo.read(id)?.let { character ->
                                parseItemBody(itemSerializer)?.let { item ->
                                    if (character.addItemToInventory(item))
                                        HttpStatusCode.Created
                                    else
                                        HttpStatusCode.NotFound
                                }?: HttpStatusCode.BadRequest
                            }?: HttpStatusCode.NotFound
                        }?: HttpStatusCode.BadRequest
                )
            }
        }
        route("$path/{characterId}/inventory/{itemId}"){
            delete {
                call.respond(
                        parseCharacterId()?.let { characterId ->
                            repo.read(characterId)?.let { character ->
                                parseItemId()?.let { itemId ->
                                    if (character.removeItemFromInventory(itemId))
                                        HttpStatusCode.OK
                                    else
                                        HttpStatusCode.NotFound
                                }?: HttpStatusCode.BadRequest
                            }?: HttpStatusCode.NotFound
                        }?: HttpStatusCode.BadRequest
                )
            }
        }
        route("$path/{characterId}/habits"){
            get {
                call.respond(
                        parseCharacterId()?.let { id ->
                            repo.read(id)?.let { character ->
                                character.habits
                            }?: HttpStatusCode.NotFound
                        }?: HttpStatusCode.BadRequest
                )
            }
            post {
                call.respond(
                        parseCharacterId()?.let { id ->
                            repo.read(id)?.let { character ->
                                parseTaskBody(taskSerializer)?.let { task ->
                                    if (character.addTask(task))
                                        HttpStatusCode.Created
                                    else
                                        HttpStatusCode.NotFound
                                }?: HttpStatusCode.BadRequest
                            }?: HttpStatusCode.NotFound
                        }?: HttpStatusCode.BadRequest
                )
            }
        }
        route("$path/{characterId}/habits/{taskName}"){
            delete {
                call.respond(
                        parseCharacterId()?.let { characterId ->
                            repo.read(characterId)?.let { character ->
                                parseTaskName()?.let { taskName ->
                                    if (character.removeTask(taskName, TaskType.HABIT))
                                        HttpStatusCode.OK
                                    else
                                        HttpStatusCode.NotFound
                                }?: HttpStatusCode.BadRequest
                            }?: HttpStatusCode.NotFound
                        }?: HttpStatusCode.BadRequest
                )
            }
        }
        route("$path/{characterId}/dailies"){
            get {
                call.respond(
                        parseCharacterId()?.let { id ->
                            repo.read(id)?.let { character ->
                                character.dailies
                            }?: HttpStatusCode.NotFound
                        }?: HttpStatusCode.BadRequest
                )
            }
            post {
                call.respond(
                        parseCharacterId()?.let { id ->
                            repo.read(id)?.let { character ->
                                parseTaskBody(taskSerializer)?.let { task ->
                                    if (character.addTask(task))
                                        HttpStatusCode.Created
                                    else
                                        HttpStatusCode.NotFound
                                }?: HttpStatusCode.BadRequest
                            }?: HttpStatusCode.NotFound
                        }?: HttpStatusCode.BadRequest
                )
            }
        }
        route("$path/{characterId}/dailies/{taskName}"){
            delete {
                call.respond(
                        parseCharacterId()?.let { characterId ->
                            repo.read(characterId)?.let { character ->
                                parseTaskName()?.let { taskName ->
                                    if (character.removeTask(taskName, TaskType.DAILY))
                                        HttpStatusCode.OK
                                    else
                                        HttpStatusCode.NotFound
                                } ?: HttpStatusCode.BadRequest
                            } ?: HttpStatusCode.NotFound
                        } ?: HttpStatusCode.BadRequest
                )
            }
        }
        route("$path/{characterId}/todo"){
            get {
                call.respond(
                        parseCharacterId()?.let { id ->
                            repo.read(id)?.let { character ->
                                character.toDos
                            }?: HttpStatusCode.NotFound
                        }?: HttpStatusCode.BadRequest
                )
            }
            post {
                call.respond(
                        parseCharacterId()?.let { id ->
                            repo.read(id)?.let { character ->
                                parseTaskBody(taskSerializer)?.let { task ->
                                    if (character.addTask(task))
                                        HttpStatusCode.Created
                                    else
                                        HttpStatusCode.NotFound
                                }?: HttpStatusCode.BadRequest
                            }?: HttpStatusCode.NotFound
                        }?: HttpStatusCode.BadRequest
                )
            }
        }
        route("$path/{characterId}/todo/{taskName}"){
            delete {
                call.respond(
                        parseCharacterId()?.let { characterId ->
                            repo.read(characterId)?.let { character ->
                                parseTaskName()?.let { taskName ->
                                    if (character.removeTask(taskName, TaskType.TODO))
                                        HttpStatusCode.OK
                                    else
                                        HttpStatusCode.NotFound
                                } ?: HttpStatusCode.BadRequest
                            } ?: HttpStatusCode.NotFound
                        } ?: HttpStatusCode.BadRequest
                )
            }
        }
        route("$path/{characterId}/quests"){
            get {
                call.respond(
                        parseCharacterId()?.let { id ->
                            repo.read(id)?.let { character ->
                                character.quests
                            }?: HttpStatusCode.NotFound
                        }?: HttpStatusCode.BadRequest
                )
            }
            post {
                call.respond(
                        parseCharacterId()?.let { id ->
                            repo.read(id)?.let { character ->
                                parseTaskBody(taskSerializer)?.let { task ->
                                    if (character.addTask(task))
                                        HttpStatusCode.Created
                                    else
                                        HttpStatusCode.NotFound
                                }?: HttpStatusCode.BadRequest
                            }?: HttpStatusCode.NotFound
                        }?: HttpStatusCode.BadRequest
                )
            }
        }
        route("$path/{characterId}/quests/{taskName}"){
            delete {
                call.respond(
                        parseCharacterId()?.let { characterId ->
                            repo.read(characterId)?.let { character ->
                                parseTaskName()?.let { taskName ->
                                    if (character.removeTask(taskName, TaskType.QUEST))
                                        HttpStatusCode.OK
                                    else
                                        HttpStatusCode.NotFound
                                } ?: HttpStatusCode.BadRequest
                            } ?: HttpStatusCode.NotFound
                        } ?: HttpStatusCode.BadRequest
                )
            }
        }
        route("$path/{characterId}/abilities"){
            get {
                call.respond(
                        parseCharacterId()?.let { id ->
                            repo.read(id)?.let { character ->
                                character.abilities
                            }?: HttpStatusCode.NotFound
                        }?: HttpStatusCode.BadRequest
                )
            }
            post {
                call.respond(
                        parseCharacterId()?.let { id ->
//                            repo.read(id)?.let { character ->
                                parseAbilityBody(abilitySerializer)?.let{ ability ->
                                    if (addAbility(id, ability))
                                        HttpStatusCode.Created
                                    else
                                        HttpStatusCode.NotFound
                                }?: HttpStatusCode.BadRequest
//                            }?: HttpStatusCode.NotFound
                        }?: HttpStatusCode.BadRequest
                )
            }
        }
        route("$path/{characterId}/buffs"){
            get {
                call.respond(
                        parseCharacterId()?.let { id ->
                            repo.read(id)?.let { character ->
                                character.buffs
                            }?: HttpStatusCode.NotFound
                        }?: HttpStatusCode.BadRequest
                )
            }
            post {
                call.respond(
                        parseCharacterId()?.let { id ->
                            parseBuffBody(buffSerializer)?.let { buff ->
                                if (addBuff(id, buff))
                                    HttpStatusCode.Created
                                else
                                    HttpStatusCode.NotFound
                            } ?: HttpStatusCode.BadRequest
                        }?: HttpStatusCode.BadRequest
                )
            }
        }
        route("$path/{characterId}/buffs/{buffName}"){
            delete {
                call.respond(
                        parseCharacterId()?.let { id ->
                            parseBuffName()?.let { buffName ->
                                if (removeBuff(id, buffName))
                                    HttpStatusCode.OK
                                else
                                    HttpStatusCode.NotFound
                            }?: HttpStatusCode.BadRequest
                        }?: HttpStatusCode.BadRequest
                )
            }
        }
    }
}

fun PipelineContext<Unit, ApplicationCall>.parseCharacterId(id: String = "characterId") =
        call.parameters[id]?.toIntOrNull()

fun PipelineContext<Unit, ApplicationCall>.parseTaskName(id: String = "taskName") =
        call.parameters[id]

fun PipelineContext<Unit, ApplicationCall>.parseBuffName(id: String = "buffName") =
        call.parameters[id]

suspend fun PipelineContext<Unit, ApplicationCall>.parseCharacterBody(
        serializer: KSerializer<Character>
) =
        try {
            Json.decodeFromString(
                    serializer,
                    call.receive()
            )
        } catch (e: Throwable) {
            null
        }

suspend fun PipelineContext<Unit, ApplicationCall>.parseTaskBody(
        serializer: KSerializer<Task>
) =
        try {
            Json.decodeFromString(
                    serializer,
                    call.receive()
            )
        } catch (e: Throwable) {
            null
        }

suspend fun PipelineContext<Unit, ApplicationCall>.parseBuffBody(
        serializer: KSerializer<Buff>
) =
        try {
            Json.decodeFromString(
                    serializer,
                    call.receive()
            )
        } catch (e: Throwable) {
            null
        }

private fun addBuff(characterId: Int, buff: Buff): Boolean{
    transaction {
        buffTable.insert { fill(it, buff) }
    }
    return charactersRepo.read(characterId)?.buffs?.firstOrNull { it.name == buff.name } != null
}

private fun removeBuff(characterId: Int, buffName:String): Boolean{
    return transaction {
        buffTable.deleteWhere { (buffTable.character_id eq characterId) and (buffTable.name eq buffName) } > 0
    }
}

private fun addAbility(characterId: Int, ability: Ability): Boolean{
    val abId= abilitiesRepo.read().firstOrNull{ it.name == ability.name }
    return if (abId != null){
        transaction {
            characterAbilityTable.insert { fill(it, CharacterAbilityFiller(abId.id, characterId)) }
            true
        }
    } else {
        false
    }
}
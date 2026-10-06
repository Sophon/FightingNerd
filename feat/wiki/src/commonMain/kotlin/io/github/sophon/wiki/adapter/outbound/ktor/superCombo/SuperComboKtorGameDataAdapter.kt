package io.github.sophon.wiki.adapter.outbound.ktor.superCombo

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.core.network.safeCall
import io.github.sophon.core.wiki.util.getWikiImageUrl
import io.github.sophon.wiki.app.outPort.FetchGameDataPort
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.Move
import io.github.sophon.wiki.model.wiki.Game
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class SuperComboKtorGameDataAdapter(
    private val httpClient: HttpClient,
) : FetchGameDataPort {

    override fun fetch(
        game: Game,
    ): Flow<Result<Pair<Character, List<Move>>, DataError.Remote>> {
        val flow = flow {
            fetchCharacterList(game)
                .onSuccess { characterList ->
                    for (character in characterList) {
                        val characterWithMovesResult = fetchMoveList(game, character)
                            .map { moveList -> character to moveList }
                        emit(characterWithMovesResult)
                    }
                }
                .onError { error -> emit(Result.Error(error)) }
        }
        return flow
    }

    private suspend fun fetchCharacterList(
        game: Game,
    ): Result<List<Character>, DataError.Remote> {
        val table = SuperComboTables.characterTableByGame[game]
            ?: return Result.Error(DataError.Remote.PAGE_NOT_FOUND)

        val characterListResult = safeCall<SuperComboCharacterListResponseDto> {
            httpClient.get(BASE_URL) {
                parameter("action", "cargoquery")
                parameter("tables", table.name)
                parameter("fields", table.fieldList.joinToString(","))
                parameter("format", "json")
                parameter("limit", LIMIT_CHARACTERS)
            }
        }
            .flatMap { dto ->
                resolveCharacterImageUrls(dto)
                    .map { imageUrlMap -> dto.toDomain(game, imageUrlMap) }
            }
        return characterListResult
    }

    private suspend fun fetchMoveList(
        game: Game,
        character: Character,
    ): Result<List<Move>, DataError.Remote> {
        val table = SuperComboTables.moveTableByGame[game]
            ?: return Result.Error(DataError.Remote.PAGE_NOT_FOUND)

        val moveListResult = safeCall<SuperComboMoveListResponseDto> {
            httpClient.get(BASE_URL) {
                parameter("action", "cargoquery")
                parameter("tables", table.name)
                parameter("fields", table.fieldList.joinToString(","))
                parameter("where", "chara='${character.remoteQueryId}'")
                parameter("format", "json")
                parameter("limit", LIMIT_MOVES)
            }
        }
            .flatMap { dto ->
                resolveHitboxUrls(dto)
                    .map { imageUrlMap -> dto.toDomain(game, imageUrlMap) }
            }
        return moveListResult
    }

    private suspend fun resolveCharacterImageUrls(
        dto: SuperComboCharacterListResponseDto,
    ): Result<Map<String, String>, DataError.Remote> {
        val fileNameList = dto.cargoquery
            .flatMap { query -> listOfNotNull(query.title.icon, query.title.portrait) }
            .distinct()

        val imageUrlResult = getWikiImageUrl(
            httpClient = httpClient,
            url = BASE_URL,
            fileNames = fileNameList,
        )
        return imageUrlResult
    }

    private suspend fun resolveHitboxUrls(
        dto: SuperComboMoveListResponseDto,
    ): Result<Map<String, String>, DataError.Remote> {
        val fileNameList = dto.cargoQuery
            .asSequence()
            .flatMap { query -> listOfNotNull(query.title.hitboxes, query.title.images) }
            .flatMap { fileNames -> fileNames.split(",") }
            .map { fileName -> fileName.trim() }
            .filter { fileName -> fileName.isNotEmpty() }
            .distinct()
            .toList()

        val hitboxUrlResult = getWikiImageUrl(
            httpClient = httpClient,
            url = BASE_URL,
            fileNames = fileNameList,
        )
        return hitboxUrlResult
    }


    private companion object {
        const val BASE_URL = "https://wiki.supercombo.gg/api.php"
        const val LIMIT_CHARACTERS = 50
        const val LIMIT_MOVES = 200
    }
}

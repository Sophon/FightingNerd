package io.github.sophon.wikidragdown.data

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.network.safeCall
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.wikidragdown.domain.BASE_URL
import io.github.sophon.wikidragdown.domain.LIMIT_CHARACTERS
import io.github.sophon.wikidragdown.domain.LIMIT_MOVES
import io.github.sophon.wikidragdown.domain.WIKI_BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.head
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

internal interface DragDownDataSource {
    suspend fun downloadCharacterList(table: String): Result<List<CharacterResponseDto>, DataError.Remote>
    suspend fun downloadMoveList(table: String, character: Character): Result<List<MoveResponseDto>, DataError.Remote>
    suspend fun resolveCharacterImageUrls(characters: List<CharacterResponseDto>): Result<Map<String, String>, DataError.Remote>
    suspend fun resolveHitboxUrls(moves: List<MoveResponseDto>): Result<Map<String, String>, DataError.Remote>
}

internal class DragDownDataSourceImpl(
    private val httpClient: HttpClient,
): DragDownDataSource {

    private val redirectClient = httpClient.config {
        followRedirects = false
    }

    override suspend fun downloadCharacterList(
        table: String,
    ): Result<List<CharacterResponseDto>, DataError.Remote> {
        return safeCall {
            httpClient.get(urlString = BASE_URL) {
                parameter("action", "cargoquery")
                parameter("tables", table)
                parameter("limit", LIMIT_CHARACTERS)
                parameter("format", "json")
                parameter("fields", getCharacterFields())
            }
        }
    }

    override suspend fun downloadMoveList(
        table: String,
        character: Character,
    ): Result<List<MoveResponseDto>, DataError.Remote> {
        return safeCall {
            httpClient.get(urlString = BASE_URL) {
                parameter("action", "cargoquery")
                parameter("tables", table)
                parameter("limit", LIMIT_MOVES)
                parameter("format", "json")
                parameter("fields", getMoveFields())
                parameter("where", "chara=\"${character.remoteQueryId}\"")
            }
        }
    }

    override suspend fun resolveCharacterImageUrls(
        characters: List<CharacterResponseDto>,
    ): Result<Map<String, String>, DataError.Remote> {
        val urlMap = characters.associate { characterDto ->
            val fileName = "RoA2_${characterDto.chara}_Portrait.png"
            fileName to buildImageUrl(fileName)
        }
        return Result.Success(urlMap)
    }

    override suspend fun resolveHitboxUrls(
        moves: List<MoveResponseDto>,
    ): Result<Map<String, String>, DataError.Remote> {
        val imageFileNames = moves.flatMap { moveDto ->
            val images = moveDto.image.orEmpty()
            val hitboxes = moveDto.hitbox.orEmpty()
            (images + hitboxes).map { it.trim() }.filter { it.isNotEmpty() }
        }.distinct()
        val urlMap = imageFileNames.associateWith { fileName ->
            buildImageUrl(fileName)
        }
        return Result.Success(urlMap)
    }

    private suspend fun resolveFileUrls(
        fileNames: List<String>,
    ): Result<Map<String, String>, DataError.Remote> {
        val pairs = coroutineScope {
            fileNames.map { fileName ->
                async {
                    val resolved = resolveOne(fileName)
                    fileName to resolved
                }
            }.awaitAll()
        }
        val map = pairs.mapNotNull { (name, url) ->
            if (url != null) name to url else null
        }.toMap()
        val result: Result<Map<String, String>, DataError.Remote> = Result.Success(map)
        return result
    }

    private suspend fun resolveOne(fileName: String): String? {
        return try {
            val response = redirectClient.head("$WIKI_BASE_URL/Special:Redirect/file/$fileName")
            val location = response.headers[HttpHeaders.Location]
            location
        } catch (e: Exception) {
            Napier.w(tag = "DragDown") { "Failed to resolve $fileName: ${e.message}" }
            null
        }
    }

    private fun getCharacterFields(): String {
        val allFields = listOf(
            "chara",
            "DacusSpeedMultiplier",
            "Weight",
            "FrictionGround",
            "FrictionAir",
            "DashFrames",
            "DashSpeed",
            "DashAcceleration",
            "RunSpeedMax",
            "RunTurnAcceleration",
            "RunTurnFrames",
            "WalkAccelerationMax",
            "WalkSpeedMax",
            "Gravity",
            "HitstunGravity",
            "FallSpeedMax",
            "FastFallSpeed",
            "AirAcceleration",
            "AirSpeedHorizontalMax",
            "JumpSpeedHorizontalMax",
            "FullHopSpeed",
            "ShortHopSpeed",
            "DoubleJumpSpeed",
            "DoubleJumpMaxHorizontalSpeed",
            "AirDodgeSpeed",
            "AirDodgeFriction",
            "RollSpeed",
            "ShieldSizeMultiplier",
            "LedgeStandSpeed",
            "LedgeRollSpeed",
            "LedgeJumpMaxHorizontalAirSpeed",
            "GetupRollSpeed",
            "TechRollSpeed",
            "WallJumpSpeedY",
            "WallJumpSpeedX",
        )
        return allFields.joinToString(",")
    }

    private fun getMoveFields(): String {
        val allFields = listOf(
            "chara",
            "attack",
            "attackID",
            "mode",
            "image",
            "hitbox",
            "caption",
            "hitboxCaption",
            "startup",
            "startupNotes",
            "totalActive",
            "totalActiveNotes",
            "endlag",
            "endlagNotes",
            "cancel",
            "cancelNotes",
            "landingLag",
            "landingLagNotes",
            "iasa",
            "iasaNotes",
            "totalDuration",
            "totalDurationNotes",
            "ledgeGrabFrame",
            "ledgeGrabFrameNotes",
            "frameChart",
            "hitID",
            "hitMoveID",
            "hitName",
            "hitActive",
            "customShieldSafety",
            "uniqueField",
            "articleID",
            "notes",
            "advNotes",
        )
        return allFields.joinToString(",")
    }

    private fun buildImageUrl(fileName: String): String {
        val url = "https://dragdown.wiki/wiki/Special:Redirect/file/$fileName"
        return url
    }
}

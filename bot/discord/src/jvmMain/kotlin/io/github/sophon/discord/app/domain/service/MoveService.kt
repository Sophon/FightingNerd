package io.github.sophon.discord.app.domain.service

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.core.util.equalsIgnoreCase
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.app.domain.model.BotError
import io.github.sophon.discord.app.domain.model.BotResponse
import io.github.sophon.discord.app.domain.model.CharacterId
import io.github.sophon.discord.app.domain.model.Command
import io.github.sophon.discord.app.domain.model.Emoji
import io.github.sophon.discord.app.domain.model.FrameRange
import io.github.sophon.discord.app.domain.model.MoveId
import io.github.sophon.discord.app.domain.model.MoveType
import io.github.sophon.discord.app.port.outbound.FrameDataPort
import io.github.sophon.discord.app.port.outbound.GetMovesInRangePort
import io.github.sophon.discord.app.port.outbound.GetMovesOfTypePort
import kotlin.time.Duration.Companion.seconds

internal interface MoveService {
    suspend fun findFrameData(query: String): Result<BotResponse.MoveResponse, BotError>

    suspend fun findMovesOfType(
        characterQuery: String,
        moveType: MoveType,
    ): Result<BotResponse.ListResponse, BotError>

    suspend fun findMovesInRange(
        query: String,
        command: Command,
    ): Result<BotResponse.ListResponse, BotError>

    suspend fun findStanceOrMove(query: String): Result<BotResponse.ListResponse, BotError>

    suspend fun findStrings(query: String): Result<BotResponse.ListResponse, BotError>

    suspend fun getMoves(characterId: CharacterId): Result<List<BotResponse.MoveResponse>, BotError>
}


internal class MoveServiceImpl(
    private val characterService: CharacterService,
    private val frameDataPort: FrameDataPort,
    private val getMovesOfTypePort: GetMovesOfTypePort,
    private val getMovesInRangePort: GetMovesInRangePort,
): MoveService {
    override suspend fun findFrameData(query: String): Result<BotResponse.MoveResponse, BotError> {
        val characterQuery = query.substringBefore(' ')
        val moveQuery = query.substringAfter(delimiter = " ", missingDelimiterValue = "")

        val result = characterService.findCharacter(characterQuery)
            .flatMap { character -> frameDataPort.getMoves(character.toCharacterId()) }
            .flatMap { moveList ->
                val move = moveList.firstOrNull { it.matches(moveQuery) }
                val moveResult = if (move == null) {
                    Result.Error(BotError.UnknownMove(characterQuery, moveQuery))
                } else {
                    Result.Success(move)
                }
                moveResult
            }
        return result
    }

    override suspend fun findMovesOfType(
        characterQuery: String,
        moveType: MoveType,
    ): Result<BotResponse.ListResponse, BotError> {
        val result = characterService.findCharacter(characterQuery)
            .flatMap { character ->
                val listResponse = getMovesOfTypePort.getMovesOfType(
                    characterId = character.toCharacterId(),
                    moveType = moveType,
                )
                    .map { moveList -> moveList.toListResponse(character, moveType) }
                listResponse
            }
        return result
    }

    /**
     * `[character] [min] [max]`
     */
    override suspend fun findMovesInRange(
        query: String,
        command: Command,
    ): Result<BotResponse.ListResponse, BotError> {
        val characterQuery = query.substringBefore(' ')
        val rangeQuery = query.substringAfter(' ', missingDelimiterValue = "").trim()

        val result = command.toFrameRangeType()
            .flatMap { rangeType -> rangeQuery.toFrameRange(rangeType) }
            .flatMap { frameRange ->
                val listResponse = characterService.findCharacter(characterQuery)
                    .flatMap { character ->
                        val rangeListResponse = getMovesInRangePort.getMovesInRange(
                            characterId = character.toCharacterId(),
                            frameRange = frameRange,
                        )
                            .map { moveList ->
                                val moveListResponse = moveList
                                    .distinctBy { it.input }
                                    .toListResponse(character = character, moveType = frameRange.toTitle(character))
                                moveListResponse
                            }
                        rangeListResponse
                    }
                listResponse
            }
        return result
    }

    override suspend fun findStanceOrMove(query: String): Result<BotResponse.ListResponse, BotError> {
        val characterQuery = query.substringBefore(' ')
        val stanceQuery = query.substringAfter(' ', missingDelimiterValue = "").trim()

        val result = if (stanceQuery.isEmpty()) {
            findStances(characterQuery = characterQuery)
        } else {
            findStanceMoves(characterQuery = characterQuery, stanceQuery = stanceQuery)
        }
        return result
    }

    override suspend fun findStrings(query: String): Result<BotResponse.ListResponse, BotError> {
        val characterQuery = query.substringBefore(' ')
        val prefix = query.substringAfter(' ', missingDelimiterValue = "").trim()

        val result = characterService.findCharacter(characterQuery)
            .flatMap { character ->
                val listResponse = frameDataPort.getMoves(character.toCharacterId())
                    .map { moveList ->
                        val followupListResponse = moveList
                            .filter { move ->
                                val isFollowedByPlus = (move.input.getOrNull(prefix.length) == '+')
                                move.input.startsWith(prefix, ignoreCase = true) && isFollowedByPlus.not()
                            }
                            .toListResponse(
                                character = character,
                                moveType = "${character.displayName} followups to $prefix",
                            )
                        followupListResponse
                    }
                listResponse
            }
        return result
    }

    override suspend fun getMoves(characterId: CharacterId): Result<List<BotResponse.MoveResponse>, BotError> {
        val result = frameDataPort.getMoves(characterId)
        return result
    }


    private suspend fun findStances(characterQuery: String): Result<BotResponse.ListResponse, BotError> {
        val result = characterService.findCharacter(characterQuery)
            .flatMap { character ->
                val listResponse = frameDataPort.getMoves(character.toCharacterId())
                    .map { moveList ->
                        val stances = moveList
                            .mapNotNull { it.stance }
                            .toSet()
                        val stanceListResponse = stances.toListResponse(character)
                        stanceListResponse
                    }
                listResponse
            }
        return result
    }

    private suspend fun findStanceMoves(
        characterQuery: String,
        stanceQuery: String,
    ): Result<BotResponse.ListResponse, BotError> {
        val result = characterService.findCharacter(characterQuery)
            .flatMap { character ->
                val listResponse = frameDataPort.getMoves(character.toCharacterId())
                    .map { moveList ->
                        val stanceMoveListResponse = moveList
                            .filter { it.stance.equalsIgnoreCase(stanceQuery) }
                            .toListResponse(character = character, moveType = stanceQuery)
                        stanceMoveListResponse
                    }
                listResponse
            }
        return result
    }


    private fun BotResponse.MoveResponse.matches(moveQuery: String): Boolean {
        return input.equalsIgnoreCase(moveQuery)
                || moveName.equalsIgnoreCase(moveQuery)
                || aliasList.any { it.equalsIgnoreCase(moveQuery) }
    }

    private fun BotResponse.CharacterResponse.toCharacterId(): CharacterId {
        val characterId = CharacterId(
            game = game,
            characterId = id,
        )

        return characterId
    }

    private fun List<BotResponse.MoveResponse>.toListResponse(
        character: BotResponse.CharacterResponse,
        moveType: MoveType,
    ): BotResponse.ListResponse {
        val buttonSet = mapIndexed { index, move ->
            BotResponse.EmbedButton(
                label = (index + 1).toString(),
                action = BotResponse.EmbedButton.Action.Query(
                    moveId = MoveId(
                        game = character.game,
                        characterId = character.id,
                        input = move.input,
                    ),
                ),
            )
        }
            .takeIf { it.isNotEmpty() }
            ?.let { buttonList ->
                BotResponse.ButtonSet(
                    buttonList = buttonList,
                    duration = EMBED_BUTTON_DURATION_INF.seconds,
                )
            }

        val listResponse = BotResponse.ListResponse(
            title = "${moveType.toEmoji()}${character.displayName.uppercase()} ${moveType.toTitle()} moves",
            values = map { it.input },
            dataSource = character.dataSource,
            buttonSet = buttonSet,
        )

        return listResponse
    }

    private fun List<BotResponse.MoveResponse>.toListResponse(
        character: BotResponse.CharacterResponse,
        moveType: String,
    ): BotResponse.ListResponse {
        val buttonSet = mapIndexed { index, move ->
            BotResponse.EmbedButton(
                label = (index + 1).toString(),
                action = BotResponse.EmbedButton.Action.Query(
                    moveId = MoveId(
                        game = character.game,
                        characterId = character.id,
                        input = move.input,
                    ),
                ),
            )
        }
            .takeIf { it.isNotEmpty() }
            ?.let { buttonList ->
                BotResponse.ButtonSet(
                    buttonList = buttonList,
                    duration = EMBED_BUTTON_DURATION_INF.seconds,
                )
            }

        val listResponse = BotResponse.ListResponse(
            title = "$moveType moves",
            values = map { it.input },
            dataSource = character.dataSource,
            buttonSet = buttonSet,
        )

        return listResponse
    }

    private fun Set<String>.toListResponse(
        character: BotResponse.CharacterResponse,
    ): BotResponse.ListResponse {
        val buttonSet = mapIndexed { index, stance ->
            BotResponse.EmbedButton(
                label = (index + 1).toString(),
                action = BotResponse.EmbedButton.Action.Command(
                    command = Command.Stance,
                    query = "${character.id} $stance",
                ),
            )
        }
            .takeIf { it.isNotEmpty() }
            ?.let { buttonList ->
                BotResponse.ButtonSet(
                    buttonList = buttonList,
                    duration = EMBED_BUTTON_DURATION_INF.seconds,
                )
            }

        val listResponse = BotResponse.ListResponse(
            title = "${character.displayName.uppercase()} stances",
            values = toList(),
            dataSource = character.dataSource,
            buttonSet = buttonSet,
        )

        return listResponse
    }

    /**
     * `[min] [max]`; a bound can be `inf` (aka `+inf`) or `-inf`. One bound is an exact match, two are sorted.
     */
    private fun String.toFrameRange(type: FrameRange.Type): Result<FrameRange, BotError> {
        val boundList = split(' ')
            .mapNotNull { it.toFrameBoundOrNull() }
            .take(2)
            .sorted()

        val result = if (boundList.isEmpty()) {
            Result.Error(BotError.InvalidQuery("SYNTAX: <character> <min> [max]: $this"))
        } else {
            Result.Success(FrameRange(type = type, from = boundList.first(), to = boundList.last()))
        }
        return result
    }

    private fun String.toFrameBoundOrNull(): Int? {
        val bound = when {
            equalsIgnoreCase("inf") || equalsIgnoreCase("+inf") -> Int.MAX_VALUE
            equalsIgnoreCase("-inf") -> Int.MIN_VALUE
            else -> toIntOrNull()
        }

        return bound
    }

    private fun FrameRange.toTitle(character: BotResponse.CharacterResponse): String {
        val title = "${character.displayName} ${type.toTitle()} [${from.toFormattedBound()} ; ${to.toFormattedBound()}]"

        return title
    }

    private fun Command.toFrameRangeType(): Result<FrameRange.Type, BotError> {
        val result = when (this) {
            Command.Startup -> Result.Success(FrameRange.Type.STARTUP)
            Command.OnHit -> Result.Success(FrameRange.Type.ON_HIT)
            Command.OnBlock -> Result.Success(FrameRange.Type.ON_BLOCK)
            Command.OnCounter -> Result.Success(FrameRange.Type.ON_COUNTER)
            else -> Result.Error(BotError.InvalidCommand(name))
        }

        return result
    }

    private fun FrameRange.Type.toTitle(): String {
        val title = when (this) {
            FrameRange.Type.STARTUP -> "Startup"
            FrameRange.Type.ON_HIT -> "On Hit"
            FrameRange.Type.ON_BLOCK -> "On Block"
            FrameRange.Type.ON_COUNTER -> "On Counter"
        }

        return title
    }

    private fun Int.toFormattedBound(): String {
        val formattedBound = when (this) {
            Int.MIN_VALUE -> "-INF"
            Int.MAX_VALUE -> "INF"
            else -> toString()
        }

        return formattedBound
    }

    private fun MoveType.toTitle(): String {
        val title = when (this) {
            MoveType.PC -> "Power Crush"
            MoveType.HEAT -> "Heat"
            MoveType.HOMING -> "Homing"
        }

        return title
    }

    private fun MoveType.toEmoji(): Emoji {
        val emoji = when (this) {
            MoveType.PC -> Emoji.TK_PC
            MoveType.HEAT -> Emoji.TK_HEAT
            MoveType.HOMING -> Emoji.TK_HOMING
        }

        return emoji
    }
}

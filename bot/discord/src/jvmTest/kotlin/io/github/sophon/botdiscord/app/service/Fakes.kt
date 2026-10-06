package io.github.sophon.botdiscord.app.service

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.FrameRange
import io.github.sophon.discord.app.model.GameList
import io.github.sophon.discord.app.model.UsageReport
import io.github.sophon.discord.app.model.UserRequest
import io.github.sophon.discord.app.model.discord.Command
import io.github.sophon.discord.app.model.discord.DiscordConfig
import io.github.sophon.discord.app.model.frameData.CharacterId
import io.github.sophon.discord.app.model.frameData.MoveId
import io.github.sophon.discord.app.model.frameData.MoveType
import io.github.sophon.discord.app.model.response.AliasResponse
import io.github.sophon.discord.app.model.response.BanResponse
import io.github.sophon.discord.app.model.response.BotResponse
import io.github.sophon.discord.app.model.response.CharacterResponse
import io.github.sophon.discord.app.model.response.CoreResponse
import io.github.sophon.discord.app.model.response.EwgfResponse
import io.github.sophon.discord.app.model.response.FeedbackResponse
import io.github.sophon.discord.app.model.response.GlossaryResponse
import io.github.sophon.discord.app.model.response.ListResponse
import io.github.sophon.discord.app.model.response.ModulesResponse
import io.github.sophon.discord.app.model.response.MoveResponse
import io.github.sophon.discord.app.model.response.PlainTextResponse
import io.github.sophon.discord.app.model.response.ReplyResponse
import io.github.sophon.discord.app.model.response.SteamLobbyResponse
import io.github.sophon.discord.app.model.response.UnbanResponse
import io.github.sophon.discord.app.outPort.CharactersPort
import io.github.sophon.discord.app.outPort.FrameDataPort
import io.github.sophon.discord.app.outPort.GamePort
import io.github.sophon.discord.app.outPort.GetMovesInRangePort
import io.github.sophon.discord.app.outPort.GetMovesOfTypePort
import io.github.sophon.discord.app.outPort.LoadConfigPort
import io.github.sophon.discord.app.outPort.StatsPort
import io.github.sophon.discord.app.service.AdminService
import io.github.sophon.discord.app.service.BanService
import io.github.sophon.discord.app.service.CharacterService
import io.github.sophon.discord.app.service.CommandRouterService
import io.github.sophon.discord.app.service.CoreBotService
import io.github.sophon.discord.app.service.EwgfService
import io.github.sophon.discord.app.service.GlossaryService
import io.github.sophon.discord.app.service.MoveService
import io.github.sophon.wiki.model.wiki.Game

//region ports
internal class FakeCharactersPort(
    private val characterList: List<CharacterResponse>,
): CharactersPort {
    override suspend fun getCharacters(): List<CharacterResponse> = characterList
}

internal class FakeGamePort(
    private val gameList: GameList = GameList(gameList = emptyList(), dataSource = wavuDataSource),
): GamePort {
    override suspend fun getGameList(): GameList = gameList
}

/**
 * Exact lookups only - the real port is keyed by [CharacterId] and [MoveId].
 */
internal class FakeFrameDataPort(
    private val moveMap: Map<CharacterId, List<MoveResponse>> = emptyMap(),
    private val error: BotError = BotError.WikiError("DatabaseError(wiki.db)"),
): FrameDataPort {
    override suspend fun getMoves(characterId: CharacterId): Result<List<MoveResponse>, BotError> {
        val moveList = moveMap[characterId]
        val result = if (moveList == null) {
            Result.Error(error)
        } else {
            Result.Success(moveList)
        }
        return result
    }

    override suspend fun getFrameData(moveId: MoveId): Result<MoveResponse, BotError> {
        val move = moveMap[CharacterId(game = moveId.game, characterId = moveId.characterId)]
            ?.firstOrNull { it.input == moveId.input }
        val result = if (move == null) {
            Result.Error(BotError.UnknownMove(moveId.characterId, moveId.input))
        } else {
            Result.Success(move)
        }
        return result
    }
}

internal class FakeGetMovesOfTypePort(
    private val result: Result<List<MoveResponse>, BotError> = Result.Success(emptyList()),
): GetMovesOfTypePort {
    val requestList = mutableListOf<Pair<CharacterId, MoveType>>()

    override suspend fun getMovesOfType(
        characterId: CharacterId,
        moveType: MoveType,
    ): Result<List<MoveResponse>, BotError> {
        requestList += (characterId to moveType)
        return result
    }
}

internal class FakeGetMovesInRangePort(
    private val result: Result<List<MoveResponse>, BotError> = Result.Success(emptyList()),
): GetMovesInRangePort {
    val requestList = mutableListOf<Pair<CharacterId, FrameRange>>()

    override suspend fun getMovesInRange(
        characterId: CharacterId,
        frameRange: FrameRange,
    ): Result<List<MoveResponse>, BotError> {
        requestList += (characterId to frameRange)
        return result
    }
}

internal class FakeStatsPort(
    private val configureResult: EmptyResult<BotError> = Result.Success(Unit),
    private val latestReportResult: Result<UsageReport?, BotError> = Result.Success(null),
): StatsPort {
    var configureCount = 0
        private set
    var failureCount = 0
        private set
    val registeredList = mutableListOf<Pair<Command, Game?>>()

    override suspend fun configure(): EmptyResult<BotError> {
        configureCount++
        return configureResult
    }

    override suspend fun register(command: Command, game: Game?): EmptyResult<BotError> {
        registeredList += (command to game)
        return Result.Success(Unit)
    }

    override suspend fun registerFailure(): EmptyResult<BotError> {
        failureCount++
        return Result.Success(Unit)
    }

    override suspend fun getLatestReport(): Result<UsageReport?, BotError> = latestReportResult
}

internal class FakeLoadConfigPort(
    private val result: Result<DiscordConfig, BotError>,
): LoadConfigPort {
    override fun load(): Result<DiscordConfig, BotError> = result
}
//endregion

//region services
/**
 * Every call is recorded as `name(arguments)` in [callList]; every call fails unless a result is given.
 */
internal class FakeMoveService(
    private val frameDataResult: Result<MoveResponse, BotError> = Result.Error(BotError.UnknownMove()),
    private val listResult: Result<ListResponse, BotError> = Result.Error(BotError.UnknownCharacter("")),
): MoveService {
    val callList = mutableListOf<String>()

    override suspend fun findFrameData(query: String): Result<MoveResponse, BotError> {
        callList += "findFrameData($query)"
        return frameDataResult
    }

    override suspend fun findMovesOfType(characterQuery: String, moveType: MoveType): Result<ListResponse, BotError> {
        callList += "findMovesOfType($characterQuery, $moveType)"
        return listResult
    }

    override suspend fun findMovesInRange(query: String, command: Command): Result<ListResponse, BotError> {
        callList += "findMovesInRange($query, ${command.name})"
        return listResult
    }

    override suspend fun findStanceOrMove(query: String): Result<ListResponse, BotError> {
        callList += "findStanceOrMove($query)"
        return listResult
    }

    override suspend fun findStrings(query: String): Result<ListResponse, BotError> {
        callList += "findStrings($query)"
        return listResult
    }

    override suspend fun getMoves(characterId: CharacterId): Result<List<MoveResponse>, BotError> {
        callList += "getMoves($characterId)"
        return Result.Error(BotError.NotImplemented("getMoves"))
    }
}

internal class FakeCharacterService(
    private val characterResult: Result<CharacterResponse, BotError> = Result.Error(BotError.UnknownCharacter("")),
    private val aliasResult: Result<AliasResponse, BotError> = Result.Error(BotError.UnsupportedGame("")),
): CharacterService {
    val callList = mutableListOf<String>()

    override suspend fun findCharacter(
        characterQuery: String,
        requireProperties: Boolean,
    ): Result<CharacterResponse, BotError> {
        callList += "findCharacter($characterQuery, $requireProperties)"
        return characterResult
    }

    override suspend fun findAliases(gameQuery: String): Result<AliasResponse, BotError> {
        callList += "findAliases($gameQuery)"
        return aliasResult
    }

    override suspend fun getCharacters(): List<CharacterResponse> {
        callList += "getCharacters()"
        return emptyList()
    }
}

internal class FakeCoreBotService: CoreBotService {
    val callList = mutableListOf<String>()

    override fun createTipResponse(): Result<CoreResponse, BotError> {
        callList += "createTipResponse()"
        return Result.Success(CoreResponse(type = CoreResponse.Type.Tip, dataSource = wavuDataSource))
    }

    override fun createHelpResponse(): Result<CoreResponse, BotError> {
        callList += "createHelpResponse()"
        return Result.Success(CoreResponse(type = CoreResponse.Type.Help, dataSource = wavuDataSource))
    }

    override fun createCommandsResponse(): Result<CoreResponse, BotError> {
        callList += "createCommandsResponse()"
        return Result.Success(CoreResponse(type = CoreResponse.Type.Commands, dataSource = wavuDataSource))
    }

    override fun createRepoResponse(): Result<PlainTextResponse, BotError> {
        callList += "createRepoResponse()"
        return Result.Success(PlainTextResponse(text = "repo"))
    }

    override fun createInviteResponse(): Result<PlainTextResponse, BotError> {
        callList += "createInviteResponse()"
        return Result.Success(PlainTextResponse(text = "invite"))
    }

    override fun createModulesResponse(): Result<ModulesResponse, BotError> {
        callList += "createModulesResponse()"
        return Result.Success(ModulesResponse(moduleList = emptyList(), dataSource = wavuDataSource))
    }

    override fun createSteamLobbyResponse(
        query: String,
        source: UserRequest.Source?,
    ): Result<SteamLobbyResponse, BotError> {
        callList += "createSteamLobbyResponse($query, $source)"
        return Result.Error(BotError.NotImplemented("createSteamLobbyResponse"))
    }
}

internal class FakeBanService: BanService {
    val callList = mutableListOf<String>()

    override suspend fun ban(query: String, source: UserRequest.Source?): Result<BanResponse, BotError> {
        callList += "ban($query, $source)"
        return Result.Error(BotError.NotImplemented("ban"))
    }

    override suspend fun unban(query: String, source: UserRequest.Source?): Result<UnbanResponse, BotError> {
        callList += "unban($query, $source)"
        return Result.Error(BotError.NotImplemented("unban"))
    }
}

internal class FakeAdminService: AdminService {
    val callList = mutableListOf<String>()

    override fun isAdmin(userId: String): Result<Boolean, BotError> {
        callList += "isAdmin($userId)"
        return Result.Success(false)
    }

    override suspend fun forwardFeedback(query: String, source: UserRequest.Source?): Result<FeedbackResponse, BotError> {
        callList += "forwardFeedback($query, $source)"
        return Result.Error(BotError.NotImplemented("forwardFeedback"))
    }

    override fun replyToFeedback(query: String, source: UserRequest.Source?): Result<ReplyResponse, BotError> {
        callList += "replyToFeedback($query, $source)"
        return Result.Error(BotError.NotImplemented("replyToFeedback"))
    }

    override fun refreshWiki(source: UserRequest.Source?): Result<PlainTextResponse, BotError> {
        callList += "refreshWiki($source)"
        return Result.Error(BotError.NotImplemented("refreshWiki"))
    }
}

internal class FakeEwgfService: EwgfService {
    val callList = mutableListOf<String>()

    override suspend fun performOperation(query: String, source: UserRequest.Source?): Result<EwgfResponse, BotError> {
        callList += "performOperation($query, $source)"
        return Result.Error(BotError.NotImplemented("performOperation"))
    }
}

internal class FakeGlossaryService: GlossaryService {
    val callList = mutableListOf<String>()

    override suspend fun findTerm(query: String): Result<GlossaryResponse, BotError> {
        callList += "findTerm($query)"
        return Result.Error(BotError.GlossaryTermNotFound(query))
    }
}

/**
 * A real router over recording service fakes.
 */
internal class RouterFixture(
    val moveService: FakeMoveService = FakeMoveService(),
    val characterService: FakeCharacterService = FakeCharacterService(),
    val coreBotService: FakeCoreBotService = FakeCoreBotService(),
    val banService: FakeBanService = FakeBanService(),
    val adminService: FakeAdminService = FakeAdminService(),
    val ewgfService: FakeEwgfService = FakeEwgfService(),
    val glossaryService: FakeGlossaryService = FakeGlossaryService(),
    val statsPort: FakeStatsPort = FakeStatsPort(),
) {
    val router = CommandRouterService(
        moveService = moveService,
        characterService = characterService,
        coreBotService = coreBotService,
        banService = banService,
        adminService = adminService,
        ewgfService = ewgfService,
        glossaryService = glossaryService,
        statsPort = statsPort,
    )

    /**
     * Calls of every service, in the order of the services above.
     */
    val callList: List<String>
        get() {
            val callList = (moveService.callList
                    + characterService.callList
                    + coreBotService.callList
                    + banService.callList
                    + adminService.callList
                    + ewgfService.callList
                    + glossaryService.callList)
            return callList
        }
}
//endregion

//region fixtures
internal val wavuDataSource = BotResponse.DataSource(
    name = "Tekken 8 (Wavu Wiki)",
    iconUrl = "https://wavu.wiki/android-chrome-512x512.png",
    color = 0x00095FB,
)

internal fun characterResponse(
    id: String,
    displayName: String,
    game: Game = Game.Tekken8,
    aliasList: List<String> = emptyList(),
    propertyList: List<BotResponse.Field> = emptyList(),
): CharacterResponse {
    val characterResponse = CharacterResponse(
        id = id,
        game = game,
        displayName = displayName,
        url = "https://wavu.wiki/t/$displayName",
        dataSource = wavuDataSource,
        aliasList = aliasList,
        propertyList = propertyList,
    )
    return characterResponse
}

internal fun moveResponse(
    input: String,
    moveName: String? = null,
    game: Game = Game.Tekken8,
    characterName: String = "Jin",
    primaryFields: List<BotResponse.Field> = emptyList(),
    aliasList: List<String> = emptyList(),
    stance: String? = null,
    buttonSet: BotResponse.ButtonSet? = null,
): MoveResponse {
    val moveResponse = MoveResponse(
        game = game,
        input = input,
        url = "https://wavu.wiki/t/${characterName}_movelist#$input",
        characterName = characterName,
        moveName = moveName,
        characterImageUrl = null,
        primaryFields = primaryFields,
        dataSource = wavuDataSource,
        aliasList = aliasList,
        stance = stance,
        buttonSet = buttonSet,
    )
    return moveResponse
}

internal fun discordConfigOf(
    featureList: List<DiscordConfig.Feature> = emptyList(),
    statsChannelIdList: List<String> = emptyList(),
): DiscordConfig {
    val discordConfig = DiscordConfig(
        featureList = featureList,
        adminConfig = DiscordConfig.AdminConfig(
            administratorIdList = listOf("111111111111111111"),
            feedbackChannelIdList = listOf("666666666666666666"),
            adminServerId = "777777777777777777",
        ),
        statsConfig = DiscordConfig.StatsConfig(isEnabled = true, statsChannelIdList = statsChannelIdList),
    )
    return discordConfig
}
//endregion

package io.github.sophon.botdiscord.feat.config.usecase

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isInstanceOf
import assertk.assertions.none
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.FeatureRepo
import io.github.sophon.core.featureConfig.model.Config
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.discord.feat.admin.AdminDiscordFeature
import io.github.sophon.discord.feat.admin.usecase.BanUseCase
import io.github.sophon.discord.feat.admin.usecase.CreateRedirectButtonsUseCase
import io.github.sophon.discord.feat.admin.usecase.ProcessFeedbackUseCase
import io.github.sophon.discord.feat.admin.usecase.RefreshDataUseCase
import io.github.sophon.discord.feat.admin.usecase.ReplyToFeedbackUseCase
import io.github.sophon.discord.feat.admin.usecase.StartAdminToolsUseCase
import io.github.sophon.discord.feat.admin.usecase.UnbanUseCase
import io.github.sophon.discord.feat.config.BotFeatureRepo
import io.github.sophon.discord.feat.config.usecase.BindToDiscordFeaturesUseCase
import io.github.sophon.discord.feat.core.domain.Scheduler
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.discord.feat.core.domain.model.Command
import io.github.sophon.discord.feat.core.domain.model.DiscordRegisteredFeature
import io.github.sophon.integration.AdminFeatureInfo
import io.github.sophon.integration.AdminTool
import io.github.sophon.integration.model.AdminError
import io.github.sophon.integration.model.AdminResult
import io.github.sophon.integration.model.Ban
import io.github.sophon.integration.model.Source
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import org.junit.Test

@OptIn(ExperimentalTime::class)
internal class BindToDiscordFeaturesUseCaseTest {
    @Test
    fun `usecase returns enabled features ordered by config with admin feature appended last`() {
        // given
        val config = configOf(
            Config.Feature(name = "EWGF", isEnabled = true, supportedGameList = emptyList()),
            Config.Feature(name = "Infil Glossary", isEnabled = true, supportedGameList = emptyList()),
            Config.Feature(name = "Wavu Wiki", isEnabled = true, supportedGameList = emptyList()),
            Config.Feature(name = "SuperCombo Wiki", isEnabled = true, supportedGameList = emptyList()),
        )
        val usecase = BindToDiscordFeaturesUseCase(allRegisteredFeatures, coreFeatureRepoFor(config), adminFeature)

        // when
        val result = usecase.invoke(config)

        // then
        assertThat(result).isInstanceOf(Result.Success::class)
        val features = (result as Result.Success).data
        assertThat(features).hasSize(5)
        assertThat(features[0]).isInstanceOf(FakeEwgfFeature::class)
        assertThat(features[1]).isInstanceOf(FakeInfilFeature::class)
        assertThat(features[2]).isInstanceOf(FakeWavuFeature::class)
        assertThat(features[3]).isInstanceOf(FakeSuperComboFeature::class)
        assertThat(features[4]).isInstanceOf(AdminDiscordFeature::class)
    }

    @Test
    fun `usecase excludes features that are disabled in config`() {
        // given
        val config = configOf(
            Config.Feature(name = "EWGF", isEnabled = true, supportedGameList = emptyList()),
            Config.Feature(name = "Infil Glossary", isEnabled = false, supportedGameList = emptyList()),
            Config.Feature(name = "Wavu Wiki", isEnabled = true, supportedGameList = emptyList()),
            Config.Feature(name = "SuperCombo Wiki", isEnabled = true, supportedGameList = emptyList()),
        )
        val usecase = BindToDiscordFeaturesUseCase(allRegisteredFeatures, coreFeatureRepoFor(config), adminFeature)

        // when
        val result = usecase.invoke(config)

        // then
        assertThat(result).isInstanceOf(Result.Success::class)
        val features = (result as Result.Success).data
        assertThat(features).hasSize(4)
        assertThat(features).none { it.isInstanceOf(FakeInfilFeature::class) }
    }

    @Test
    fun `usecase excludes registered features that are absent from config`() {
        // given
        val config = configOf(
            Config.Feature(name = "EWGF", isEnabled = true, supportedGameList = emptyList()),
        )
        val usecase = BindToDiscordFeaturesUseCase(allRegisteredFeatures, coreFeatureRepoFor(config), adminFeature)

        // when
        val result = usecase.invoke(config)

        // then
        assertThat(result).isInstanceOf(Result.Success::class)
        val features = (result as Result.Success).data
        assertThat(features).hasSize(2)
        assertThat(features[0]).isInstanceOf(FakeEwgfFeature::class)
        assertThat(features[1]).isInstanceOf(AdminDiscordFeature::class)
    }


    //region Test Doubles
    private class FakeEwgfFeature : DiscordRegisteredFeature {
        override val featureInfo = FeatureInfo(name = "EWGF", url = "", version = "1.0.0")
        override val defaultCommand: Command? = null
        override val otherCommands = emptyList<Command>()

        override suspend fun start() {}
        override suspend fun execute(command: Command, query: String, origin: Source, game: Game?): Result<BotOutput, BotError> =
            Result.Error(BotError.BotLogicError())
        override suspend fun refreshData(): EmptyResult<BotError> = Result.Success(Unit)
    }

    private class FakeInfilFeature : DiscordRegisteredFeature {
        override val featureInfo = FeatureInfo(name = "Infil Glossary", url = "", version = "1.0.0")
        override val defaultCommand: Command? = null
        override val otherCommands = emptyList<Command>()

        override suspend fun start() {}
        override suspend fun execute(command: Command, query: String, origin: Source, game: Game?): Result<BotOutput, BotError> =
            Result.Error(BotError.BotLogicError())
        override suspend fun refreshData(): EmptyResult<BotError> = Result.Success(Unit)
    }

    private class FakeWavuFeature : DiscordRegisteredFeature {
        override val featureInfo = FeatureInfo(name = "Wavu Wiki", url = "", version = "1.0.0")
        override val defaultCommand: Command? = null
        override val otherCommands = emptyList<Command>()

        override suspend fun start() {}
        override suspend fun execute(command: Command, query: String, origin: Source, game: Game?): Result<BotOutput, BotError> =
            Result.Error(BotError.BotLogicError())
        override suspend fun refreshData(): EmptyResult<BotError> = Result.Success(Unit)
    }

    private class FakeSuperComboFeature : DiscordRegisteredFeature {
        override val featureInfo = FeatureInfo(name = "SuperCombo Wiki", url = "", version = "1.0.0")
        override val defaultCommand: Command? = null
        override val otherCommands = emptyList<Command>()

        override suspend fun start() {}
        override suspend fun execute(command: Command, query: String, origin: Source, game: Game?): Result<BotOutput, BotError> =
            Result.Error(BotError.BotLogicError())
        override suspend fun refreshData(): EmptyResult<BotError> = Result.Success(Unit)
    }

    private val placeholderSource = Source(username = "", id = "", channelId = "")

    private val placeholderBan = Ban(
        offenderId = "",
        bannedAt = Instant.fromEpochMilliseconds(0),
        expiresAt = Instant.fromEpochMilliseconds(0),
        authorId = "",
        preventBotUsage = false,
    )

    private val fakeAdminTool = object : AdminTool {
        override fun getFeatureInfo() = FeatureInfo(name = "Admin", url = "", version = "1.0.0")
        override fun init(adminConfig: Config.AdminConfig): EmptyResult<AdminError> = Result.Success(Unit)
        override suspend fun processFeedback(origin: Source, feedback: String): Result<AdminResult, AdminError> =
            Result.Success(AdminResult(source = placeholderSource))
        override fun replyToFeedback(origin: Source, target: Source, reply: String): Result<AdminResult, AdminError> =
            Result.Success(AdminResult(source = placeholderSource))
        override suspend fun banUser(
            origin: Source,
            offenderId: String,
            duration: Duration,
            preventBotUsage: Boolean,
        ): Result<Ban, AdminError> = Result.Success(placeholderBan)
        override suspend fun unbanUser(origin: Source, offenderId: String): EmptyResult<AdminError> = Result.Success(Unit)
        override suspend fun updateUserPenalty(
            source: Source,
            offenderId: String,
            duration: Duration,
            preventBotUsage: Boolean,
        ): Result<Ban, AdminError> = Result.Success(placeholderBan)
        override suspend fun cleanExpiredBans(): EmptyResult<AdminError> = Result.Success(Unit)
    }

    private val fakeBotFeatureRepo = object : BotFeatureRepo {
        override suspend fun initialize(): EmptyResult<BotError> = Result.Success(Unit)
        override fun getFeatures(): List<DiscordRegisteredFeature> = emptyList()
    }
    //endregion

    //region Setup
    private val ewgfFeature = FakeEwgfFeature()
    private val infilFeature = FakeInfilFeature()
    private val wavuFeature = FakeWavuFeature()
    private val superComboFeature = FakeSuperComboFeature()

    // registration order deliberately differs from config order, to prove sorting follows config
    private val allRegisteredFeatures = listOf(superComboFeature, wavuFeature, infilFeature, ewgfFeature)

    private val adminFeature = AdminDiscordFeature(
        adminFeatureInfo = AdminFeatureInfo,
        adminConfig = Config.AdminConfig(
            administratorIdList = listOf("administratorIdList"),
            feedbackChannelIdList = listOf("feedbackChannelIdList"),
            adminServerId = "adminServerId",
        ),
        startAdminToolsUseCase = StartAdminToolsUseCase(fakeAdminTool),
        processFeedbackUseCase = ProcessFeedbackUseCase(fakeAdminTool),
        replyToFeedbackUseCase = ReplyToFeedbackUseCase(fakeAdminTool),
        createRedirectButtonsUseCase = CreateRedirectButtonsUseCase(),
        banUseCase = BanUseCase(fakeAdminTool),
        unbanUseCase = UnbanUseCase(fakeAdminTool),
        refreshDataUseCase = RefreshDataUseCase(lazy { fakeBotFeatureRepo }),
        scheduler = Scheduler(),
        scope = CoroutineScope(Dispatchers.Unconfined),
        featureRepo = lazy { fakeBotFeatureRepo },
    )

    private fun configOf(vararg features: Config.Feature): Config {
        val result = Config(
            featureList = features.toList(),
            adminConfig = Config.AdminConfig(
                administratorIdList = listOf("administratorIdList"),
                feedbackChannelIdList = listOf("feedbackChannelIdList"),
                adminServerId = "adminServerId",
            ),
            statsConfig = Config.StatsConfig(
                isEnabled = true,
                statsChannelIdList = listOf("statsChannelIdList"),
            ),
        )
        return result
    }

    private fun coreFeatureRepoFor(config: Config): FeatureRepo {
        val enabledNames = config.featureList
            .filter { it.isEnabled }
            .map { it.name }
            .toSet()
        return object : FeatureRepo {
            override fun initialize(config: Config): EmptyResult<WikiError> = Result.Success(Unit)
            override fun getGameClients(): Map<Game, WikiClient> = emptyMap()
            override fun getOtherFeatures(): List<Config.Feature> = emptyList()
            override fun getEnabledFeatureNames(): Set<String> = enabledNames
            override fun getWikiClientFor(game: Game): WikiClient? = null
        }
    }
    //endregion
}

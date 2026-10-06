package io.github.sophon.discord

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import dev.kord.core.Kord
import io.github.aakira.napier.Napier
import io.github.sophon.adminModule
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.coreModule
import io.github.sophon.core.featureConfig.model.WikiClientFeature
import io.github.sophon.core.wiki.data.fingerprint
import io.github.sophon.core.wiki.data.readStoredFingerprint
import io.github.sophon.core.wiki.data.storeFingerprint
import io.github.sophon.discord.adapter.inbound.kord.CommandRegistry
import io.github.sophon.discord.adapter.inbound.kord.DiscordBot
import io.github.sophon.discord.adapter.inbound.kord.DiscordBotImpl
import io.github.sophon.discord.adapter.inbound.kord.DiscordButtonBuilder
import io.github.sophon.discord.adapter.inbound.kord.KordResponder
import io.github.sophon.discord.adapter.inbound.scheduler.GlossaryScheduler
import io.github.sophon.discord.adapter.inbound.scheduler.ReportScheduler
import io.github.sophon.discord.adapter.inbound.scheduler.Scheduler
import io.github.sophon.discord.adapter.inbound.scheduler.WikiScheduler
import io.github.sophon.discord.adapter.outbound.admin.AdminAdapter
import io.github.sophon.discord.adapter.outbound.config.ConfigAdapter
import io.github.sophon.discord.adapter.outbound.ewgf.EwgfAdapter
import io.github.sophon.discord.adapter.outbound.featureInfo.FeatureInfoAdapter
import io.github.sophon.discord.adapter.outbound.glossary.GlossaryAdapter
import io.github.sophon.discord.adapter.outbound.kord.KordPostAdapter
import io.github.sophon.discord.adapter.outbound.stats.StatsAdapter
import io.github.sophon.discord.adapter.outbound.wiki.WikiAdapter
import io.github.sophon.discord.app.model.discord.DiscordConfig
import io.github.sophon.discord.app.outPort.AdminPort
import io.github.sophon.discord.app.outPort.BanPort
import io.github.sophon.discord.app.outPort.CharactersPort
import io.github.sophon.discord.app.outPort.ConfigureAdminPort
import io.github.sophon.discord.app.outPort.ConfigureWikiPort
import io.github.sophon.discord.app.outPort.EwgfPort
import io.github.sophon.discord.app.outPort.FeatureInfoPort
import io.github.sophon.discord.app.outPort.ForwardPort
import io.github.sophon.discord.app.outPort.FrameDataPort
import io.github.sophon.discord.app.outPort.GamePort
import io.github.sophon.discord.app.outPort.GetMovesInRangePort
import io.github.sophon.discord.app.outPort.GetMovesOfTypePort
import io.github.sophon.discord.app.outPort.GlossaryPort
import io.github.sophon.discord.app.outPort.LoadConfigPort
import io.github.sophon.discord.app.outPort.PostReportPort
import io.github.sophon.discord.app.outPort.ReadFilePort
import io.github.sophon.discord.app.outPort.RefreshGlossaryPort
import io.github.sophon.discord.app.outPort.RefreshWikiPort
import io.github.sophon.discord.app.outPort.StatsPort
import io.github.sophon.discord.app.service.AdminService
import io.github.sophon.discord.app.service.AdminServiceImpl
import io.github.sophon.discord.app.service.BanService
import io.github.sophon.discord.app.service.BanServiceImpl
import io.github.sophon.discord.app.service.CharacterService
import io.github.sophon.discord.app.service.CharacterServiceImpl
import io.github.sophon.discord.app.service.CommandRouterService
import io.github.sophon.discord.app.service.CoreBotService
import io.github.sophon.discord.app.service.CoreBotServiceImpl
import io.github.sophon.discord.app.service.EwgfService
import io.github.sophon.discord.app.service.EwgfServiceImpl
import io.github.sophon.discord.app.service.GlossaryService
import io.github.sophon.discord.app.service.GlossaryServiceImpl
import io.github.sophon.discord.app.service.MoveService
import io.github.sophon.discord.app.service.MoveServiceImpl
import io.github.sophon.discord.app.service.PostDailyReportService
import io.github.sophon.discord.app.service.ProcessButtonEventService
import io.github.sophon.discord.app.service.ProcessUserInputService
import io.github.sophon.discord.app.service.ProduceAutoCompleteService
import io.github.sophon.discord.app.service.RefreshGlossaryService
import io.github.sophon.discord.app.service.RefreshWikiService
import io.github.sophon.discord.app.service.StartFeaturesService
import io.github.sophon.discord.inPort.PostDailyReportUseCase
import io.github.sophon.discord.inPort.ProcessButtonEventUseCase
import io.github.sophon.discord.inPort.ProcessUserInputUseCase
import io.github.sophon.discord.inPort.ProduceAutoCompleteUseCase
import io.github.sophon.discord.inPort.RefreshGlossaryUseCase
import io.github.sophon.discord.inPort.RefreshWikiUseCase
import io.github.sophon.discord.inPort.StartFeaturesUseCase
import io.github.sophon.ewgfModule
import io.github.sophon.glossaryinfil.infilModule
import io.github.sophon.statsModule
import io.github.sophon.wiki.wikiModule
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module
import java.io.File

internal fun initKoin(
    kord: Kord,
    config: KoinAppDeclaration? = null
) = startKoin {
    config?.invoke(this)

    modules(
        coreModule,
        dcBotModule(kord),
        adminModule(),
        statsModule(
            directory = System.getenv(ENV_STATS_DIR).orEmpty().ifEmpty { LOCAL_STATS_DIR },
        ),

        infilModule(
            databaseDirectory = System.getenv(ENV_WIKI_DATABASE_DIR).orEmpty().ifEmpty { LOCAL_DATABASE_DIR },
        ),
        ewgfModule(
            apiToken = System.getenv(ENV_API_EWGF).orEmpty()
        ),
        wikiModule(
            databaseDirectory = System.getenv(ENV_WIKI_DATABASE_DIR).orEmpty().ifEmpty { LOCAL_DATABASE_DIR },
        ),
    )
}

internal fun dcBotModule(kord: Kord) = module {
    single {
        CoroutineScope(
            SupervisorJob() +
                    Dispatchers.Default +
                    CoroutineExceptionHandler { _, throwable ->
                        Napier.e(tag = "Kord") { "Unhandled exception: $throwable" }
                    }
        )
    }
    single { kord }

    singleOf(::DiscordButtonBuilder)
    singleOf(::CommandRegistry)

    singleOf(::Scheduler)

    WikiClientFeature.entries.forEach { feature ->
        single<SqlDriver>(named(feature.id)) { params ->
            val schema = params.get<SqlSchema<QueryResult.Value<Unit>>>()
            val databaseDir = System.getenv(ENV_WIKI_DATABASE_DIR).orEmpty().ifEmpty { LOCAL_DATABASE_DIR }
            val databaseFile = File(databaseDir, "${feature.id}.db")
            databaseFile.parentFile?.mkdirs()

            // Clean up the legacy version file — fingerprint replaces it.
            File(databaseDir, "${feature.id}.db.version").delete()

            openFingerprintedDriver(schema, databaseFile)
        }
    }

    //region Kord
    singleOf(::DiscordBotImpl).bind<DiscordBot>()
    singleOf(::KordResponder)
    //endregion

    //region Services
    singleOf(::StartFeaturesService).bind<StartFeaturesUseCase>()
    singleOf(::ProcessUserInputService).bind<ProcessUserInputUseCase>()
    singleOf(::ProcessButtonEventService).bind<ProcessButtonEventUseCase>()
    singleOf(::CommandRouterService)
    singleOf(::CharacterServiceImpl).bind<CharacterService>()
    singleOf(::CoreBotServiceImpl).bind<CoreBotService>()
    singleOf(::MoveServiceImpl).bind<MoveService>()
    singleOf(::ProduceAutoCompleteService).bind<ProduceAutoCompleteUseCase>()
    singleOf(::BanServiceImpl).bind<BanService>()
    singleOf(::AdminServiceImpl).bind<AdminService>()
    singleOf(::EwgfServiceImpl).bind<EwgfService>()
    singleOf(::PostDailyReportService).bind<PostDailyReportUseCase>()
    singleOf(::RefreshWikiService).bind<RefreshWikiUseCase>()
    singleOf(::GlossaryServiceImpl).bind<GlossaryService>()
    singleOf(::RefreshGlossaryService).bind<RefreshGlossaryUseCase>()
    //endregion

    //region Admin
    singleOf(::AdminAdapter) {
        bind<ConfigureAdminPort>()
        bind<AdminPort>()
        bind<BanPort>()
    }
    //endregion

    //region Config
    singleOf(::ConfigAdapter) {
        bind<ReadFilePort>()
        bind<LoadConfigPort>()
    }
    singleOf(::FeatureInfoAdapter).bind<FeatureInfoPort>()
    single<DiscordConfig.AdminConfig> {
        when (val result = get<LoadConfigPort>().load()) {
            is Result.Success -> result.data.adminConfig
            is Result.Error -> error("Failed to load config: ${result.error}")
        }
    }
    //endregion

    //region Stats
    singleOf(::StatsAdapter).bind<StatsPort>()
    singleOf(::KordPostAdapter) {
        bind<PostReportPort>()
        bind<ForwardPort>()
    }
    singleOf(::ReportScheduler)
    //endregion

    //region Wiki
    singleOf(::WikiAdapter) {
        bind<ConfigureWikiPort>()
        bind<RefreshWikiPort>()
        bind<FrameDataPort>()
        bind<GetMovesOfTypePort>()
        bind<GetMovesInRangePort>()
        bind<CharactersPort>()
        bind<GamePort>()
    }
    singleOf(::WikiScheduler)
    //endregion

    //region EWGF
    singleOf(::EwgfAdapter).bind<EwgfPort>()
    //endregion

    //region Glossary
    singleOf(::GlossaryAdapter) {
        bind<GlossaryPort>()
        bind<RefreshGlossaryPort>()
    }
    singleOf(::GlossaryScheduler)
    //endregion
}

private fun openFingerprintedDriver(
    schema: SqlSchema<QueryResult.Value<Unit>>,
    databaseFile: File,
): SqlDriver {
    val expected = schema.fingerprint()
    val wasFresh = databaseFile.exists().not()

    val driver = JdbcSqliteDriver("jdbc:sqlite:${databaseFile.absolutePath}")
    if (wasFresh) {
        schema.create(driver)
        driver.storeFingerprint(expected)
        return driver
    }

    val stored = try {
        driver.readStoredFingerprint()
    } catch (t: Throwable) {
        null
    }
    if (stored == expected) return driver

    driver.close()
    databaseFile.delete()
    val fresh = JdbcSqliteDriver("jdbc:sqlite:${databaseFile.absolutePath}")
    schema.create(fresh)
    fresh.storeFingerprint(expected)
    return fresh
}

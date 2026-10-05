package io.github.sophon.discord

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import dev.kord.core.Kord
import io.github.aakira.napier.Napier
import io.github.sophon.core.coreModule
import io.github.sophon.core.featureConfig.model.WikiClientFeature
import io.github.sophon.core.wiki.data.fingerprint
import io.github.sophon.core.wiki.data.readStoredFingerprint
import io.github.sophon.core.wiki.data.storeFingerprint
import io.github.sophon.discord.adapter.inbound.kord.DiscordButtonBuilder
import io.github.sophon.discord.adapter.inbound.kord.KordResponder
import io.github.sophon.discord.adapter.outbound.config.ConfigAdapter
import io.github.sophon.discord.adapter.outbound.wiki.WikiAdapter
import io.github.sophon.discord.app.domain.service.CharacterService
import io.github.sophon.discord.app.domain.service.CharacterServiceImpl
import io.github.sophon.discord.app.domain.service.CommandRouterService
import io.github.sophon.discord.app.domain.service.CoreBotService
import io.github.sophon.discord.app.domain.service.CoreBotServiceImpl
import io.github.sophon.discord.app.domain.service.MoveService
import io.github.sophon.discord.app.domain.service.MoveServiceImpl
import io.github.sophon.discord.app.domain.service.ProcessButtonEventService
import io.github.sophon.discord.app.domain.service.ProcessUserInputService
import io.github.sophon.discord.app.domain.service.ProduceAutoCompleteService
import io.github.sophon.discord.app.domain.service.StartFeaturesService
import io.github.sophon.discord.app.port.inbound.ProcessButtonEventUseCase
import io.github.sophon.discord.app.port.inbound.ProcessUserInputUseCase
import io.github.sophon.discord.app.port.inbound.ProduceAutoCompleteUseCase
import io.github.sophon.discord.app.port.inbound.StartFeaturesUseCase
import io.github.sophon.discord.app.port.outbound.CharactersPort
import io.github.sophon.discord.app.port.outbound.ConfigureWikiPort
import io.github.sophon.discord.app.port.outbound.FrameDataPort
import io.github.sophon.discord.app.port.outbound.GetMovesInRangePort
import io.github.sophon.discord.app.port.outbound.GetMovesOfTypePort
import io.github.sophon.discord.app.port.outbound.LoadConfigPort
import io.github.sophon.discord.app.port.outbound.ReadFilePort
import io.github.sophon.discord.app.port.outbound.RefreshWikiPort
import io.github.sophon.discord.feat.core.data.FileManager
import io.github.sophon.discord.feat.core.data.InMemoryGlossaryDB
import io.github.sophon.discord.feat.core.data.JsonReportRepo
import io.github.sophon.discord.feat.core.domain.CommandRegistry
import io.github.sophon.discord.feat.featureRegistryModule
import io.github.sophon.glossaryinfil.integration.data.GlossaryDB
import io.github.sophon.glossaryinfil.integration.infilModule
import io.github.sophon.adminModule
import io.github.sophon.integration.data.ReportRepo
import io.github.sophon.integration.ewgfModule
import io.github.sophon.integration.statsModule
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
        statsModule(),

        infilModule,
        ewgfModule(
            apiToken = System.getenv(ENV_API_EWGF).orEmpty()
        ),
        wikiModule(
            databaseDirectory = System.getenv(ENV_WIKI_DATABASE_DIR).orEmpty().ifEmpty { LOCAL_DATABASE_DIR },
        ),

        featureRegistryModule,
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

    singleOf(::InMemoryGlossaryDB).bind<GlossaryDB>()

    singleOf(::FileManager)
    singleOf(::JsonReportRepo).bind<ReportRepo>()

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
    //endregion

    //region Config
    singleOf(::ConfigAdapter) {
        bind<ReadFilePort>()
        bind<LoadConfigPort>()
    }
    //endregion

    //region Wiki
    singleOf(::WikiAdapter) {
        bind<ConfigureWikiPort>()
        bind<RefreshWikiPort>()
        bind<FrameDataPort>()
        bind<GetMovesOfTypePort>()
        bind<GetMovesInRangePort>()
        bind<CharactersPort>()
    }
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

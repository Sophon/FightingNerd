package io.github.sophon.discord

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import dev.kord.core.Kord
import io.github.sophon.core.featureConfig.model.WikiClientFeature
import org.koin.core.qualifier.named
import io.github.aakira.napier.Napier
import io.github.sophon.integration.adminModule
import io.github.sophon.core.coreModule
import io.github.sophon.integration.data.ReportRepo
import io.github.sophon.discord.feat.core.data.FileManager
import io.github.sophon.discord.feat.core.data.InMemoryGlossaryDB
import io.github.sophon.discord.feat.core.data.JsonReportRepo
import io.github.sophon.discord.feat.core.domain.DiscordButtonBuilder
import io.github.sophon.discord.feat.featureRegistryModule
import io.github.sophon.dreamcancel.integration.dreamCancelModule
import io.github.sophon.integration.ewgfModule
import io.github.sophon.glossaryinfil.integration.data.GlossaryDB
import io.github.sophon.glossaryinfil.integration.infilModule
import io.github.sophon.integration.statsModule
import io.github.sophon.wikiSuperCombo.integration.superComboModule
import io.github.sophon.wikidragdown.integration.dragDownModule
import io.github.sophon.wikidustloop.integration.dustLoopModule
import io.github.sophon.wikimizuumi.integration.mizuumiModule
import io.github.sophon.wikiwavu.integration.wavuModule
import io.github.sophon.xko.integration.xkoModule
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

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
        wavuModule(),
        superComboModule(),
        xkoModule(),
        dreamCancelModule(),
        dustLoopModule(),
        mizuumiModule(),
        dragDownModule(),
        ewgfModule(
            apiToken = System.getenv(ENV_API_EWGF).orEmpty()
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

    singleOf(::InMemoryGlossaryDB).bind<GlossaryDB>()

    singleOf(::FileManager)
    singleOf(::JsonReportRepo).bind<ReportRepo>()

    WikiClientFeature.entries.forEach { feature ->
        single<SqlDriver>(named(feature.id)) { JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY) }
    }
}

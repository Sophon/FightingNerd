package io.github.sophon.discord

import io.github.sophon.core.coreModule
import io.github.sophon.discord.config.ConfigLoader
import io.github.sophon.discord.data.InMemoryGlossaryDB
import io.github.sophon.discord.data.InMemoryMoveListDB
import io.github.sophon.discord.domain.usecase.DownloadDataUseCase
import io.github.sophon.discord.domain.usecase.GetHeatMovesUseCase
import io.github.sophon.discord.domain.usecase.GetHomingMovesUseCase
import io.github.sophon.discord.domain.usecase.GetPowerCrushMovesUseCase
import io.github.sophon.discord.domain.usecase.SearchFrameDataUseCase
import io.github.sophon.discord.domain.usecase.SearchGlossaryUseCase
import io.github.sophon.discord.domain.usecase.StartGlossaryUseCase
import io.github.sophon.discord.featureRegistry.DiscordRegisteredFeature
import io.github.sophon.discord.featureRegistry.FeatureRegistry
import io.github.sophon.discord.featureRegistry.featureRegistryModule
import io.github.sophon.discord.featureRegistry.infilGlossary.InfilGlossaryDiscordFeature
import io.github.sophon.discord.featureRegistry.wikiWavu.WavuWikiDiscordFeature
import io.github.sophon.discord.featureRegistry.wikiWavu.Scheduler
import io.github.sophon.discord.featureRegistry.wikiWavu.FileReaderJVM
import io.github.sophon.glossaryinfil.data.GlossaryDB
import io.github.sophon.glossaryinfil.infilModule
import io.github.sophon.wikiwavu.data.MoveListDB
import io.github.sophon.wikiwavu.infrastructure.FileReader
import io.github.sophon.wikiwavu.wavuModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

fun initKoin(
    apiKey: String,
    config: KoinAppDeclaration? = null
) = startKoin {
    config?.invoke(this)

    modules(
        coreModule,
        dcBotModule(apiKey),

        infilModule,
        wavuModule,

        featureRegistryModule,
    )
}

fun dcBotModule(apiKey: String) = module {
    single { apiKey }
    single {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    singleOf(::DiscordBotImpl).bind<DiscordBot>()

    singleOf(::InMemoryMoveListDB).bind<MoveListDB>()
    singleOf(::InMemoryGlossaryDB).bind<GlossaryDB>()
}
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
import io.github.sophon.discord.featureRegistry.infilGlossary.GlossaryFeatureDiscord
import io.github.sophon.discord.featureRegistry.wikiWavu.DiscordWavuWikiFeature
import io.github.sophon.discord.featureRegistry.wikiWavu.Scheduler
import io.github.sophon.discord.infrastructure.FileReaderJVM
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
    )
}

fun dcBotModule(apiKey: String) = module {
    single { apiKey }
    single {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    singleOf(::DiscordBotImpl).bind<DiscordBot>()

    singleOf(::StartGlossaryUseCase)
    singleOf(::SearchGlossaryUseCase)
    singleOf(::SearchFrameDataUseCase)
    singleOf(::GetPowerCrushMovesUseCase)
    singleOf(::GetHeatMovesUseCase)
    singleOf(::GetHomingMovesUseCase)
    singleOf(::DownloadDataUseCase)

    singleOf(::InMemoryMoveListDB).bind<MoveListDB>()
    singleOf(::InMemoryGlossaryDB).bind<GlossaryDB>()

    //region FEATURES
    single { FeatureRegistry(getAll()) }

    singleOf(::DiscordWavuWikiFeature).bind<DiscordRegisteredFeature>()
    singleOf(::GlossaryFeatureDiscord).bind<DiscordRegisteredFeature>()

    singleOf(::ConfigLoader)
    single<List<DiscordRegisteredFeature>> {
        val config = get<ConfigLoader>().loadConfig()
        val registry = get<FeatureRegistry>()
        val enabledFeatures = config.featureList
            .filter { it.isEnabled }
            .map { it.name }
        registry.getFeatures(enabledFeatures)
    }
    //endregion

    singleOf(::FileReaderJVM).bind<FileReader>()
    singleOf(::Scheduler)
}
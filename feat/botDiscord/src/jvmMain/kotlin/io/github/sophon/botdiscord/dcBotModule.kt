package io.github.sophon.botdiscord

import io.github.sophon.botdiscord.data.InMemoryGlossaryDB
import io.github.sophon.botdiscord.data.InMemoryMoveListDB
import io.github.sophon.botdiscord.domain.usecase.DownloadDataUseCase
import io.github.sophon.botdiscord.domain.usecase.GetHeatMovesUseCase
import io.github.sophon.botdiscord.domain.usecase.GetHomingMovesUseCase
import io.github.sophon.botdiscord.domain.usecase.GetPowerCrushMovesUseCase
import io.github.sophon.botdiscord.domain.usecase.SearchFrameDataUseCase
import io.github.sophon.botdiscord.domain.usecase.SearchGlossaryUseCase
import io.github.sophon.botdiscord.domain.usecase.StartGlossaryUseCase
import io.github.sophon.botdiscord.featureRegistry.infilGlossary.GlossaryFeatureDiscord
import io.github.sophon.botdiscord.featureRegistry.wikiWavu.DiscordWavuWikiFeature
import io.github.sophon.botdiscord.featureRegistry.wikiWavu.Scheduler
import io.github.sophon.botdiscord.infrastructure.FileReaderJVM
import io.github.sophon.core.coreModule
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

    singleOf(::DiscordWavuWikiFeature)
    singleOf(::GlossaryFeatureDiscord)

    singleOf(::FileReaderJVM).bind<FileReader>()
    singleOf(::Scheduler)
}
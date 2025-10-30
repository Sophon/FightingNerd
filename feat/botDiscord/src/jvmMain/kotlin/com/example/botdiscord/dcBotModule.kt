package com.example.botdiscord

import com.example.botdiscord.data.InMemoryGlossaryDB
import com.example.botdiscord.data.InMemoryMoveListDB
import com.example.botdiscord.domain.usecase.DownloadMoveListUseCase
import com.example.botdiscord.domain.usecase.GetHeatMovesUseCase
import com.example.botdiscord.domain.usecase.GetHomingMovesUseCase
import com.example.botdiscord.domain.usecase.GetPowerCrushMovesUseCase
import com.example.botdiscord.domain.usecase.SearchFrameDataUseCase
import com.example.botdiscord.domain.usecase.SearchGlossaryUseCase
import com.example.botdiscord.domain.usecase.StartGlossaryUseCase
import com.example.botdiscord.featureRegistry.GlossaryFeature
import com.example.botdiscord.featureRegistry.frameData.FrameDataFeature
import com.example.botdiscord.featureRegistry.frameData.Scheduler
import com.example.botdiscord.infrastructure.FileReaderJVM
import com.example.core.coreModule
import com.example.glossaryinfil.data.GlossaryDB
import com.example.glossaryinfil.infilModule
import com.example.wikiwavu.data.MoveListDB
import com.example.wikiwavu.infrastructure.FileReader
import com.example.wikiwavu.wavuModule
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
    singleOf(::DownloadMoveListUseCase)

    singleOf(::InMemoryMoveListDB).bind<MoveListDB>()
    singleOf(::InMemoryGlossaryDB).bind<GlossaryDB>()

    singleOf(::FrameDataFeature)
    singleOf(::GlossaryFeature)

    singleOf(::FileReaderJVM).bind<FileReader>()
    singleOf(::Scheduler)
}
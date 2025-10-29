package com.example.botdiscord

import com.example.botdiscord.data.InMemoryGlossaryDB
import com.example.botdiscord.data.InMemoryMoveListDB
import com.example.botdiscord.infrastructure.FileReaderJVM
import com.example.core.coreModule
import com.example.glossaryinfil.data.GlossaryDB
import com.example.glossaryinfil.infilModule
import com.example.wikiwavu.data.MoveListDB
import com.example.wikiwavu.wavuModule
import com.example.botdiscord.featureRegistry.FrameDataFeature
import com.example.botdiscord.featureRegistry.GlossaryFeature
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module
import com.example.botdiscord.usecase.GetHeatMovesUseCase
import com.example.botdiscord.usecase.GetHomingMovesUseCase
import com.example.botdiscord.usecase.GetPowerCrushMovesUseCase
import com.example.botdiscord.usecase.SearchFrameDataUseCase
import com.example.botdiscord.usecase.SearchGlossaryUseCase
import com.example.botdiscord.usecase.StartGlossaryUseCase
import com.example.botdiscord.usecase.StartWikiUseCase
import com.example.wikiwavu.infrastructure.FileReader

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

    singleOf(::DiscordBotImpl).bind<DiscordBot>()

    singleOf(::StartGlossaryUseCase)
    singleOf(::SearchGlossaryUseCase)
    singleOf(::StartWikiUseCase)
    singleOf(::SearchFrameDataUseCase)
    singleOf(::GetPowerCrushMovesUseCase)
    singleOf(::GetHeatMovesUseCase)
    singleOf(::GetHomingMovesUseCase)

    singleOf(::InMemoryMoveListDB).bind<MoveListDB>()
    singleOf(::InMemoryGlossaryDB).bind<GlossaryDB>()

    singleOf(::FrameDataFeature)
    singleOf(::GlossaryFeature)

    singleOf(::FileReaderJVM).bind<FileReader>()
}
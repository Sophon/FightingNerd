package io.github.sophon.botdiscord

import io.github.sophon.botdiscord.data.InMemoryGlossaryDB
import io.github.sophon.botdiscord.data.InMemoryMoveListDB
import io.github.sophon.botdiscord.domain.usecase.DownloadMoveListUseCase
import io.github.sophon.botdiscord.domain.usecase.GetHeatMovesUseCase
import io.github.sophon.botdiscord.domain.usecase.GetHomingMovesUseCase
import io.github.sophon.botdiscord.domain.usecase.GetPowerCrushMovesUseCase
import io.github.sophon.botdiscord.domain.usecase.SearchFrameDataUseCase
import io.github.sophon.botdiscord.domain.usecase.SearchGlossaryUseCase
import io.github.sophon.botdiscord.domain.usecase.StartGlossaryUseCase
import io.github.sophon.botdiscord.featureRegistry.GlossaryFeature
import io.github.sophon.botdiscord.featureRegistry.frameData.FrameDataFeature
import io.github.sophon.botdiscord.featureRegistry.frameData.Scheduler
import io.github.sophon.botdiscord.infrastructure.FileReaderJVM
import io.github.sophon.core.coreModule
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
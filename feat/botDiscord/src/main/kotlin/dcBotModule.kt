import com.example.core.coreModule
import data.InMemoryGlossaryDB
import data.InMemoryMoveListDB
import data.MoveListDB
import data.GlossaryDB
import featureRegistry.FrameDataFeature
import featureRegistry.GlossaryFeature
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module
import usecase.GetHeatMovesUseCase
import usecase.GetHomingMovesUseCase
import usecase.GetPowerCrushMovesUseCase
import usecase.SearchFrameDataUseCase
import usecase.SearchGlossaryUseCase
import usecase.StartGlossaryUseCase
import usecase.StartWikiUseCase

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
}
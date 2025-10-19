import com.example.core.coreModule
import domain.EmbedBuilder
import domain.serviceRegistry.FrameDataService
import domain.serviceRegistry.GlossaryService
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module
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

    singleOf(::HeihachiRebornImpl).bind<HeihachiReborn>()
    singleOf(::EmbedBuilder)

    singleOf(::StartGlossaryUseCase)
    singleOf(::SearchGlossaryUseCase)
    singleOf(::StartWikiUseCase)
    singleOf(::SearchFrameDataUseCase)

    singleOf(::FrameDataService)
    singleOf(::GlossaryService)
}
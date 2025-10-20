import com.example.core.coreModule
import dataLocal.GlossaryDB
import dataLocal.InMemoryGlossaryDB
import dataRemote.InfilGlossaryDataSource
import dataRemote.InfilGlossaryDataSourceImpl
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module
import usecase.CacheGlossaryUseCase
import usecase.DownloadGlossaryUseCase
import usecase.FetchDataForTermUseCase

fun initKoin(config: KoinAppDeclaration? = null) = startKoin {
    config?.invoke(this)

    //add Koin modules here
    modules(
        infilModule,
        coreModule,
    )
}

val infilModule = module {
    singleOf(::InfilGlossaryDataSourceImpl).bind<InfilGlossaryDataSource>()
    singleOf(::InfilGlossaryImpl).bind<InfilGlossary>()
    singleOf(::InMemoryGlossaryDB).bind<GlossaryDB>()

    singleOf(::DownloadGlossaryUseCase)
    singleOf(::CacheGlossaryUseCase)
    singleOf(::FetchDataForTermUseCase)

    singleOf(::InfilUrlProvider)
}
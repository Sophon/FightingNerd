import com.example.core.coreModule
import data.InfilGlossaryDataSource
import data.InfilGlossaryDataSourceImpl
import domain.InfilUrlProvider
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

    singleOf(::DownloadGlossaryUseCase)
    singleOf(::CacheGlossaryUseCase)
    singleOf(::FetchDataForTermUseCase)

    singleOf(::InfilUrlProvider)
}
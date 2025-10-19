import com.example.core.coreModule
import dataLocal.InMemoryMoveListDB
import dataLocal.MoveListDB
import dataRemote.WavuWikiDataSource
import dataRemote.WavuWikiDataSourceImpl
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module
import usecase.CacheMoveListUseCase
import usecase.DownloadMoveListUseCase
import usecase.FetchCharacterListUseCase
import usecase.FetchMoveDataUseCase

fun initKoin(config: KoinAppDeclaration? = null) = startKoin {
    config?.invoke(this)

    modules(
        coreModule,
        wavuModule,
    )
}

val wavuModule = module {
    singleOf(::WavuWikiDataSourceImpl).bind<WavuWikiDataSource>()
    singleOf(::WavuWikiClientImpl).bind<WavuWikiClient>()
    singleOf(::InMemoryMoveListDB).bind<MoveListDB>()

    singleOf(::FetchCharacterListUseCase)
    singleOf(::DownloadMoveListUseCase)
    singleOf(::CacheMoveListUseCase)
    singleOf(::FetchMoveDataUseCase)
}
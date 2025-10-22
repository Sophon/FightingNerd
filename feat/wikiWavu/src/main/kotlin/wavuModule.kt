import com.example.core.coreModule
import dataLocal.InMemoryMoveListDB
import dataLocal.MoveListDB
import dataRemote.WavuWikiDataSource
import dataRemote.WavuWikiDataSourceImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module
import usecase.CacheMoveListUseCase
import usecase.DownloadMoveListUseCase
import usecase.FetchCharacterListUseCase
import usecase.FetchMoveDataUseCase
import usecase.FetchMovesWithPropertyUseCase

fun initKoin(config: KoinAppDeclaration? = null) = startKoin {
    config?.invoke(this)

    modules(
        coreModule,
        wavuModule,
    )
}

val wavuModule = module {
    single {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    singleOf(::WavuWikiDataSourceImpl).bind<WavuWikiDataSource>()
    singleOf(::WavuWikiClientImpl).bind<WavuWikiClient>()
    singleOf(::InMemoryMoveListDB).bind<MoveListDB>()

    singleOf(::FetchCharacterListUseCase)
    singleOf(::DownloadMoveListUseCase)
    singleOf(::CacheMoveListUseCase)
    singleOf(::FetchMoveDataUseCase)
    singleOf(::FetchMovesWithPropertyUseCase)

    singleOf(::WavuUrlProvider)
    singleOf(::Scheduler)
}
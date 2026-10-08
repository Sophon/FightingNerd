package io.github.sophon.wiki

import io.github.sophon.wiki.adapter.outbound.ktor.KtorGameDataAdapter
import io.github.sophon.wiki.adapter.outbound.ktor.dragDown.DragDownKtorGameDataAdapter
import io.github.sophon.wiki.adapter.outbound.ktor.dreamCancel.DreamCancelKtorGameDataAdapter
import io.github.sophon.wiki.adapter.outbound.ktor.dustLoop.DustLoopKtorGameDataAdapter
import io.github.sophon.wiki.adapter.outbound.ktor.mizuumi.MizuumiKtorGameDataAdapter
import io.github.sophon.wiki.adapter.outbound.ktor.superCombo.SuperComboKtorGameDataAdapter
import io.github.sophon.wiki.adapter.outbound.ktor.wavu.WavuKtorGameDataAdapter
import io.github.sophon.wiki.adapter.outbound.ktor.xko.XkoKtorGameDataAdapter
import io.github.sophon.wiki.adapter.outbound.memory.InMemoryWikiConfigAdapter
import io.github.sophon.wiki.adapter.outbound.sqldelight.LazyWikiDB
import io.github.sophon.wiki.adapter.outbound.sqldelight.SqlDelightCharacterAdapter
import io.github.sophon.wiki.adapter.outbound.sqldelight.SqlDelightGamePropertiesRouter
import io.github.sophon.wiki.adapter.outbound.sqldelight.SqlDelightMoveAdapter
import io.github.sophon.wiki.adapter.outbound.sqldelight.createWikiSqlDriver
import io.github.sophon.wiki.adapter.outbound.sqldelight.gameProperties.DragDownSqlDelightGameProperties
import io.github.sophon.wiki.adapter.outbound.sqldelight.gameProperties.DreamCancelSqlDelightGameProperties
import io.github.sophon.wiki.adapter.outbound.sqldelight.gameProperties.DustLoopSqlDelightGameProperties
import io.github.sophon.wiki.adapter.outbound.sqldelight.gameProperties.MizuumiSqlDelightGameProperties
import io.github.sophon.wiki.adapter.outbound.sqldelight.gameProperties.SuperComboSqlDelightGameProperties
import io.github.sophon.wiki.adapter.outbound.sqldelight.gameProperties.WavuSqlDelightGameProperties
import io.github.sophon.wiki.app.outPort.DeleteCharacterListPort
import io.github.sophon.wiki.app.outPort.DeleteMoveListPort
import io.github.sophon.wiki.app.outPort.FetchGameDataPort
import io.github.sophon.wiki.app.outPort.LoadCharacterListPort
import io.github.sophon.wiki.app.outPort.LoadCharacterPort
import io.github.sophon.wiki.app.outPort.LoadLastUpdatePort
import io.github.sophon.wiki.app.outPort.LoadMoveListPort
import io.github.sophon.wiki.app.outPort.LoadMovePort
import io.github.sophon.wiki.app.outPort.LoadWikiConfigPort
import io.github.sophon.wiki.app.outPort.SaveCharacterMoveListPort
import io.github.sophon.wiki.app.outPort.SaveWikiConfigPort
import io.github.sophon.wiki.app.outPort.StrikeCharacterListPort
import io.github.sophon.wiki.app.service.ConfigureWikiService
import io.github.sophon.wiki.app.service.GetAvailableGamesService
import io.github.sophon.wiki.app.service.GetCharacterListService
import io.github.sophon.wiki.app.service.GetCharacterService
import io.github.sophon.wiki.app.service.GetFiltersService
import io.github.sophon.wiki.app.service.GetGroupsService
import io.github.sophon.wiki.app.service.GetMoveListService
import io.github.sophon.wiki.app.service.GetMoveService
import io.github.sophon.wiki.app.service.GetUpdateTimeStampService
import io.github.sophon.wiki.app.service.NormalizeMoveInputService
import io.github.sophon.wiki.app.service.RefreshDataService
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import kotlin.time.Clock

/**
 * [databaseDirectory] - where `wiki.db` lives; null for the platform's default database location.
 */
fun wikiModule(databaseDirectory: String? = null): Module = module {
    single { WikiFeatureInfo }


    singleOf(::ConfigureWikiService)
    singleOf(::GetAvailableGamesService)
    singleOf(::GetCharacterListService)
    singleOf(::GetCharacterService)
    singleOf(::GetFiltersService)
    singleOf(::GetGroupsService)
    singleOf(::GetMoveListService)
    singleOf(::GetMoveService)
    singleOf(::GetUpdateTimeStampService)
    singleOf(::NormalizeMoveInputService)
    singleOf(::RefreshDataService)

    single<ConfigureWikiUseCase> { get<ConfigureWikiService>() }
    single<GetAvailableGamesUseCase> { get<GetAvailableGamesService>() }
    single<GetCharacterListUseCase> { get<GetCharacterListService>() }
    single<GetCharacterUseCase> { get<GetCharacterService>() }
    single<GetFiltersUseCase> { get<GetFiltersService>() }
    single<GetGroupsUseCase> { get<GetGroupsService>() }
    single<GetMoveListUseCase> { get<GetMoveListService>() }
    single<GetMoveUseCase> { get<GetMoveService>() }
    single<GetUpdateTimeStampUseCase> { get<GetUpdateTimeStampService>() }
    single<NormalizeMoveInputUseCase> { get<NormalizeMoveInputService>() }
    single<RefreshDataUseCase> { get<RefreshDataService>() }


    singleOf(::InMemoryWikiConfigAdapter)
    singleOf(::KtorGameDataAdapter)
    singleOf(::WavuKtorGameDataAdapter)
    singleOf(::MizuumiKtorGameDataAdapter)
    singleOf(::DustLoopKtorGameDataAdapter)
    singleOf(::SuperComboKtorGameDataAdapter)
    singleOf(::DragDownKtorGameDataAdapter)
    singleOf(::XkoKtorGameDataAdapter)
    singleOf(::DreamCancelKtorGameDataAdapter)


    single { LazyWikiDB { createWikiSqlDriver(databaseDirectory) } }
    singleOf(::SqlDelightGamePropertiesRouter)
    singleOf(::WavuSqlDelightGameProperties)
    singleOf(::MizuumiSqlDelightGameProperties)
    singleOf(::DustLoopSqlDelightGameProperties)
    singleOf(::SuperComboSqlDelightGameProperties)
    singleOf(::DragDownSqlDelightGameProperties)
    singleOf(::DreamCancelSqlDelightGameProperties)
    single {
        SqlDelightCharacterAdapter(
            wikiDatabase = get(),
            gamePropertiesRouter = get(),
            clock = Clock.System,
        )
    }
    singleOf(::SqlDelightMoveAdapter)


    single<DeleteCharacterListPort> { get<SqlDelightCharacterAdapter>() }
    single<DeleteMoveListPort> { get<SqlDelightMoveAdapter>() }
    single<FetchGameDataPort> { get<KtorGameDataAdapter>() }
    single<LoadCharacterListPort> { get<SqlDelightCharacterAdapter>() }
    single<LoadCharacterPort> { get<SqlDelightCharacterAdapter>() }
    single<LoadLastUpdatePort> { get<SqlDelightMoveAdapter>() }
    single<LoadMoveListPort> { get<SqlDelightMoveAdapter>() }
    single<LoadMovePort> { get<SqlDelightMoveAdapter>() }
    single<LoadWikiConfigPort> { get<InMemoryWikiConfigAdapter>() }
    single<SaveCharacterMoveListPort> { get<SqlDelightCharacterAdapter>() }
    single<SaveWikiConfigPort> { get<InMemoryWikiConfigAdapter>() }
    single<StrikeCharacterListPort> { get<SqlDelightCharacterAdapter>() }
}

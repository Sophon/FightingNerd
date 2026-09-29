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
import io.github.sophon.wiki.application.domain.service.ClearCacheService
import io.github.sophon.wiki.application.domain.service.ConfigureWikiService
import io.github.sophon.wiki.application.domain.service.GetCharacterListService
import io.github.sophon.wiki.application.domain.service.GetFiltersService
import io.github.sophon.wiki.application.domain.service.GetGroupsService
import io.github.sophon.wiki.application.domain.service.GetMoveListService
import io.github.sophon.wiki.application.domain.service.GetUpdateTimeStampService
import io.github.sophon.wiki.application.domain.service.RefreshDataService
import io.github.sophon.wiki.application.port.inbound.ClearCacheUseCase
import io.github.sophon.wiki.application.port.inbound.ConfigureWikiUseCase
import io.github.sophon.wiki.application.port.inbound.GetCharacterListUseCase
import io.github.sophon.wiki.application.port.inbound.GetFiltersUseCase
import io.github.sophon.wiki.application.port.inbound.GetGroupsUseCase
import io.github.sophon.wiki.application.port.inbound.GetMoveListUseCase
import io.github.sophon.wiki.application.port.inbound.GetUpdateTimeStampUseCase
import io.github.sophon.wiki.application.port.inbound.RefreshDataUseCase
import io.github.sophon.wiki.application.port.outbound.DeleteCharacterListPort
import io.github.sophon.wiki.application.port.outbound.DeleteMoveListPort
import io.github.sophon.wiki.application.port.outbound.FetchGameDataPort
import io.github.sophon.wiki.application.port.outbound.LoadCharacterListPort
import io.github.sophon.wiki.application.port.outbound.LoadLastUpdatePort
import io.github.sophon.wiki.application.port.outbound.LoadMoveListPort
import io.github.sophon.wiki.application.port.outbound.LoadWikiConfigPort
import io.github.sophon.wiki.application.port.outbound.SaveCharacterMoveListPort
import io.github.sophon.wiki.application.port.outbound.SaveWikiConfigPort
import io.github.sophon.wiki.application.port.outbound.StrikeCharacterListPort
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.time.Clock

/**
 * [databaseDirectory] - where `wiki.db` lives; null for the platform's default database location.
 */
fun wikiModule(databaseDirectory: String? = null): Module = module {

    // region Use cases and Services
    singleOf(::ClearCacheService).bind<ClearCacheUseCase>()
    singleOf(::ConfigureWikiService).bind<ConfigureWikiUseCase>()
    singleOf(::GetCharacterListService).bind<GetCharacterListUseCase>()
    singleOf(::GetFiltersService).bind<GetFiltersUseCase>()
    singleOf(::GetGroupsService).bind<GetGroupsUseCase>()
    singleOf(::GetMoveListService).bind<GetMoveListUseCase>()
    singleOf(::GetUpdateTimeStampService).bind<GetUpdateTimeStampUseCase>()
    singleOf(::RefreshDataService).bind<RefreshDataUseCase>()
    //endregion

    // region Outbound adapters and Ports
    singleOf(::InMemoryWikiConfigAdapter) {
        bind<LoadWikiConfigPort>()
        bind<SaveWikiConfigPort>()
    }
    singleOf(::KtorGameDataAdapter).bind<FetchGameDataPort>()
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
    } withOptions {
        bind<LoadCharacterListPort>()
        bind<SaveCharacterMoveListPort>()
        bind<StrikeCharacterListPort>()
        bind<DeleteCharacterListPort>()
    }
    singleOf(::SqlDelightMoveAdapter) {
        bind<LoadMoveListPort>()
        bind<LoadLastUpdatePort>()
        bind<DeleteMoveListPort>()
    }
    //endregion
}

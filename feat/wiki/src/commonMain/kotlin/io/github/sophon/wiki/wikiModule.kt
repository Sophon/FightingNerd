package io.github.sophon.wiki

import io.github.sophon.wiki.application.domain.service.ClearCacheService
import io.github.sophon.wiki.application.domain.service.GetCharacterListService
import io.github.sophon.wiki.application.domain.service.GetFiltersService
import io.github.sophon.wiki.application.domain.service.GetGroupsService
import io.github.sophon.wiki.application.domain.service.GetMoveListService
import io.github.sophon.wiki.application.domain.service.GetUpdateTimeStampService
import io.github.sophon.wiki.application.domain.service.RefreshDataService
import io.github.sophon.wiki.application.port.inbound.ClearCacheUseCase
import io.github.sophon.wiki.application.port.inbound.GetCharacterListUseCase
import io.github.sophon.wiki.application.port.inbound.GetFiltersUseCase
import io.github.sophon.wiki.application.port.inbound.GetGroupsUseCase
import io.github.sophon.wiki.application.port.inbound.GetMoveListUseCase
import io.github.sophon.wiki.application.port.inbound.GetUpdateTimeStampUseCase
import io.github.sophon.wiki.application.port.inbound.RefreshDataUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun wikiModule(): Module = module {

    // region Use cases and Services
    singleOf(::ClearCacheService).bind<ClearCacheUseCase>()
    singleOf(::GetCharacterListService).bind<GetCharacterListUseCase>()
    singleOf(::GetFiltersService).bind<GetFiltersUseCase>()
    singleOf(::GetGroupsService).bind<GetGroupsUseCase>()
    singleOf(::GetMoveListService).bind<GetMoveListUseCase>()
    singleOf(::GetUpdateTimeStampService).bind<GetUpdateTimeStampUseCase>()
    singleOf(::RefreshDataService).bind<RefreshDataUseCase>()
    //endregion
}

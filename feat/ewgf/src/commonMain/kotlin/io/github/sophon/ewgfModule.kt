package io.github.sophon

import io.github.sophon.adapter.outbound.ktor.KtorBattleAdapter
import io.github.sophon.adapter.outbound.sqldelight.SqlDelightPlayerAdapter
import io.github.sophon.app.outPort.DeletePlayerPort
import io.github.sophon.app.outPort.FetchBattleListPort
import io.github.sophon.app.outPort.LoadPlayerPort
import io.github.sophon.app.outPort.SavePlayerPort
import io.github.sophon.app.outPort.UpdatePolarisIdPort
import io.github.sophon.app.service.GetRecentSetsService
import io.github.sophon.app.service.RegisterPlayerService
import io.github.sophon.app.service.UnregisterPlayerService
import io.github.sophon.app.service.UpdatePolarisIdService
import io.github.sophon.inPort.GetRecentSetsUseCase
import io.github.sophon.inPort.RegisterPlayerUseCase
import io.github.sophon.inPort.UnregisterPlayerUseCase
import io.github.sophon.inPort.UpdatePolarisIdUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.bind
import org.koin.dsl.module

fun ewgfModule(apiToken: String) = module {
    includes(ewgfPlatformModule)

    single { EwgfFeatureInfo }

    singleOf(::RegisterPlayerService).bind<RegisterPlayerUseCase>()
    singleOf(::UpdatePolarisIdService).bind<UpdatePolarisIdUseCase>()
    singleOf(::UnregisterPlayerService).bind<UnregisterPlayerUseCase>()
    singleOf(::GetRecentSetsService).bind<GetRecentSetsUseCase>()

    singleOf(::SqlDelightPlayerAdapter) withOptions {
        bind<LoadPlayerPort>()
        bind<SavePlayerPort>()
        bind<UpdatePolarisIdPort>()
        bind<DeletePlayerPort>()
    }
    single { KtorBattleAdapter(apiToken = apiToken, httpClient = get()) }.bind<FetchBattleListPort>()
}

internal expect val ewgfPlatformModule: Module

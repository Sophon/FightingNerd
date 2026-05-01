package io.github.sophon.integration

import io.github.sophon.integration.data.PlayerRepo
import io.github.sophon.data.local.PlayerRepoImpl
import io.github.sophon.data.remote.EwgfDataSource
import io.github.sophon.data.remote.EwgfDataSourceImpl
import io.github.sophon.domain.EwgfClientImpl
import io.github.sophon.usecase.DeletePlayerUseCase
import io.github.sophon.usecase.DownloadPlayerBattlesUseCase
import io.github.sophon.usecase.GroupBySetUseCase
import io.github.sophon.usecase.RegisterPlayerUseCase
import io.github.sophon.usecase.UpdatePolarisIdUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun ewgfModule(apiToken: String) = module {
    includes(ewgfPlatformModule)

    single { EwgfFeatureInfo }

    single<EwgfDataSource> {
        EwgfDataSourceImpl(
            apiToken = apiToken,
            httpClient = get(),
        )
    }

    singleOf(::EwgfClientImpl).bind<EwgfClient>()
    singleOf(::PlayerRepoImpl).bind<PlayerRepo>()

    singleOf(::RegisterPlayerUseCase)
    singleOf(::DownloadPlayerBattlesUseCase)
    singleOf(::UpdatePolarisIdUseCase)
    singleOf(::DeletePlayerUseCase)
    singleOf(::GroupBySetUseCase)
}

expect val ewgfPlatformModule: Module

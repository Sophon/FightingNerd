package io.github.sophon

import io.github.sophon.data.PlayerRepo
import io.github.sophon.data.local.PlayerRepoImpl
import io.github.sophon.domain.EwgfFeatureInfo
import io.github.sophon.usecase.DeletePlayerUseCase
import io.github.sophon.usecase.RegisterPlayerUseCase
import io.github.sophon.usecase.UpdatePolarisIdUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun ewgfModule() = module {
    includes(platformModule)

    single { EwgfFeatureInfo }

    singleOf(::EwgfClientImpl).bind<EwgfClient>()
    singleOf(::PlayerRepoImpl).bind<PlayerRepo>()

    singleOf(::RegisterPlayerUseCase)
    singleOf(::UpdatePolarisIdUseCase)
    singleOf(::DeletePlayerUseCase)
}

expect val platformModule: Module
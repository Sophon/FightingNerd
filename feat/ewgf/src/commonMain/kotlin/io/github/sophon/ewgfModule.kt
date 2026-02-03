package io.github.sophon

import io.github.sophon.data.PlayerRepo
import io.github.sophon.data.PlayerRepoImpl
import io.github.sophon.domain.EwgfFeatureInfo
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun ewgfModule() = module {
    includes(platformModule)

    single { EwgfFeatureInfo }

    singleOf(::PlayerRepoImpl).bind<PlayerRepo>()
}

expect val platformModule: Module
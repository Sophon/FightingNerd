package io.github.sophon

import io.github.sophon.data.BanRepo
import io.github.sophon.data.BanRepoImpl
import io.github.sophon.domain.AdminFeatureInfo
import io.github.sophon.domain.usecase.CreateReplyUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun adminModule() = module {
    single { AdminFeatureInfo }
    singleOf(::AdminToolImpl).bind<AdminTool>()

    includes(platformModule)
    singleOf(::BanRepoImpl).bind<BanRepo>()

    singleOf(::CreateReplyUseCase)
}

expect val platformModule: Module
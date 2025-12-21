package io.github.sophon

import io.github.sophon.data.BanRepo
import io.github.sophon.data.BanRepoImpl
import io.github.sophon.domain.AdminFeatureInfo
import io.github.sophon.usecase.ProcessFeedbackUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun adminModule() = module {
    includes(platformModule)

    single { AdminFeatureInfo }
    singleOf(::AdminToolImpl).bind<AdminTool>()

    singleOf(::BanRepoImpl).bind<BanRepo>()

    singleOf(::ProcessFeedbackUseCase)
}

expect val platformModule: Module
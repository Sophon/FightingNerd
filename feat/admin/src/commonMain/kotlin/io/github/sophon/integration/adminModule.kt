package io.github.sophon.integration

import io.github.sophon.adapter.inbound.scheduler.AdminScheduler
import io.github.sophon.adapter.outbound.sqldelight.SqlDelightAdapter
import io.github.sophon.app.domain.service.BanUserService
import io.github.sophon.app.domain.service.UnbanUserService
import io.github.sophon.app.port.inbound.BanUserUseCase
import io.github.sophon.app.port.inbound.UnbanUserUseCase
import io.github.sophon.app.port.outbound.BanPort
import io.github.sophon.data.BanRepo
import io.github.sophon.data.BanRepoImpl
import io.github.sophon.domain.AdminToolImpl
import io.github.sophon.usecase.ProcessFeedbackUseCase
import io.github.sophon.usecase.ProcessReplyUseCase
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
    singleOf(::ProcessReplyUseCase)

    singleOf(::BanUserService).bind<BanUserUseCase>()
    singleOf(::UnbanUserService).bind<UnbanUserUseCase>()

    singleOf(::SqlDelightAdapter).bind<BanPort>()
    singleOf(::AdminScheduler)
}

expect val platformModule: Module

package io.github.sophon

import io.github.sophon.adapter.inbound.scheduler.AdminScheduler
import io.github.sophon.adapter.outbound.memory.InMemoryAdminListAdapter
import io.github.sophon.adapter.outbound.sqldelight.SqlDelightAdapter
import io.github.sophon.app.service.BanUserService
import io.github.sophon.app.service.ConfigureAdminToolService
import io.github.sophon.app.service.IsUserAdminService
import io.github.sophon.app.service.IsUserBannedService
import io.github.sophon.app.service.UnbanUserService
import io.github.sophon.inPort.BanUserUseCase
import io.github.sophon.inPort.ConfigureAdminToolUseCase
import io.github.sophon.inPort.IsUserAdminUseCase
import io.github.sophon.inPort.IsUserBannedUseCase
import io.github.sophon.inPort.UnbanUserUseCase
import io.github.sophon.app.outPort.AdminListPort
import io.github.sophon.app.outPort.BanPort
import io.github.sophon.app.outPort.ClearExpiredBansPort
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.time.Clock

fun adminModule() = module {
    includes(platformModule)

    single { AdminFeatureInfo }

    singleOf(::ConfigureAdminToolService).bind<ConfigureAdminToolUseCase>()
    singleOf(::IsUserAdminService).bind<IsUserAdminUseCase>()
    single { BanUserService(adminListPort = get(), banPort = get(), clock = Clock.System) }.bind<BanUserUseCase>()
    singleOf(::UnbanUserService).bind<UnbanUserUseCase>()
    single { IsUserBannedService(banPort = get(), clock = Clock.System) }.bind<IsUserBannedUseCase>()

    single { SqlDelightAdapter(driverFactory = get(), clock = Clock.System) } withOptions {
        bind<BanPort>()
        bind<ClearExpiredBansPort>()
    }
    singleOf(::InMemoryAdminListAdapter).bind<AdminListPort>()
    singleOf(::AdminScheduler)
}

expect val platformModule: Module

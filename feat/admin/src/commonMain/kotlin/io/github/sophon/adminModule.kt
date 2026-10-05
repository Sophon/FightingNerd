package io.github.sophon

import io.github.sophon.adapter.inbound.scheduler.AdminScheduler
import io.github.sophon.adapter.outbound.memory.InMemoryAdminListAdapter
import io.github.sophon.adapter.outbound.sqldelight.SqlDelightAdapter
import io.github.sophon.app.service.BanUserService
import io.github.sophon.app.service.ConfigureAdminToolService
import io.github.sophon.app.service.IsUserAdminService
import io.github.sophon.app.service.UnbanUserService
import io.github.sophon.inboundPorts.BanUserUseCase
import io.github.sophon.inboundPorts.ConfigureAdminToolUseCase
import io.github.sophon.inboundPorts.IsUserAdminUseCase
import io.github.sophon.inboundPorts.UnbanUserUseCase
import io.github.sophon.app.outboundPorts.AdminListPort
import io.github.sophon.app.outboundPorts.BanPort
import io.github.sophon.app.outboundPorts.ClearExpiredBansPort
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
    singleOf(::BanUserService).bind<BanUserUseCase>()
    singleOf(::UnbanUserService).bind<UnbanUserUseCase>()

    single { SqlDelightAdapter(driverFactory = get(), clock = Clock.System) } withOptions {
        bind<BanPort>()
        bind<ClearExpiredBansPort>()
    }
    singleOf(::InMemoryAdminListAdapter).bind<AdminListPort>()
    singleOf(::AdminScheduler)
}

expect val platformModule: Module

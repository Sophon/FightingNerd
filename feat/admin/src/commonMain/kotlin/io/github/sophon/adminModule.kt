package io.github.sophon

import io.github.sophon.adapter.inbound.scheduler.AdminScheduler
import io.github.sophon.adapter.outbound.memory.InMemoryAdminListAdapter
import io.github.sophon.adapter.outbound.sqldelight.SqlDelightAdapter
import io.github.sophon.app.domain.service.BanUserService
import io.github.sophon.app.domain.service.ConfigureAdminToolService
import io.github.sophon.app.domain.service.IsUserAdminService
import io.github.sophon.app.domain.service.UnbanUserService
import io.github.sophon.app.port.inbound.BanUserUseCase
import io.github.sophon.app.port.inbound.ConfigureAdminToolUseCase
import io.github.sophon.app.port.inbound.IsUserAdminUseCase
import io.github.sophon.app.port.inbound.UnbanUserUseCase
import io.github.sophon.app.port.outbound.AdminListPort
import io.github.sophon.app.port.outbound.BanPort
import io.github.sophon.app.port.outbound.ClearExpiredBansPort
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

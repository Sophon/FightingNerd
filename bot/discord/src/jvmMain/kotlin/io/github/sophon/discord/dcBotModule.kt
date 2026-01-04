package io.github.sophon.discord

import dev.kord.core.Kord
import io.github.sophon.adminModule
import io.github.sophon.core.coreModule
import io.github.sophon.discord.data.InMemoryGlossaryDB
import io.github.sophon.discord.featureRegistry.featureRegistryModule
import io.github.sophon.discord.usecase.CreateErrorEmbedUseCase
import io.github.sophon.discord.usecase.RouteCommandToFeatureUseCase
import io.github.sophon.dreamcancel.dreamCancelModule
import io.github.sophon.glossaryinfil.data.GlossaryDB
import io.github.sophon.glossaryinfil.infilModule
import io.github.sophon.wikiSuperCombo.superComboModule
import io.github.sophon.wikidustloop.dustLoopModule
import io.github.sophon.wikimizuumi.mizuumiModule
import io.github.sophon.wikiwavu.wavuModule
import io.github.sophon.xko.xkoModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

fun initKoin(
    kord: Kord,
    config: KoinAppDeclaration? = null
) = startKoin {
    config?.invoke(this)

    modules(
        coreModule,
        dcBotModule(kord),
        adminModule(),

        infilModule,
        wavuModule(),
        superComboModule(),
        xkoModule(),
        dreamCancelModule(),
        dustLoopModule(),
        mizuumiModule(),

        featureRegistryModule,
    )
}

fun dcBotModule(kord: Kord) = module {
    single {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
    single { kord }

    singleOf(::DiscordBotImpl).bind<DiscordBot>()

    singleOf(::RouteCommandToFeatureUseCase)
    singleOf(::CreateErrorEmbedUseCase)

    singleOf(::InMemoryGlossaryDB).bind<GlossaryDB>()
}

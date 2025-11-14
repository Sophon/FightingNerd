package io.github.sophon.discord

import io.github.sophon.core.coreModule
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.discord.data.InMemoryCharacterListDB
import io.github.sophon.discord.data.InMemoryGlossaryDB
import io.github.sophon.discord.data.InMemoryMoveListDB
import io.github.sophon.discord.featureRegistry.featureRegistryModule
import io.github.sophon.discord.usecase.RouteCommandToFeatureUseCase
import io.github.sophon.glossaryinfil.data.GlossaryDB
import io.github.sophon.glossaryinfil.infilModule
import io.github.sophon.wikiSuperCombo.superComboModule
import io.github.sophon.wikiwavu.wavuModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

internal const val QUALIFIER_WAVU = "wavu"
internal const val QUALIFIER_SC = "superCombo"

fun initKoin(
    apiKey: String,
    config: KoinAppDeclaration? = null
) = startKoin {
    config?.invoke(this)

    modules(
        coreModule,
        dcBotModule(apiKey),

        infilModule,
        wavuModule(named(QUALIFIER_WAVU)),
        superComboModule(named(QUALIFIER_SC)),

        featureRegistryModule,
    )
}

fun dcBotModule(apiKey: String) = module {
    single { apiKey }
    single {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    singleOf(::DiscordBotImpl).bind<DiscordBot>()

    // Separate database instances per feature - bind to interfaces with qualifiers
    single<CharacterListDB>(named(QUALIFIER_WAVU)) { InMemoryCharacterListDB() }
    single<CharacterListDB>(named(QUALIFIER_SC)) { InMemoryCharacterListDB() }

    single<MoveListDB>(named(QUALIFIER_WAVU)) { InMemoryMoveListDB() }
    single<MoveListDB>(named(QUALIFIER_SC)) { InMemoryMoveListDB() }

    singleOf(::InMemoryGlossaryDB).bind<GlossaryDB>()

    singleOf(::RouteCommandToFeatureUseCase)
}
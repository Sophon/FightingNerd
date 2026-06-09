package io.github.sophon.fightingnerd

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.util.DebugLogger
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.core.featureConfig.CoreFeatureRepo
import io.github.sophon.fightingnerd.navigation.ui.BottomBarView
import io.github.sophon.fightingnerd.feat.home.ui.HomeScreen
import io.github.sophon.fightingnerd.feat.module.usecase.LoadConfigUseCase
import io.github.sophon.fightingnerd.feat.moveList.ui.MoveListScreen
import io.github.sophon.fightingnerd.navigation.domain.Destination
import io.github.sophon.fightingnerd.navigation.ui.PlaceholderScreen
import io.github.sophon.fightingnerd.theme.AppTheme
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.koinInject

private val navConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(Destination.Home::class, Destination.Home.serializer())
            subclass(Destination.MoveList::class, Destination.MoveList.serializer())
        }
    }
}

@Composable
internal fun App() {
//    setSingletonImageLoaderFactory { context ->
//        getAsyncImageLoader(context)
//    }
    var isInitialized by remember { mutableStateOf(false) }

    val featureRepo = koinInject<CoreFeatureRepo>()
    val loadConfigUseCase = koinInject<LoadConfigUseCase>()
    LaunchedEffect(Unit) {
        loadConfigUseCase.invoke()
            .onSuccess { config ->
                featureRepo.initialize(config)
                isInitialized = true
            }
    }

    if (isInitialized) {
        AppTheme {
            val backStack = rememberNavBackStack(navConfig, Destination.Home)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface),
            ) {
                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator(),
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding(),
                    entryProvider = entryProvider {
                        entry<Destination.Home> {
                            HomeScreen(
                                onNavigateToMoveList = { gameId, characterId ->
                                    backStack.add(Destination.MoveList(gameId = gameId, characterId = characterId))
                                }
                            )
                        }
                        entry<Destination.Search> {
                            PlaceholderScreen(label = "Search")
                        }
                        entry<Destination.Saved> {
                            PlaceholderScreen(label = "Saved")
                        }
                        entry<Destination.Quiz> {
                            PlaceholderScreen(label = "Quiz")
                        }
                        entry<Destination.More> {
                            PlaceholderScreen(label = "More")
                        }

                        entry<Destination.MoveList>{ destination ->
                            MoveListScreen(
                                gameId = destination.gameId,
                                characterId = destination.characterId,
                            )
                        }
                    }
                )

                AnimatedVisibility(
                    visible = backStack.size == 1,
                    enter = slideInVertically { it },
                    exit = slideOutVertically { it },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding(),
                ) {
                    BottomBarView(
                        currentRoot = backStack.first() as Destination.TopLevelDestination,
                        onTabClick = { destination ->
                            backStack.clear()
                            backStack.add(destination)
                        },
                    )
                }
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

fun getAsyncImageLoader(context: PlatformContext): ImageLoader {
    return ImageLoader.Builder(context)
        .logger(DebugLogger())
        .build()
}

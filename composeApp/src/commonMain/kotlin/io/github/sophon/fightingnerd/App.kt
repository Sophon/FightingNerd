package io.github.sophon.fightingnerd

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.core.featureConfig.CoreFeatureRepo
import io.github.sophon.fightingnerd.core.ui.components.CircularLoader
import io.github.sophon.fightingnerd.core.ui.OverlayService
import io.github.sophon.fightingnerd.core.ui.components.ToastSnackbar
import io.github.sophon.fightingnerd.core.ui.components.ToastVisuals
import io.github.sophon.fightingnerd.feat.home.ui.HomeScreen
import io.github.sophon.fightingnerd.feat.module.usecase.LoadConfigUseCase
import io.github.sophon.fightingnerd.feat.more.model.MoreItem
import io.github.sophon.fightingnerd.feat.more.ui.MoreScreen
import io.github.sophon.fightingnerd.feat.more.ui.featureSettings.FeatureSettingsScreen
import io.github.sophon.fightingnerd.feat.move.ui.MoveListScreen
import io.github.sophon.fightingnerd.feat.quiz.ui.overview.QuizOverviewScreen
import io.github.sophon.fightingnerd.feat.quiz.ui.quiz.QuizScreen
import io.github.sophon.fightingnerd.navigation.domain.Destination
import io.github.sophon.fightingnerd.navigation.ui.BottomNavBarView
import io.github.sophon.fightingnerd.navigation.ui.PlaceholderScreen
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.koinInject

private val navConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(Destination.Home::class, Destination.Home.serializer())
            subclass(Destination.Search::class, Destination.Search.serializer())
            subclass(Destination.Saved::class, Destination.Saved.serializer())
            subclass(Destination.Quiz::class, Destination.Quiz.serializer())
            subclass(Destination.More::class, Destination.More.serializer())
            subclass(Destination.MoveList::class, Destination.MoveList.serializer())
            subclass(Destination.MoveDetail::class, Destination.MoveDetail.serializer())
            subclass(Destination.CharacterDetail::class, Destination.CharacterDetail.serializer())
            subclass(Destination.FeatureSettings::class, Destination.FeatureSettings.serializer())
        }
    }
}
val LocalBottomBarPadding = compositionLocalOf { PaddingValues(0.dp) }

@Composable
internal fun App() {
    val isInitialized = rememberFeaturesLoaded()

    FightingNerdTheme {
        if (isInitialized) {
            Content()
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularLoader()
            }
        }
    }
}

@Composable
private fun rememberFeaturesLoaded(): Boolean {
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

    return isInitialized
}

@Composable
private fun Content(
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(navConfig, Destination.Home)
    val overlayService = koinInject<OverlayService>()
    val snackbarHostState = remember { SnackbarHostState() }

    val botPaddingValues = PaddingValues(
        bottom = 80.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    )
    CompositionLocalProvider(LocalBottomBarPadding provides botPaddingValues) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
        ) {
            AppNavDisplay(backStack = backStack)

            AppBottomBar(backStack = backStack)

            OverlayContent(
                overlayService = overlayService,
                snackbarHostState = snackbarHostState,
            )
        }
    }
}

@Composable
private fun AppNavDisplay(
    backStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier
) {
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        modifier = modifier
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
            entry<Destination.QuizOverview> {
                QuizOverviewScreen(
                    onNavigateToQuiz = { gameId ->
                        backStack.add(Destination.Quiz(gameId = gameId))
                    }
                )
            }
            entry<Destination.More> {
                MoreScreen(
                    onNavigate = { moreItem ->
                        when (moreItem) {
                            MoreItem.FeatureSettings -> backStack.add(Destination.FeatureSettings)

//                            MoreItem.Theme -> {/* no navigation */}
                        }
                    }
                )
            }

            entry<Destination.MoveList> { destination ->
                MoveListScreen(
                    gameId = destination.gameId,
                    characterId = destination.characterId,
                )
            }
            entry<Destination.FeatureSettings> {
                FeatureSettingsScreen()
            }
            entry<Destination.Quiz> { destination ->
                QuizScreen(gameId = destination.gameId)
            }
        }
    )
}

@Composable
internal fun OverlayContent(
    overlayService: OverlayService,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(overlayService) {
        overlayService.toast.collect { toast ->
            snackbarHostState.showSnackbar(ToastVisuals(toast))
        }
    }

    val bottomBarPadding = LocalBottomBarPadding.current
    Box(modifier = modifier.fillMaxSize()) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottomBarPadding),
        ) { snackbarData ->
            val visuals = snackbarData.visuals as ToastVisuals
            ToastSnackbar(toast = visuals.toast)
        }
    }
}

@Composable
private fun BoxScope.AppBottomBar(
    backStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = (backStack.size == 1),
        enter = slideInVertically { it },
        exit = slideOutVertically { it },
        modifier = modifier
            .align(Alignment.BottomCenter)
            .navigationBarsPadding(),
    ) {
        BottomNavBarView(
            currentRoot = backStack.first() as Destination.TopLevelDestination,
            onTabClick = { destination ->
                backStack.clear()
                backStack.add(destination)
            },
        )
    }
}

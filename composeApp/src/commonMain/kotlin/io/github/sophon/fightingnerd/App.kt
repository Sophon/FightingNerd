package io.github.sophon.fightingnerd

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import kotlinx.coroutines.flow.collect
import io.github.sophon.fightingnerd.adapter.inbound.coreUi.Dialog
import io.github.sophon.fightingnerd.adapter.inbound.coreUi.OverlayService
import io.github.sophon.fightingnerd.adapter.inbound.coreUi.components.ToastSnackBar
import io.github.sophon.fightingnerd.adapter.inbound.coreUi.components.ToastVisuals
import io.github.sophon.fightingnerd.adapter.inbound.changelog.ChangelogDialog
import io.github.sophon.fightingnerd.adapter.inbound.home.HomeScreen
import io.github.sophon.fightingnerd.adapter.inbound.more.model.MoreItem
import io.github.sophon.fightingnerd.adapter.inbound.more.MoreScreen
import io.github.sophon.fightingnerd.adapter.inbound.more.featureSettings.FeatureSettingsScreen
import io.github.sophon.fightingnerd.adapter.inbound.more.updates.UpdatesScreen
import io.github.sophon.fightingnerd.adapter.inbound.move.MoveListScreen
import io.github.sophon.fightingnerd.adapter.inbound.quiz.QuizOverviewScreen
import io.github.sophon.fightingnerd.adapter.inbound.quiz.QuizScreen
import io.github.sophon.fightingnerd.inPort.RecordInstallationUseCase
import io.github.sophon.fightingnerd.adapter.inbound.more.about.AboutScreen
import io.github.sophon.fightingnerd.inPort.OnLaunchSetupUseCase
import io.github.sophon.fightingnerd.inPort.SaveReleaseAsSeenUseCase
import io.github.sophon.fightingnerd.inPort.SubscribeToUnseenReleaseUseCase
import io.github.sophon.fightingnerd.adapter.inbound.navigation.Destination
import io.github.sophon.fightingnerd.adapter.inbound.navigation.rootDestinationSet
import io.github.sophon.fightingnerd.adapter.inbound.navigation.rootDestinations
import io.github.sophon.fightingnerd.adapter.inbound.navigation.BottomNavBarView
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import kotlinx.coroutines.launch
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
            subclass(Destination.CharacterDetail::class, Destination.CharacterDetail.serializer())
            subclass(Destination.FeatureSettings::class, Destination.FeatureSettings.serializer())
            subclass(Destination.UpdatesSettings::class, Destination.UpdatesSettings.serializer())
            subclass(Destination.About::class, Destination.About.serializer())
        }
    }
}
val LocalBottomBarPadding = compositionLocalOf { PaddingValues(0.dp) }

private val rootDestinationIndex: Map<String, Int> =
    rootDestinations.withIndex().associate { (index, destination) -> destination.toString() to index }

private val forwardTransition: ContentTransform =
    slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
private val backTransition: ContentTransform =
    slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
private val pushUpTransition: ContentTransform =
    slideInVertically { it } togetherWith ExitTransition.None
private val popDownTransition: ContentTransform = ContentTransform(
    targetContentEnter = EnterTransition.None,
    initialContentExit = slideOutVertically { it },
    targetContentZIndex = -1f,
)

@Composable
internal fun App() {
    val recordInstallation = koinInject<RecordInstallationUseCase>()
    LaunchedEffect(Unit) { recordInstallation() }

    val syncWikiConfig = koinInject<OnLaunchSetupUseCase>()
    val overlayService = koinInject<OverlayService>()
    LaunchedEffect(Unit) {
        syncWikiConfig()
            .onSuccess { refreshFlow -> refreshFlow.collect() }
            .onError { error -> overlayService.show(error) }
    }

    FightingNerdTheme {
        Content()
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(navConfig, Destination.Home)
    val overlayService = koinInject<OverlayService>()
    val subscribeToUnseenRelease = koinInject<SubscribeToUnseenReleaseUseCase>()
    val saveReleaseAsSeen = koinInject<SaveReleaseAsSeenUseCase>()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(subscribeToUnseenRelease, overlayService) {
        subscribeToUnseenRelease().collect { release ->
            overlayService.show(
                Dialog(
                    content = { onDismiss ->
                        ChangelogDialog(
                            release = release,
                            onDismiss = {
                                scope.launch {
                                    saveReleaseAsSeen(release.version)
                                        .onError { error -> Napier.e(tag = TAG_CHANGELOG) { error.errorMessage } }
                                }
                                onDismiss()
                            },
                        )
                    },
                )
            )
        }
    }

    BottomBarPaddingProvider {
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
private fun BottomBarPaddingProvider(content: @Composable () -> Unit) {
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val botPaddingValues = remember(bottomInset) {
        PaddingValues(bottom = 80.dp + bottomInset)
    }
    CompositionLocalProvider(LocalBottomBarPadding provides botPaddingValues, content = content)
}

@Composable
private fun AppNavDisplay(
    backStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier,
) {
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        transitionSpec = {
            val from = rootDestinationIndex[initialState.key] ?: -1
            val to = rootDestinationIndex[targetState.key] ?: -1
            val bothTopLevel = (from >= 0) && (to >= 0)
            val spec = if (bothTopLevel) {
                if (to > from) forwardTransition else backTransition
            } else {
                pushUpTransition
            }
            spec
        },
        popTransitionSpec = {
            val from = rootDestinationIndex[initialState.key] ?: -1
            val to = rootDestinationIndex[targetState.key] ?: -1
            val bothTopLevel = (from >= 0) && (to >= 0)
            val spec = if (bothTopLevel) backTransition else popDownTransition
            spec
        },
        predictivePopTransitionSpec = { popDownTransition },
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        entryProvider = entryProvider {
            entry<Destination.Home> {
                HomeScreen(
                    onNavigateToMoveList = { gameId, characterId ->
                        backStack.add(Destination.MoveList(gameId = gameId, characterId = characterId))
                    },
                    onNavigateToFeatureSettings = {
                        backStack.add(Destination.FeatureSettings)
                    }
                )
            }
            entry<Destination.MoveList> { destination ->
                MoveListScreen(
                    gameId = destination.gameId,
                    characterId = destination.characterId,
                    onExit = { backStack.removeLastOrNull() },
                )
            }

            entry<Destination.QuizOverview> {
                QuizOverviewScreen(
                    onNavigateToQuiz = { gameId, characterId ->
                        backStack.add(Destination.Quiz(gameId = gameId, characterId = characterId))
                    }
                )
            }
            entry<Destination.Quiz> { destination ->
                QuizScreen(
                    gameId = destination.gameId,
                    characterId = destination.characterId,
                    onExit = { backStack.removeLastOrNull() },
                )
            }

            entry<Destination.More> {
                MoreScreen(
                    onNavigate = { moreItem ->
                        when (moreItem) {
                            MoreItem.FeatureSettings -> backStack.add(Destination.FeatureSettings)
                            MoreItem.UpdatesSettings -> backStack.add(Destination.UpdatesSettings)
                            MoreItem.About -> backStack.add(Destination.About)
//                            MoreItem.Theme -> {/* no navigation */}
                        }
                    }
                )
            }

            entry<Destination.FeatureSettings> {
                FeatureSettingsScreen(onExit = { backStack.removeLastOrNull() })
            }
            entry<Destination.UpdatesSettings> {
                UpdatesScreen(onExit = { backStack.removeLastOrNull() })
            }
            entry<Destination.About> {
                AboutScreen(onExit = { backStack.removeLastOrNull() })
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
    val currentDialog by overlayService.currentDialog.collectAsState()

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
            ToastSnackBar(toast = visuals.toast)
        }

        currentDialog?.let { dialog ->
            dialog.content { overlayService.popDialog() }
        }
    }
}

@Composable
private fun BoxScope.AppBottomBar(
    backStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier,
) {
    val topLevel = (backStack.lastOrNull() as? Destination)?.takeIf { it in rootDestinationSet }
    val lastTopLevel = remember { mutableStateOf<Destination?>(topLevel) }
    if (topLevel != null) lastTopLevel.value = topLevel

    AnimatedVisibility(
        visible = topLevel != null,
        enter = slideInVertically { it },
        exit = slideOutVertically { it },
        modifier = modifier
            .align(Alignment.BottomCenter)
            .navigationBarsPadding(),
    ) {
        val displayed = lastTopLevel.value
        if (displayed != null) {
            BottomNavBarView(
                currentRoot = displayed,
                onTabClick = { destination ->
                    backStack.clear()
                    backStack.add(destination)
                },
            )
        }
    }
}


private const val TAG_CHANGELOG = "Changelog"

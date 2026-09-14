package io.github.sophon.fightingnerd.feat.more.usecase

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.core.featureConfig.FeatureRepo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.fightingnerd.core.data.MediaRepo
import io.github.sophon.fightingnerd.feat.more.model.SettingsError
import io.github.sophon.fightingnerd.feat.more.ui.featureSettings.FeatureSettingsState.UiFeatureSetting
import io.github.sophon.fightingnerd.feat.more.util.featureKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.withContext

/**
 * Persists prefs first; side effects only run if the save committed.
 *
 * Invariant: an enabled feature must have clean, complete data.
 * - enabled + corrupt/partial data -> unacceptable
 * - disabled + need to re-enable and re-download -> acceptable
 *
 * On a successful save:
 * - each newly-disabled game has its cache wiped (best-effort; a clearCache failure is logged
 *   but does not roll back the disable in prefs).
 * - each newly-enabled game triggers a fire-and-forget download on the app-scope so the work
 *   survives if the caller navigates away.
 *
 * On a failed save no side effects run — the on-disk state and the wiki caches stay
 * consistent with each other.
 */
internal class SaveFeatureConfigUseCase(
    private val store: DataStore<Preferences>,
    private val featureRepo: FeatureRepo,
    private val mediaRepo: MediaRepo,
    private val scope: CoroutineScope,
) {
    suspend fun invoke(
        featureList: List<UiFeatureSetting>,
    ): EmptyResult<SettingsError> {
        val result = withContext(Dispatchers.IO) {
            val prefs = store.data.first()
            val disabledPairList = diffDisabled(newConfig = featureList, prefs = prefs)
            val enabledPairList = diffEnabled(newConfig = featureList, prefs = prefs)

            saveToStore(featureList).onSuccess {
                wipeCacheForDisabled(disabledPairList)
                triggerDownloadForEnabled(enabledPairList)
            }
        }
        return result
    }

    private suspend fun wipeCacheForDisabled(disabledPairList: List<Pair<String, String>>) {
        for ((_, gameId) in disabledPairList) {
            val game = Game.fromId(gameId)
            if (game == null) {
                Napier.w(tag = TAG) { "Unknown gameId in saved prefs: $gameId" }
                continue
            }
            val wikiClient = featureRepo.getWikiClientFor(game)
            if (wikiClient == null) {
                Napier.w(tag = TAG) { "No WikiClient registered for game: $gameId" }
                continue
            }

            wikiClient.clearCache().onError { error ->
                Napier.w(tag = TAG) { "Wipe failed for $gameId, proceeding to disable anyway: $error" }
            }
            mediaRepo.wipe(gameId = gameId)
        }
    }

    private fun triggerDownloadForEnabled(enabledPairList: List<Pair<String, String>>) {
        for ((_, gameId) in enabledPairList) {
            val game = Game.fromId(gameId)
            if (game == null) {
                Napier.w(tag = TAG) { "Unknown gameId to enable: $gameId" }
                continue
            }
            val wikiClient = featureRepo.getWikiClientFor(game)
            if (wikiClient == null) {
                Napier.w(tag = TAG) { "No WikiClient registered for game: $gameId" }
                continue
            }
            wikiClient.refreshData().launchIn(scope)
        }
    }

    private fun diffDisabled(
        newConfig: List<UiFeatureSetting>,
        prefs: Preferences,
    ): List<Pair<String, String>> {
        val result = mutableListOf<Pair<String, String>>()
        newConfig.forEach { feature ->
            feature.gameList.forEach { game ->
                val wasEnabled = prefs[featureKey(feature.featureName, game.id)] ?: true
                if (wasEnabled && game.isEnabled.not()) {
                    result.add(feature.featureName to game.id)
                }
            }
        }
        return result
    }

    private fun diffEnabled(
        newConfig: List<UiFeatureSetting>,
        prefs: Preferences,
    ): List<Pair<String, String>> {
        val result = mutableListOf<Pair<String, String>>()
        newConfig.forEach { feature ->
            feature.gameList.forEach { game ->
                val wasEnabled = prefs[featureKey(feature.featureName, game.id)] ?: false
                if (wasEnabled.not() && game.isEnabled) {
                    result.add(feature.featureName to game.id)
                }
            }
        }
        return result
    }

    private suspend fun saveToStore(featureList: List<UiFeatureSetting>): EmptyResult<SettingsError> {
        val result = try {
            store.edit { prefs ->
                featureList.forEach { feature ->
                    feature.gameList.forEach { game ->
                        prefs[featureKey(feature.featureName, game.id)] = game.isEnabled
                    }
                }
            }
            Result.Success(Unit)
        } catch (_: IOException) {
            Result.Error(SettingsError.IO_ERROR)
        }
        return result
    }


    private companion object {
        const val TAG = "SaveFeatureConfigUseCase"
    }
}

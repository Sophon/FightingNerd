package io.github.sophon.fightingnerd.feat.more.usecase

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.featureConfig.FeatureRepo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.fightingnerd.feat.more.model.SettingsError
import io.github.sophon.fightingnerd.feat.more.ui.featureSettings.FeatureSettingsState.UiFeatureSetting
import io.github.sophon.fightingnerd.feat.more.util.featureKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

/**
 * Wipes eagerly on any failure.
 *
 * Invariant: an enabled feature must have clean, complete data.
 * - enabled + corrupt/partial data -> unacceptable
 * - disabled + need to re-enable and re-download -> acceptable
 *
 * `clearCache` fails - still proceed to mark the feature disabled in prefs.
 *
 * Re-enabling will trigger a fresh download.
 */
internal class SaveFeatureConfigUseCase(
    private val store: DataStore<Preferences>,
    private val repo: FeatureRepo,
) {
    suspend fun invoke(
        featureList: List<UiFeatureSetting>,
    ): EmptyResult<SettingsError> {
        return withContext(Dispatchers.IO) {
            wipeCacheForFeature(featureList)
            saveToStore(featureList)
        }
    }

    private suspend fun wipeCacheForFeature(featureList: List<UiFeatureSetting>) {
        val disabledPairList = diffDisabled(newConfig = featureList)

        for ((_, gameId) in disabledPairList) {
            val game = Game.fromId(gameId)
            if (game == null) {
                Napier.w(tag = TAG) { "Unknown gameId in saved prefs: $gameId" }
                continue
            }
            val wikiClient = repo.getWikiClientFor(game)
            if (wikiClient == null) {
                Napier.w(tag = TAG) { "No WikiClient registered for game: $gameId" }
                continue
            }

            wikiClient.clearCache().onError { error ->
                Napier.w(tag = TAG) { "Wipe failed for $gameId, proceeding to disable anyway: $error" }
            }
        }
    }

    private suspend fun diffDisabled(
        newConfig: List<UiFeatureSetting>,
    ): List<Pair<String, String>> {
        val prefs = store.data.first()
        val result = mutableListOf<Pair<String, String>>()
        newConfig.forEach { feature ->
            feature.gameList.forEach { game ->
                val wasEnabled = prefs[featureKey(feature.featureName, game.id)] ?: false
                if (wasEnabled && game.isEnabled.not()) {
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

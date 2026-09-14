package io.github.sophon.fightingnerd.feat.more.usecase

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.FeatureRepo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.fightingnerd.feat.more.KEY_PREFIX_FEATURE
import io.github.sophon.fightingnerd.feat.more.model.FeatureSetting
import io.github.sophon.fightingnerd.feat.more.model.SettingsError
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.io.IOException
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
internal class SubscribeToAvailableFeaturesUseCase(
    private val featureRepo: FeatureRepo,
    private val store: DataStore<Preferences>,
) {
    operator fun invoke(): Flow<Result<List<FeatureSetting>, SettingsError>> {
        val gameClients: Map<Game, WikiClient> = featureRepo.getGameClients()
        val grouped = gameClients.entries.groupBy { it.value.featureInfo.name }
        val gameEntries = gameClients.entries.toList()

        val timestampFlows: List<Flow<Instant?>> = gameEntries.map { it.value.subscribeToLastUpdateTimestamp() }
        val combinedTimestamps: Flow<List<Instant?>> = if (timestampFlows.isEmpty()) {
            flowOf(emptyList())
        } else {
            combine(timestampFlows) { it.toList() }
        }

        val flow = combine(store.data, combinedTimestamps) { preferences, timestamps ->
            val timestampByGameId = gameEntries.mapIndexed { index, entry -> entry.key.id to timestamps[index] }.toMap()
            val gameConfigMap = grouped.entries.flatMap { (featureName, entries) ->
                entries.map { (game, _) ->
                    val key = booleanPreferencesKey("${KEY_PREFIX_FEATURE}_${featureName}_${game.id}")
                    game.id to (preferences[key] ?: false)
                }
            }.toMap()

            val list = grouped.map { (_, entries) ->
                val wikiClient = entries.first().value
                val featureInfo = wikiClient.featureInfo

                FeatureSetting(
                    name = featureInfo.name,
                    iconUrl = featureInfo.iconUrl.orEmpty(),
                    url = featureInfo.url,
                    version = featureInfo.version,
                    gameList = entries
                        .map { (game, _) ->
                            FeatureSetting.FeatureGame(
                                name = game.displayName,
                                id = game.id,
                                isEnabled = gameConfigMap[game.id] ?: false,
                                lastUpdatedTimeStamp = timestampByGameId[game.id],
                            )
                        }
                        .toImmutableList(),
                )
            }
            val result: Result<List<FeatureSetting>, SettingsError> = Result.Success(list)
            result
        }.catch { throwable ->
            val error = if (throwable is IOException) SettingsError.IO_ERROR else SettingsError.UNKNOWN
            emit(Result.Error(error))
        }
        return flow
    }
}

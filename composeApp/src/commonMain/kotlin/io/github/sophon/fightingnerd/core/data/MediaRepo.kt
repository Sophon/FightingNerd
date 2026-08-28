package io.github.sophon.fightingnerd.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringSetPreferencesKey
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.wiki.model.CharacterId
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.fightingnerd.core.model.AppError
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.readRawBytes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import okio.FileSystem
import okio.Path

internal interface MediaRepo {
    fun subscribeToCharsWithOfflineMedia(gameId: String): Flow<Set<CharacterId>>
    suspend fun save(gameId: String, characterId: CharacterId, media: Move.Urls): EmptyResult<AppError>
    suspend fun wipe(gameId: String)
    suspend fun wipe(gameId: String, characterId: CharacterId)
    suspend fun createUpdatedUrls(
        gameId: String,
        characterId: CharacterId,
        media: Move.Urls,
    ): Move.Urls
}


internal class MediaRepoImpl(
    private val fs: FileSystem,
    private val baseDir: Path,
    private val http: HttpClient,
    private val store: DataStore<Preferences>,
) : MediaRepo {
    override fun subscribeToCharsWithOfflineMedia(gameId: String): Flow<Set<CharacterId>> {
        val flow = store.data
            .catch { emit(emptyPreferences()) }
            .map { prefs ->
                val raw = prefs[offlineCharsKey(gameId)].orEmpty()
                val charIdSet = raw.mapTo(mutableSetOf()) { CharacterId(it) }
                return@map charIdSet
            }
        return flow
    }

    override suspend fun save(
        gameId: String,
        characterId: CharacterId,
        media: Move.Urls,
    ): EmptyResult<AppError> {
        try {
            val charDir = baseDir / gameId / characterId.value
            fs.createDirectories(charDir)
            val urls = listOfNotNull(media.videoUrl) + media.hitboxImageList + media.moveImageList
            urls.forEach { url ->
                val target = charDir / toStorageFileName(url)
                val bytes = http.get(url).readRawBytes()
                fs.write(target) { write(bytes) }
            }
            store.edit { prefs ->
                val key = offlineCharsKey(gameId)
                val current = prefs[key].orEmpty()
                if (characterId.value !in current) {
                    prefs[key] = current + characterId.value
                }
            }
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Error(AppError.IOError(e.message.orEmpty()))
        }
    }

    override suspend fun wipe(gameId: String) {
        val gameDir = baseDir / gameId
        fs.deleteRecursively(gameDir, mustExist = false)
        store.edit { prefs -> prefs.remove(offlineCharsKey(gameId)) }
    }

    override suspend fun wipe(gameId: String, characterId: CharacterId) {
        val charDir = baseDir / gameId / characterId.value
        fs.deleteRecursively(charDir, mustExist = false)
        store.edit { prefs ->
            val key = offlineCharsKey(gameId)
            val current = prefs[key].orEmpty()
            if (characterId.value in current) {
                prefs[key] = current - characterId.value
            }
        }
    }

    override suspend fun createUpdatedUrls(
        gameId: String,
        characterId: CharacterId,
        media: Move.Urls,
    ): Move.Urls {
        val charDir = baseDir / gameId / characterId.value
        val updated = try {
            media.copy(
                videoUrl = media.videoUrl?.let { toLocalUrl(charDir, it) },
                hitboxImageList = media.hitboxImageList.map { toLocalUrl(charDir, it) },
                moveImageList = media.moveImageList.map { toLocalUrl(charDir, it) },
            )
        } catch (e: Exception) {
            media
        }
        return updated
    }

    private fun toLocalUrl(charDir: Path, url: String): String {
        val local = charDir / toStorageFileName(url)
        val link = if (fs.exists(local)) "file://$local" else url
        return link
    }

    private fun toStorageFileName(url: String): String {
        val name = url
            .substringAfterLast("/")
            .replace(Regex("[%:]"), "_")
        return name
    }


    private companion object {
        private const val KEY_PREFIX_OFFLINE_CHARS = "offline_chars"

        fun offlineCharsKey(gameId: String): Preferences.Key<Set<String>> {
            return stringSetPreferencesKey("${KEY_PREFIX_OFFLINE_CHARS}_$gameId")
        }
    }
}

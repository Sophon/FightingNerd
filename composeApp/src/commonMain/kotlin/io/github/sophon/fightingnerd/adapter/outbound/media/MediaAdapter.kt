package io.github.sophon.fightingnerd.adapter.outbound.media

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringSetPreferencesKey
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.Move
import io.github.sophon.fightingnerd.app.outPort.MediaPort
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.readRawBytes
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import okio.FileSystem
import okio.IOException
import okio.Path

@ExcludeFromCoverage("TODO: needs a fake file system and HTTP engine")
internal class MediaAdapter(
    private val fs: FileSystem,
    private val baseDir: Path,
    private val http: HttpClient,
    private val store: DataStore<Preferences>,
): MediaPort {
    override fun subscribeToCharactersWithOfflineMedia(gameId: String): Flow<Set<String>> {
        val flow = store.data
            .catch { emit(emptyPreferences()) }
            .map { preferences ->
                val characterIdSet = preferences[offlineCharactersKey(gameId)].orEmpty()
                characterIdSet
            }
        return flow
    }

    override suspend fun save(
        gameId: String,
        characterId: String,
        urls: Move.Urls,
    ): EmptyResult<AppError> {
        val result = runCatchingIO {
            val characterDir = (baseDir / gameId / characterId)
            fs.createDirectories(characterDir)
            val urlList = (listOfNotNull(urls.videoUrl) + urls.hitboxImageList + urls.moveImageList)
            urlList.forEach { url ->
                val target = (characterDir / toStorageFileName(url))
                val bytes = http.get(url).readRawBytes()
                fs.write(target) { write(bytes) }
            }
            store.edit { preferences ->
                val key = offlineCharactersKey(gameId)
                preferences[key] = (preferences[key].orEmpty() + characterId)
            }
        }
        return result
    }

    override suspend fun wipe(gameId: String, characterId: String): EmptyResult<AppError> {
        val result = runCatchingIO {
            fs.deleteRecursively(baseDir / gameId / characterId, mustExist = false)
            store.edit { preferences ->
                val key = offlineCharactersKey(gameId)
                preferences[key] = (preferences[key].orEmpty() - characterId)
            }
        }
        return result
    }

    override fun toOfflineUrls(
        gameId: String,
        characterId: String,
        urls: Move.Urls,
    ): Move.Urls {
        val characterDir = (baseDir / gameId / characterId)
        val offlineUrls = try {
            urls.copy(
                videoUrl = urls.videoUrl?.let { url -> toLocalUrl(characterDir, url) },
                hitboxImageList = urls.hitboxImageList.map { url -> toLocalUrl(characterDir, url) },
                moveImageList = urls.moveImageList.map { url -> toLocalUrl(characterDir, url) },
            )
        } catch (e: IOException) {
            urls
        }
        return offlineUrls
    }


    private suspend fun runCatchingIO(block: suspend () -> Unit): EmptyResult<AppError> {
        val result = try {
            block()
            Result.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Error(AppError.IOError(e.message.orEmpty()))
        }
        return result
    }

    private fun toLocalUrl(characterDir: Path, url: String): String {
        val local = (characterDir / toStorageFileName(url))
        val link = if (fs.exists(local)) "file://$local" else url
        return link
    }

    private fun toStorageFileName(url: String): String {
        val name = url
            .substringAfterLast("/")
            .replace(Regex("[%:]"), "_")
        return name
    }
}


/**
 * Same key as the legacy `MediaRepoImpl` - both read and write the same offline characters.
 */
private fun offlineCharactersKey(gameId: String): Preferences.Key<Set<String>> {
    val key = stringSetPreferencesKey("${KEY_PREFIX_OFFLINE_CHARACTERS}_$gameId")
    return key
}

private const val KEY_PREFIX_OFFLINE_CHARACTERS = "offline_chars"

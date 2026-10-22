package io.github.sophon.wiki.app.outPort

import io.github.sophon.wiki.model.WikiConfig
import kotlinx.coroutines.flow.Flow

internal interface LoadWikiConfigPort {
    fun subscribe(): Flow<WikiConfig?>
}

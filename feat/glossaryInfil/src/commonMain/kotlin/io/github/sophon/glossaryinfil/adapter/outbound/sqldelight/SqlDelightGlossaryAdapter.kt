package io.github.sophon.glossaryinfil.adapter.outbound.sqldelight

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.glossaryinfil.app.outPort.CountGlossaryItemsPort
import io.github.sophon.glossaryinfil.app.outPort.LoadGlossaryItemListPort
import io.github.sophon.glossaryinfil.app.outPort.ReplaceGlossaryPort
import io.github.sophon.glossaryinfil.model.GlossaryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class SqlDelightGlossaryAdapter(
    glossaryDatabase: LazyGlossaryDB,
): ReplaceGlossaryPort, LoadGlossaryItemListPort, CountGlossaryItemsPort {
    private val database by glossaryDatabase

    override suspend fun replace(itemList: List<GlossaryItem>): EmptyResult<DataError.Local> {
        val result = runQuery("replace(${itemList.size} items)") {
            database.transaction {
                database.glossaryAliasQueries.deleteAll()
                database.glossaryItemQueries.deleteAll()
                itemList.forEach { item -> insert(item) }
            }
        }
        return result
    }

    override suspend fun load(
        query: String,
        compactQuery: String,
    ): Result<List<GlossaryItem>, DataError.Local> {
        val result = runQuery("load($query)") {
            database.glossaryItemQueries
                .selectByAlias(
                    query = query.escapeWildcards(),
                    compactQuery = compactQuery.escapeWildcards(),
                    mapper = ::toGlossaryItem,
                )
                .executeAsList()
        }
        return result
    }

    override suspend fun count(): Result<Long, DataError.Local> {
        val result = runQuery("count()") {
            database.glossaryItemQueries.count().executeAsOne()
        }
        return result
    }

    private fun insert(item: GlossaryItem) {
        database.glossaryItemQueries.insert(
            term = item.term,
            definition = item.definition,
            alt_term_list = item.altTerm,
            game_list = item.games,
            jp_translation_list = item.jpTranslation,
            term_url = item.url.term,
            video_url = item.url.video,
            image_url = item.url.image,
        )
        (listOf(item.term) + item.altTerm).forEach { alias ->
            database.glossaryAliasQueries.insert(alias = alias, term = item.term)
        }
    }

    /**
     * Runs [query] on [Dispatchers.IO] - the ports only see [DataError.Local], so the exception is logged here.
     */
    private suspend fun <T> runQuery(
        description: String,
        query: () -> T,
    ): Result<T, DataError.Local> {
        val result = withContext(Dispatchers.IO) {
            try {
                Result.Success(query())
            } catch (e: Exception) {
                Napier.e(throwable = e, tag = TAG) { "$description failed" }
                Result.Error(DataError.Local.UNKNOWN)
            }
        }
        return result
    }

    // the search is a LIKE - a `%` or `_` typed by the user is a character, not a wildcard
    private fun String.escapeWildcards(): String {
        val escaped = this
            .replace("$LIKE_ESCAPE", "$LIKE_ESCAPE$LIKE_ESCAPE")
            .replace("%", "$LIKE_ESCAPE%")
            .replace("_", "${LIKE_ESCAPE}_")
        return escaped
    }

    private fun toGlossaryItem(
        term: String,
        definition: String,
        altTermList: List<String>,
        gameList: List<String>,
        jpTranslationList: List<String>,
        termUrl: String,
        videoUrl: String?,
        imageUrl: String?,
    ): GlossaryItem {
        val item = GlossaryItem(
            term = term,
            definition = definition,
            altTerm = altTermList,
            games = gameList,
            jpTranslation = jpTranslationList,
            url = GlossaryItem.Url(
                term = termUrl,
                video = videoUrl,
                image = imageUrl,
            ),
        )
        return item
    }


    private companion object {
        const val TAG = "SqlDelightGlossaryAdapter"

        // must match the ESCAPE character in GlossaryItem.sq
        const val LIKE_ESCAPE = '!'
    }
}

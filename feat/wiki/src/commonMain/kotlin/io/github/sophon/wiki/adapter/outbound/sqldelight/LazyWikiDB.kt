package io.github.sophon.wiki.adapter.outbound.sqldelight

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import io.github.sophon.wiki.data.Character
import io.github.sophon.wiki.data.Move
import io.github.sophon.wiki.data.WikiDB
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

/**
 * Opens [WikiDB] on first use, not when Koin builds the adapters - opening checks the schema fingerprint
 * and may recreate the file, so it belongs on `Dispatchers.IO`.
 */
internal class LazyWikiDB(openDriver: () -> SqlDriver) : Lazy<WikiDB> by lazy({ createWikiDB(openDriver()) })

private fun createWikiDB(driver: SqlDriver): WikiDB {
    val database = WikiDB(
        driver = driver,
        characterAdapter = Character.Adapter(
            umoAdapter = StringListAdapter,
        ),
        moveAdapter = Move.Adapter(
            notesAdapter = StringListAdapter,
            hitbox_image_listAdapter = StringListAdapter,
            move_image_listAdapter = StringListAdapter,
        ),
    )
    return database
}

private object StringListAdapter : ColumnAdapter<List<String>, String> {
    private val serializer = ListSerializer(String.serializer())

    override fun decode(databaseValue: String): List<String> {
        val decoded = Json.decodeFromString(serializer, databaseValue)
        return decoded
    }

    override fun encode(value: List<String>): String {
        val encoded = Json.encodeToString(serializer, value)
        return encoded
    }
}

package io.github.sophon.glossaryinfil.adapter.outbound.sqldelight

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import io.github.sophon.glossaryinfil.data.GlossaryDB
import io.github.sophon.glossaryinfil.data.Glossary_item
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

/**
 * Opens [GlossaryDB] on first use, not when Koin builds the adapter - opening checks the schema fingerprint
 * and may recreate the file, so it belongs on `Dispatchers.IO`.
 */
internal class LazyGlossaryDB(openDriver: () -> SqlDriver) : Lazy<GlossaryDB> by lazy({ createGlossaryDB(openDriver()) })

private fun createGlossaryDB(driver: SqlDriver): GlossaryDB {
    val database = GlossaryDB(
        driver = driver,
        glossary_itemAdapter = Glossary_item.Adapter(
            alt_term_listAdapter = StringListAdapter,
            game_listAdapter = StringListAdapter,
            jp_translation_listAdapter = StringListAdapter,
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

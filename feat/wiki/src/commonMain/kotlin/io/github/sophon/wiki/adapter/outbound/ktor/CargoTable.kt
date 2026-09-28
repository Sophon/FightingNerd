package io.github.sophon.wiki.adapter.outbound.ktor

/**
 * A MediaWiki Cargo table and the fields we query from it.
 */
internal data class CargoTable(
    val name: String,
    val fieldList: List<String>,
)

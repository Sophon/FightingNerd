package io.github.sophon.wiki.adapter.outbound.ktor

internal data class BucketTable(
    val name: String,
    val fieldList: List<String>,
) {
    fun toSelect(): String {
        val fields = fieldList.joinToString(",") { field -> "'$field'" }
        val select = "bucket('$name').select($fields)"
        return select
    }
}

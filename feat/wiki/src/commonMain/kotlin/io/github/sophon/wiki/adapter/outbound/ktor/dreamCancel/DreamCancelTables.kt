package io.github.sophon.wiki.adapter.outbound.ktor.dreamCancel

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.wiki.adapter.outbound.ktor.CargoTable

internal object DreamCancelTables {
    val moveTableByGame = mapOf(
        Game.KoFXV to CargoTable(name = "MoveData_KOFXV", fieldList = moveFieldList + "stun"),
        Game.COTW to CargoTable(name = "MoveData_COTW", fieldList = moveFieldList + "revdamage"),
    )
}


private val moveFieldList = listOf(
    "chara",
    "moveId",
    "name",
    "idle",
    "rank",
    "input",
    "images",
    "hitboxes",
    "damage",
    "guard",
    "cancel",
    "startup",
    "active",
    "recovery",
    "hitadv",
    "blockadv",
    "invul",
    "guardDamage",
)

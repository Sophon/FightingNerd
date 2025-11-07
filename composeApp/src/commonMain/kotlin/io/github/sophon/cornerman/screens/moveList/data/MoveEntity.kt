package io.github.sophon.cornerman.screens.moveList.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "moves",
    indices = [
        Index(value = ["input"]),
        Index(value = ["charName"]),
    ]
)
data class MoveEntity(
    val charName: String,
    @PrimaryKey val id: String,
    val input: String,
    val name: String?,
    val damage: String?,
    val startup: String?,
    val onBlock: String?,
    val onHit: String?,
    val onCH: String?,
    val notes: String?,
    val recovery: String?,
    val aliases: String?,
    val videoId: String?,

    val t8level: String?,
    val t8isHeat: Boolean? = null,
    val t8isPowerCrush: Boolean? = null,
    val t8isHoming: Boolean? = null,
    val t8stance: String? = null,
    val t8isHighCrush: Boolean? = null,
    val t8isLowCrush: Boolean? = null,
)

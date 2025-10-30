package com.example.cornerman.screens.moveList.data

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
    val level: String?,
    val name: String?,
    val parent: String?,
    val damage: String?,
    val startup: String?,
    val recoveryOnWhiff: String?,
    val totalFrames: String?,
    val crushes: String?,
    val onBlock: String?,
    val onHit: String?,
    val onCH: String?,
    val notes: String?,
    val aliases: String?,
    val image: String?,
    val videoId: String?,
    val alt: String?,

    val isHeat: Boolean = false,
    val isPowerCrush: Boolean = false,
    val isHoming: Boolean = false,
)

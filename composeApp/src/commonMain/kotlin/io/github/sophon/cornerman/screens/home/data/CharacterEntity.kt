package io.github.sophon.cornerman.screens.home.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "characters",
    indices = [
        Index(value =["displayName"]),
    ]
)
data class CharacterEntity(
    @PrimaryKey val id: String,
    val displayName: String,
    val wikiUrl: String,
    val aliases: String? = null,

    val imageIconUrl: String? = null,
    val imageBannerUrl: String? = null,
)

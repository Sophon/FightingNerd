package io.github.sophon.fightingnerd.feat.home.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "characters",
    indices = [
        Index(value =["displayName"]),
    ]
)
internal data class CharacterEntity(
    @PrimaryKey val id: String,
    val displayName: String,
    val queryName: String,
    val wikiUrl: String,
    val aliasList: String? = null,

    val imageIconUrl: String? = null,
    val imageBannerUrl: String? = null,

    val hp: String? = null,
    val umo: String? = null,

    val sf6FwdWalkSpd: String? = null,
    val sf6BwdWalkSpd: String? = null,
    val sf6FwdDashSpd: String? = null,
    val sf6BwdDashSpd: String? = null,
    val sf6FwdDashDist: String? = null,
    val sf6BwdDashDist: String? = null,
    val sf6DRushMin: String? = null,
    val sf6DRushBlock: String? = null,
    val sf6DRushMax: String? = null,
    val sf6Hp: String? = null,
    val sf6ThrowRange: String? = null,
    val sf6ThrowHurtbox: String? = null,
    val sf6JumpSpd: String? = null,
    val sf6JumpApex: String? = null,
    val sf6FwdJumpDist: String? = null,
    val sf6BwdJumpDist: String? = null,
)

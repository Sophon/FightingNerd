package io.github.sophon.fightingnerd.screens.moveList.data

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
    val name: String?,

    val input: String,
    val damage: String?,
    val startup: String?,
    val onBlock: String?,
    val onHit: String?,
    val onCH: String?,
    val active: String?,
    val cancel: String?,
    val recovery: String?,
    val guard: String?,
    val invulnerability: String?,

    val notes: String?,
    val aliases: String?,

    val urlsChracterWiki: String?,
    val urlsVideoId: String?,
    val urlsHitboxImage: String?,

    val t8isHeat: Boolean? = null,
    val t8isPowerCrush: Boolean? = null,
    val t8isHoming: Boolean? = null,
    val t8stance: String? = null,
    val t8isHighCrush: Boolean? = null,
    val t8isLowCrush: Boolean? = null,

    val sf6Type: String? = null,
    val sf6Images: String? = null,
    val sf6Hitboxes: String? = null,
    val sf6Chip: String? = null,
    val sf6DmgScaling: String? = null,
    val sf6Total: String? = null,
    val sf6Cancel: String? = null,
    val sf6HitConfirm: String? = null,
    val sf6PunishAdv: String? = null,
    val sf6PerfParryAdv: String? = null,
    val sf6DRcOH: String? = null,
    val sf6DRcOB: String? = null,
    val sf6DROH: String? = null,
    val sf6DROB: String? = null,
    val sf6HitStun: String? = null,
    val sf6BlockStun: String? = null,
    val sf6HitStop: String? = null,
    val sf6DriveDmgOnBlock: String? = null,
    val sf6DriveDmgOnHit: String? = null,
    val sf6DriveGain: String? = null,
    val sf6SuperGainOnHit: String? = null,
    val sf6SuperGainOnBlock: String? = null,
    val sf6Invulnerability: String? = null,
    val sf6Armor: String? = null,
    val sf6Airborne: String? = null,
    val sf6JugStart: String? = null,
    val sf6JugIncrease: String? = null,
    val sf6JugLimit: String? = null,
    val sf6ProjectileSpeed: String? = null,
    val sf6AttackRange: String? = null,


)
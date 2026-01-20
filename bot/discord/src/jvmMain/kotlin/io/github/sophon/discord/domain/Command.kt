package io.github.sophon.discord.domain

/**
 * Add registered service commands HERE
 */
enum class Command {
    //core
    TIP,
    DONATE,
    REPO,
    INVITE,
    HELP,
    COMMANDS,
    EXAMPLES,
    JOIN,

    //admin
    FEEDBACK,
    REPLY,
    BAN,
    UNBAN,
    BANLIST,

    //general
    FD,

    //infil glossary
    GL,

    //wavu wiki
    FDTK,
    PC,
    HEAT,
    HOMING,
    STANCE,
    ALIASTK,
    THROWTK,

    //supercombo wiki
    FDSF,
    CHARSF,
    FDMK,
    CHARMK,

    //2xko wiki
    FDXKO,

    //dreamcancel wiki
    FDKOF,
    FDCOTW,
    ALIASKOF,
    ALIASCOTW,

    //dustloop wiki
    CHARGG,
    FDGG,
    INVGG,
    ALIASGG,

    CHARDB,
    FDDB,

    CHARGB,
    FDGB,
    ALIASDB,

    ALIASBB,
    CHARBB,
    FDBB,
    INVBB,

    //mizuumi
    ALIASMB,
    FDMB,
    INVMB,

    CHARUNI,
    FDUNI,
    INVUNI,

    ALIASVS,
    FDVS,
    INVVS,
}
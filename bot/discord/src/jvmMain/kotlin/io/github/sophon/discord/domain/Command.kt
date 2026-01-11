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
    CHARDB,
    CHARGB,
    FDGG,
    FDDB,
    FDGB,
    ALIASDB,
    ALIASBB,
    CHARBB,
    FDBB,

    //mizuumi
    ALIASMB,
    FDMB,
    CHARUNI,
    FDUNI,
    FDVS,
}
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

    //dustloop wiki
    CHARGG,
    CHARDB,
    CHARGB,
    FDGG,
    FDDB,
    FDGB,
    ALIAS,
}
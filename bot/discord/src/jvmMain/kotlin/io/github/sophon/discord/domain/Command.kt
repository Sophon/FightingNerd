package io.github.sophon.discord.domain

/**
 * Add registered feature commands HERE
 */

internal sealed class Command(val description: String) {
    object Tip : Command(description = "Dono arigato!")
    object Donate : Command(description = "Dono arigato!")
    object Repo : Command(description = "Project repository")
    object Invite : Command(description = "Bot invite link")
    object Help : Command(description = "RTFM")
    object Commands : Command(description = "Available commands")
    object Examples : Command(description = "Command examples")
    object Join : Command(description = "Make clickable Steam lobby link")

    // Admin commands
    object Reply : Command(description = "Answer feedback")
    object Ban : Command(description = "Ban user from using the bot")
    object Unban : Command(description = "Unban user from using the bot")
    object Banlist : Command(description = "List of users banned from using the bot")

    object Fd : Command(description = "Global frame data")

    object Gl: Command(description = "Fighting-game glossary")

    // Wavu
    object FdTK : Command(description = "Tekken frame data")
    object Pc : Command(description = "Tekken POWER CRUSH moves")
    object Heat : Command(description = "Tekken HEAT moves")
    object Homing : Command(description = "Tekken HOMING moves")
    object Stance : Command(description = "Tekken STANCE moves")
    object AliasTK : Command(description = "Tekken character aliases")
    object ThrowTK : Command(description = "Tekken THROW moves")

    // SuperCombo
    object FdSF : Command(description = "SF6 frame data")
    object CharSF : Command(description = "SF6 character data")
    object CharMK : Command(description = "MK1 character data")
    object FdMK : Command(description = "MK1 frame data")

    // 2xko
    object FdXko : Command(description = "2XKO frame data")

    // DreamCancel
    object FdKOF : Command(description = "KOF frame data")
    object AliasKOF : Command(description = "KOF character aliases")
    object FdCOTW : Command(description = "COTW frame data")
    object AliasCOTW : Command(description = "COTW character aliases")

    // DustLoop commands
    object CharGG : Command(description = "GG character data")
    object FdGG : Command(description = "GG frame data")
    object InvGG : Command(description = "GG invincible moves")
    object AliasGG : Command(description = "GG character aliases")

    object CharDB : Command(description = "DB character data")
    object FdDB : Command(description = "DB frame data")
    object AliasDB : Command(description = "DB character aliases")

    object CharBB : Command(description = "BB character data")
    object FdBB : Command(description = "BB frame data")
    object AliasBB : Command(description = "BB character aliases")
    object InvBB : Command(description = "BB invincible moves")

    object CharGB : Command(description = "GB character data")
    object FdGB : Command(description = "GB frame data")

    // Mizuumi
    object FdMB : Command(description = "MBTL frame data")
    object AliasMB : Command(description = "MBTL character aliases")
    object InvMB : Command(description = "MBTL Invincible moves")

    object FdUNI : Command(description = "Uni2 frame data")
    object CharUNI : Command(description = "Uni2 character data")
    object InvUNI : Command(description = "UNI2 Invincible moves")

    object FdVS : Command(description = "VSAV frame data")
    object InvVS : Command(description = "VSAV Invincible moves")
    object AliasVS : Command(description = "VSAV character aliases")


    companion object {
        val entries: List<Command> by lazy {
            Command::class.sealedSubclasses
                .mapNotNull { it.objectInstance }
        }
    }
}

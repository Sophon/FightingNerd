package io.github.sophon.discord.domain

/**
 * Add registered feature commands HERE
 */

internal sealed class Command(
    val name: String,
    val description: String,
    val argumentList: List<Argument> = listOf()
) {
    object Tip : Command(name = "Tip", description = "Dono arigato!")
    object Donate : Command(name = "Donate", description = "Dono arigato!")
    object Repo : Command(name = "Repo", description = "Project repository")
    object Invite : Command(name = "Invite", description = "Bot invite link")
    object Help : Command(name = "Help", description = "RTFM")
    object Modules : Command(name = "Modules", description = "Features")
    object Commands : Command(name = "Commands", description = "Available commands")
    object Examples : Command(name = "Examples", description = "Command examples")
    object Join : Command(
        name = "Join",
        description = "Make clickable Steam lobby link",
        argumentList = listOf(
            Argument(
                name = "url",
                description = "Steam lobby URL",
            ),
            Argument(
                name = "password",
                description = "Lobby password",
                isRequired = false,
            ),
            Argument(
                name = "name",
                description = "Lobby name",
                isRequired = false,
            )
        )
    )

    //region Admin commands
    object Feedback : Command(
        name = "Feedback",
        description = "Provide feedback",
        argumentList = listOf(
            Argument(
                name = "feedback",
                description = "Feedback",
            ),
        )
    )
    object Reply : Command(
        name = "Reply",
        description = "Answer feedback",
        argumentList = listOf(
            Argument(
                name = "recipient",
                description = "username-id-serverId",
            ),
            Argument(
                name = "message",
                description = "Reply"
            )
        )
    )
    object Ban : Command(
        name = "Ban",
        description = "Ban user from using the bot",
        argumentList = listOf(
            Argument(
                name = "ban",
                description = "User ID",
            )
        )
    )
    object Unban : Command(
        name = "Unban",
        description = "Unban user from using the bot",
        argumentList = listOf(
            Argument(
                name = "unban",
                description = "User ID",
            )
        )
    )
    object Banlist : Command(name = "Banlist", description = "List of users banned from using the bot")
    //endregion

    object Fd : Command(
        name = "Fd",
        description = "Global frame data",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
            ),
            Argument(
                name = "move",
                description = "Move",
            ),
        ),
    )

    object Gl: Command(
        name = "Gl",
        description = "Fighting-game glossary",
        argumentList = listOf(
            Argument(
                name = "term",
                description = "Term",
            ),
        )
    )

    //region Wavu
    object FdTK : Command(
        name = "FdTK",
        description = "Tekken frame data",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
            ),
            Argument(
                name = "move",
                description = "Move",
            ),
        ),
    )
    object Pc : Command(
        name = "Pc",
        description = "Tekken POWER CRUSH moves",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
            ),
        ),
    )
    object Heat : Command(
        name = "Heat",
        description = "Tekken HEAT moves",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
            ),
        ),
    )
    object Homing : Command(
        name = "Homing",
        description = "Tekken HOMING moves",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
            ),
        ),
    )
    object Stance : Command(
        name = "Stance",
        description = "Tekken STANCE moves",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
            ),
            Argument(
                name = "stance",
                description = "Stance",
                isRequired = false,
            ),
        ),
    )
    object AliasTK : Command(name = "AliasTK", description = "Tekken character aliases")
    object ThrowTK : Command(
        name = "ThrowTK",
        description = "Tekken THROW moves",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
            ),
        ),
    )
    object Strings : Command(
        name = "Strings",
        description = "Tekken string FOLLOWUPS",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
            ),
            Argument(
                name = "move",
                description = "Starting with",
            ),
        ),
    )
    //endregion

    //region SuperCombo
    object FdSF : Command(
        name = "FdSF",
        description = "SF6 frame data",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
            ),
            Argument(
                name = "move",
                description = "Move",
            ),
        ),
    )
    object CharSF : Command(
        name = "CharSF",
        description = "SF6 character data",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
            ),
        ),
    )
    object CharMK : Command(
        name = "CharMK",
        description = "MK1 character data",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
            ),
        ),
    )
    object FdMK : Command(
        name = "FdMK",
        description = "MK1 frame data",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
            ),
            Argument(
                name = "move",
                description = "Move",
            ),
        ),
    )
    //endregion

    // region 2xko
    object FdXko : Command(
        name = "FdXko",
        description = "2XKO frame data",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
            ),
            Argument(
                name = "move",
                description = "Move",
            ),
        ),
    )

    // DreamCancel
    object FdKOF : Command(
        name = "FdKOF",
        description = "KOF frame data",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
            ),
            Argument(
                name = "move",
                description = "Move",
            ),
        ),
    )
    object AliasKOF : Command(
        name = "AliasKOF",
        description = "KOF character aliases",
        argumentList = listOf(),
    )
    object FdCOTW : Command(
        name = "FdCOTW",
        description = "COTW frame data",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
            ),
            Argument(
                name = "move",
                description = "Move",
            ),
        ),
    )
    object AliasCOTW : Command(
        name = "AliasCOTW",
        description = "COTW character aliases",
        argumentList = listOf(),
    )
    //endregion

    //region DustLoop commands
    object CharGG : Command(
        name = "CharGG",
        description = "GG character data",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
            ),
        ),
    )
    object FdGG : Command(
        name = "FdGG",
        description = "GG frame data",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
            ),
            Argument(
                name = "move",
                description = "Move",
            ),
        ),
    )
    object InvGG : Command(
        name = "InvGG",
        description = "GG invincible moves",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
            ),
        ),
    )
    object AliasGG : Command(name = "AliasGG", description = "GG character aliases")

    object CharDB : Command(
        name = "CharDB",
        description = "DB character data",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
            ),
        ),
    )
    object FdDB : Command(
        name = "FdDB",
        description = "DB frame data",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
            ),
            Argument(
                name = "move",
                description = "Move",
            ),
        ),
    )
    object AliasDB : Command(name = "AliasDB", description = "DB character aliases")

    object CharBB : Command(
        name = "CharBB",
        description = "BB character data",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
            ),
        ),
    )
    object FdBB : Command(
        name = "FdBB",
        description = "BB frame data",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
            ),
            Argument(
                name = "move",
                description = "Move",
            ),
        ),
    )
    object AliasBB : Command(name = "AliasBB", description = "BB character aliases")
    object InvBB : Command(
        name = "InvBB",
        description = "BB invincible moves",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
            ),
        ),
    )

    object CharGB : Command(
        name = "CharGB",
        description = "GB character data",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
            ),
        ),
    )
    object FdGB : Command(
        name = "FdGB",
        description = "GB frame data",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
            ),
            Argument(
                name = "move",
                description = "Move",
            ),
        ),
    )
    //endregion

    // region Mizuumi
    object FdMB : Command(
        name = "FdMB",
        description = "MBTL frame data",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
            ),
            Argument(
                name = "move",
                description = "Move",
            ),
        ),
    )
    object AliasMB : Command(name = "AliasMB", description = "MBTL character aliases")
    object InvMB : Command(
        name = "InvMB",
        description = "MBTL Invincible moves",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
            ),
        ),
    )

    object FdUNI : Command(
        name = "FdUNI",
        description = "Uni2 frame data",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
            ),
            Argument(
                name = "move",
                description = "Move",
            ),
        ),
    )
    object CharUNI : Command(
        name = "CharUNI",
        description = "Uni2 character data",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
            ),
        ),
    )
    object InvUNI : Command(
        name = "InvUNI",
        description = "UNI2 Invincible moves",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
            ),
        ),
    )

    object FdVS : Command(
        name = "FdVS",
        description = "VSAV frame data",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
            ),
            Argument(
                name = "move",
                description = "Move",
            ),
        ),
    )
    object InvVS : Command(
        name = "InvVS",
        description = "VSAV Invincible moves",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
            ),
        ),
    )
    object AliasVS : Command(name = "AliasVS", description = "VSAV character aliases")
    //endregion



    data class Argument(
        val name: String,
        val description: String,
        val isRequired: Boolean = true,
    )

    companion object {
        val entries: List<Command> by lazy {
            Command::class.sealedSubclasses
                .mapNotNull { it.objectInstance }
        }
    }
}
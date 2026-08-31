package io.github.sophon.discord.feat.core.domain.model

internal sealed class Command(
    val name: String,
    val description: String,
    val argumentList: List<Argument> = listOf()
) {
    //region Core
    object Tip : Command(name = "Tip", description = "Dono arigato!")
    object Donate : Command(name = "Donate", description = "Dono arigato!")
    object Repo : Command(name = "Repo", description = "Project repository")
    object Invite : Command(name = "Invite", description = "Bot invite link")
    object Help : Command(name = "Help", description = "RTFM")
    object Modules : Command(name = "Modules", description = "Features")
    object Commands : Command(name = "Commands", description = "Available commands")
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
    //endregion

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
    object Refresh : Command(name = "Refresh", description = "Refreshes all features")
    //endregion

    object Fd : Command(
        name = "Fd",
        description = "Move frame data",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
                autoCompleteType = Argument.AutoCompleteType.Character,
            ),
            Argument(
                name = "move",
                description = "Move",
                autoCompleteType = Argument.AutoCompleteType.Move,
            ),
        ),
    )

    object Char : Command(
        name = "Char",
        description = "Character data",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
                autoCompleteType = Argument.AutoCompleteType.Character,
            ),
        ),
    )

    object Alias : Command(
        name = "Alias",
        description = "Show character aliases",
        argumentList = listOf(
            Argument(
                name = "game",
                description = "Game",
                autoCompleteType = Argument.AutoCompleteType.Other,
            ),
        )
    )

    //TODO: implement when there's time
//    object Invincible : Command(
//        name = "Invincible",
//        description = "Shows invincible moves",
//        argumentList = listOf(
//            Argument(
//                name = "character",
//                description = "Character name",
//                autoCompleteType = Argument.AutoCompleteType.Character,
//            ),
//        ),
//    )

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
    object Pc : Command(
        name = "Pc",
        description = "Tekken POWER CRUSH moves",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
                autoCompleteType = Argument.AutoCompleteType.Character,
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
                autoCompleteType = Argument.AutoCompleteType.Character,
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
                autoCompleteType = Argument.AutoCompleteType.Character,
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
                autoCompleteType = Argument.AutoCompleteType.Character,
            ),
            Argument(
                name = "stance",
                description = "Stance",
                isRequired = false,
                autoCompleteType = Argument.AutoCompleteType.Other,
            ),
        ),
    )
    object ThrowTK : Command(
        name = "ThrowTK",
        description = "Tekken THROW moves",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
                autoCompleteType = Argument.AutoCompleteType.Character,
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
                autoCompleteType = Argument.AutoCompleteType.Character,
            ),
            Argument(
                name = "move",
                description = "Starting with",
            ),
        ),
    )
    //endregion

    //region DragDown
    object SpecialROA : Command(
        name = "SpecialROA",
        description = "ROA2 special moves",
        argumentList = listOf(
            Argument(
                name = "character",
                description = "Character name",
                autoCompleteType = Argument.AutoCompleteType.Character,
            )
        )
    )
    //endregion

    //region EWGF
    object Ewgf : Command(
        name = "EWGF",
        description = "Perform an EWGF operation",
        argumentList = listOf(
            Argument(
                name = "operation",
                description = "Help | Register | Update | Unregister",
                isRequired = false,
            ),
            Argument(
                name = "polarisid",
                description = "Tekken ID",
                isRequired = false,
            )
        )
    )
    //endregion


    data class Argument(
        val name: String,
        val description: String,
        val isRequired: Boolean = true,
        val autoCompleteType: AutoCompleteType = AutoCompleteType.None,
    ) {
        enum class AutoCompleteType {
            Character,
            Move,
            Other,
            None,
        }
    }

    companion object {
        val entries: List<Command> by lazy {
            Command::class.sealedSubclasses
                .mapNotNull { it.objectInstance }
        }

        fun fromId(id: String): Command? {
            return entries.find { it.name.equals(id, ignoreCase = true) }
        }
    }
}
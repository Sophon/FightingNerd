package io.github.sophon.fightingnerd.feat.more.ui.about

import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.ic_discord
import fightingnerd.composeapp.generated.resources.ic_github
import fightingnerd.composeapp.generated.resources.more_about_invite_discord_label
import fightingnerd.composeapp.generated.resources.more_about_invite_github_label
import io.github.sophon.fightingnerd.feat.more.URL_DISCORD_INVITE
import io.github.sophon.fightingnerd.feat.more.URL_GITHUB_REPO
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

internal data class AboutState(
    val links: ImmutableList<Link> = persistentListOf(
        Link.GitHub,
        Link.DiscordBot,
    ),
    val uiWikiList: ImmutableList<UiWiki> = persistentListOf(),
) {
    enum class Link(
        val icon: DrawableResource,
        val label: StringResource,
        val url: String,
    ) {
        GitHub(
            icon = Res.drawable.ic_github,
            label = Res.string.more_about_invite_github_label,
            url = URL_GITHUB_REPO,
        ),
        DiscordBot(
            icon = Res.drawable.ic_discord,
            label = Res.string.more_about_invite_discord_label,
            url = URL_DISCORD_INVITE,
        ),
    }

    data class UiWiki(
        val iconUrl: String,
        val url: String,
    )
}

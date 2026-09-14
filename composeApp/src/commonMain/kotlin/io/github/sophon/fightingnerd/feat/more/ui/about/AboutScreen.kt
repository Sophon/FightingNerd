package io.github.sophon.fightingnerd.feat.more.ui.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.ic_fighting_nerd
import fightingnerd.composeapp.generated.resources.more_about_about_body
import fightingnerd.composeapp.generated.resources.more_about_about_title
import fightingnerd.composeapp.generated.resources.more_about_invite_links_title
import fightingnerd.composeapp.generated.resources.more_about_name_body
import fightingnerd.composeapp.generated.resources.more_about_name_title
import fightingnerd.composeapp.generated.resources.more_about_next_body
import fightingnerd.composeapp.generated.resources.more_about_next_title
import fightingnerd.composeapp.generated.resources.more_about_wikis_body
import fightingnerd.composeapp.generated.resources.more_about_wikis_title
import io.github.sophon.fightingnerd.core.ui.components.TopBarButton
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions
import io.github.sophon.fightingnerd.theme.nerdTypography
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun AboutScreen(
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val vm = koinViewModel<AboutVM>()
    val state by vm.state.collectAsStateWithLifecycle()
    val onExitWithReview: () -> Unit = {
        vm.onScreenExit()
        onExit()
    }

    Content(
        state = state,
        onExit = onExitWithReview,
        onLinkClick = vm::openUrl,
        modifier = modifier,
    )
}

@Composable
private fun Content(
    state: AboutState,
    onExit: () -> Unit,
    onLinkClick: (url: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(nerdColorPalette.background)
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = nerdDimensions.screenPaddingHorizontal,
                vertical = nerdDimensions.screenPaddingVertical,
            ),
    ) {
        Header(onExit)
        Spacer(Modifier.height(nerdDimensions.sectionGap))

        Section(
            title = stringResource(Res.string.more_about_about_title),
            body = stringResource(Res.string.more_about_about_body)
        )
        Spacer(Modifier.height(nerdDimensions.sectionGap))

        Section(
            title = stringResource(Res.string.more_about_name_title),
            body = stringResource(Res.string.more_about_name_body),
        )
        Spacer(Modifier.height(nerdDimensions.sectionGap))

        Section(
            title = stringResource(Res.string.more_about_next_title),
            body = stringResource(Res.string.more_about_next_body),
        )
        Spacer(Modifier.height(nerdDimensions.sectionGap))

        WikisSection(
            wikis = state.uiWikiList,
            onLinkClick = onLinkClick,
        )
        Spacer(Modifier.height(nerdDimensions.sectionGap))

        LinksSection(links = state.links, onLinkClick = onLinkClick)
        Spacer(Modifier.height(nerdDimensions.sectionGap))
    }
}

@Composable
private fun Header(
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
    ) {
        TopBarButton(
            onClick = onExit,
            modifier = Modifier.align(Alignment.TopStart),
        )
        Image(
            painter = painterResource(Res.drawable.ic_fighting_nerd),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .size(nerdDimensions.iconHeadline),
        )
    }
}

@Composable
private fun Section(
    title: String,
    body: String,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = title.uppercase(),
            style = nerdTypography.headlineSmall,
            color = nerdColorPalette.textPrimary,
        )
        Spacer(Modifier.height(nerdDimensions.componentGapTight))

        Text(
            text = body,
            style = nerdTypography.bodyLarge,
            color = nerdColorPalette.textPrimary,
        )
    }
}

@Composable
private fun WikisSection(
    wikis: ImmutableList<AboutState.UiWiki>,
    onLinkClick: (url: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = stringResource(Res.string.more_about_wikis_title).uppercase(),
            style = nerdTypography.headlineSmall,
            color = nerdColorPalette.textPrimary,
        )
        Spacer(Modifier.height(nerdDimensions.componentGapTight))

        Text(
            text = stringResource(Res.string.more_about_wikis_body),
            style = nerdTypography.bodyLarge,
            color = nerdColorPalette.textPrimary,
        )
        Spacer(Modifier.height(nerdDimensions.componentGapTight))

        Row(
            horizontalArrangement = Arrangement.spacedBy(
                space = nerdDimensions.inlineGap,
                alignment = Alignment.CenterHorizontally,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
        ) {
            val shape = RoundedCornerShape(nerdDimensions.cornerDefault)
            wikis.forEach { link ->
                AsyncImage(
                    model = link.iconUrl,
                    contentDescription = null,
                    modifier = modifier
                        .size(nerdDimensions.iconHeadline)
                        .clip(shape)
                        .border(nerdDimensions.strokeThin, nerdColorPalette.dividerSubtle, shape)
                        .clickable(onClick = { onLinkClick(link.url) }),
                )
            }
        }
    }
}

@Composable
private fun LinksSection(
    links: ImmutableList<AboutState.Link>,
    onLinkClick: (url: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = stringResource(Res.string.more_about_invite_links_title).uppercase(),
            style = nerdTypography.headlineSmall,
            color = nerdColorPalette.textPrimary,
        )
        Spacer(Modifier.height(nerdDimensions.componentGapTight))

        Row(
            horizontalArrangement = Arrangement.spacedBy(
                space = nerdDimensions.componentGap,
                alignment = Alignment.CenterHorizontally,
            ),
            modifier = Modifier.fillMaxWidth(),
        ) {
            links.forEach { link ->
                LinkBox(
                    icon = link.icon,
                    label = stringResource(link.label),
                    onClick = { onLinkClick(link.url) },
                )
            }
        }
    }
}

@Composable
private fun LinkBox(
    icon: DrawableResource,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(CircleShape)
                .background(nerdColorPalette.surfaceHigh)
                .clickable(onClick = onClick)
                .padding(nerdDimensions.componentPadding),
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = nerdColorPalette.textPrimary,
                modifier = Modifier.size(nerdDimensions.iconLarge),
            )
        }
        Spacer(Modifier.height(nerdDimensions.componentGapTight))

        Text(
            text = label,
            style = nerdTypography.labelLarge,
            color = nerdColorPalette.textSecondary,
        )
    }
}


//region PREVIEW
@Composable
@Preview()
private fun Preview() {
    FightingNerdTheme {
        Content(
            state = AboutState(),
            onExit = {},
            onLinkClick = {},
        )
    }
}
//endregion

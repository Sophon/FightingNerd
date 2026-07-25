package io.github.sophon.fightingnerd.feat.quiz.ui.quiz.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.kdroidfilter.composemediaplayer.InitialPlayerState
import io.github.kdroidfilter.composemediaplayer.VideoPlayerSurface
import io.github.kdroidfilter.composemediaplayer.rememberVideoPlayerState
import io.github.sophon.fightingnerd.core.ui.components.CircularLoader
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions

@Composable
internal fun VideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier,
) {
    val playerState = rememberVideoPlayerState()
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(videoUrl) {
        playerState.loop = true
        playerState.volume = 0f
        playerState.openUri(videoUrl, InitialPlayerState.PLAY)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(nerdDimensions.cornerDefault))
            .background(nerdColorPalette.surface)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) {
                if (playerState.isPlaying) {
                    playerState.pause()
                } else {
                    playerState.play()
                }
            },
    ) {
        VideoPlayerSurface(
            playerState = playerState,
            modifier = Modifier.fillMaxSize(),
        )
        if (playerState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(nerdColorPalette.surface),
            ) {
                CircularLoader(modifier = Modifier.align(Alignment.Center))
            }
        } else if (playerState.isPlaying.not()) {
            Icon(
                imageVector = Icons.Outlined.PlayArrow,
                contentDescription = null,
                modifier = Modifier
                    .size(128.dp)
                    .align(Alignment.Center),
            )
        }
    }
    Spacer(Modifier.height(nerdDimensions.componentPadding))
}

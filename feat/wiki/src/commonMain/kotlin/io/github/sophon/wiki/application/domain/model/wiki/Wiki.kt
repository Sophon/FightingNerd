package io.github.sophon.wiki.application.domain.model.wiki

sealed class Wiki(
    val id: String,
    val displayName: String,
    val url: String,
    val iconUrl: String,
    val feedbackDiscordChannelId: String? = null,
) {
    data object DragDown : Wiki(
        id = "dragdown",
        displayName = "DragDown Wiki",
        url = "https://dragdown.wiki/wiki",
        iconUrl = "https://static.wikitide.net/dragdownwiki/2/2b/Dd_color_symbol-square_bg.png",
    )

    data object DreamCancel : Wiki(
        id = "dreamcancel",
        displayName = "DreamCancel Wiki",
        url = "https://dreamcancel.com/wiki",
        iconUrl = "https://dreamcancel.com/w/images/dclogooutlined2.png",
    )

    data object DustLoop : Wiki(
        id = "dustloop",
        displayName = "DustLoop Wiki",
        url = "https://www.dustloop.com/wiki/",
        iconUrl = "https://www.dustloop.com/wiki/images/archive/3/30/20260601135625%21Dustloop_Wiki.png",
        feedbackDiscordChannelId = "578257299529924621",
    )

    data object Mizuumi : Wiki(
        id = "mizuumi",
        displayName = "Mizuumi Wiki",
        url = "https://mizuumi.wiki",
        iconUrl = "https://mizuumi.wiki/mizulogo.png?1fe5d",
    )

    data object SuperCombo : Wiki(
        id = "supercombo",
        displayName = "SuperCombo Wiki",
        url = "https://wiki.supercombo.gg/",
        iconUrl = "https://wiki.supercombo.gg/srk_wordmark.png",
    )

    data object Wavu : Wiki(
        id = "wavu",
        displayName = "Wavu Wiki",
        url = "https://wavu.wiki/",
        iconUrl = "https://wavu.wiki/android-chrome-512x512.png",
        feedbackDiscordChannelId = "1193118389825175582",
    )

    data object Xko : Wiki(
        id = "xko",
        displayName = "2XKO Wiki",
        url = "https://wiki.play2xko.com/en-us",
        iconUrl = "https://wiki.play2xko.com/en-us/images/Wiki.png",
    )
}

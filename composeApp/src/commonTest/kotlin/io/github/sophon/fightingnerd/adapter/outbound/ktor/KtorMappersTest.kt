package io.github.sophon.fightingnerd.adapter.outbound.ktor

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.fightingnerd.app.model.Release
import kotlinx.collections.immutable.persistentListOf
import kotlin.test.Test

internal class KtorMappersTest {
    @Test
    fun `app tag becomes an app release without the tag prefix`() {
        // given
        val releaseList = listOf(releaseDto(tagName = "app-v5.2.0", body = "- Tekken 8 heat properties"))
        val expected = listOf(
            Release(
                version = "5.2.0",
                isPreRelease = false,
                type = Release.Type.APP,
                changeList = persistentListOf("Tekken 8 heat properties"),
            ),
        )

        // when
        val result = releaseList.toDomain()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `bot tag becomes a bot release and keeps the pre-release flag`() {
        // given
        val releaseList = listOf(
            releaseDto(tagName = "bot-v4.3.1", body = "- Mizuumi Wiki support", isPreRelease = true),
        )
        val expected = listOf(
            Release(
                version = "4.3.1",
                isPreRelease = true,
                type = Release.Type.BOT,
                changeList = persistentListOf("Mizuumi Wiki support"),
            ),
        )

        // when
        val result = releaseList.toDomain()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `releases with an unknown tag are dropped`() {
        // given
        val releaseList = listOf(
            releaseDto(tagName = "v3.0.0", body = "- legacy release"),
            releaseDto(tagName = "app-v5.2.0", body = "- Tekken 8 heat properties"),
        )
        val expected = listOf("5.2.0")

        // when
        val result = releaseList.toDomain().map { release -> release.version }

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `body lines lose their bullets, backticks and surrounding blanks`() {
        // given
        val body = "- `Wavu Wiki` refresh no longer stalls\r\n\r\n* Quiz shows the counter-hit frames\r\n   - Floating bar hides on detail screens  \r\n"
        val releaseList = listOf(releaseDto(tagName = "app-v5.2.0", body = body))
        val expected = listOf(
            "Wavu Wiki refresh no longer stalls",
            "Quiz shows the counter-hit frames",
            "Floating bar hides on detail screens",
        )

        // when
        val result = releaseList.toDomain().flatMap { release -> release.changeList }

        // then
        assertThat(result).isEqualTo(expected)
    }


    private fun releaseDto(
        tagName: String,
        body: String,
        isPreRelease: Boolean = false,
    ): ReleaseDto {
        val author = AuthorDto(
            login = "Sophon",
            id = 48_213_907,
            nodeId = "MDQ6VXNlcjQ4MjEzOTA3",
            avatarUrl = "https://avatars.githubusercontent.com/u/48213907?v=4",
            gravatarId = "",
            url = "https://api.github.com/users/Sophon",
            htmlUrl = "https://github.com/Sophon",
            followersUrl = "https://api.github.com/users/Sophon/followers",
            followingUrl = "https://api.github.com/users/Sophon/following{/other_user}",
            gistsUrl = "https://api.github.com/users/Sophon/gists{/gist_id}",
            starredUrl = "https://api.github.com/users/Sophon/starred{/owner}{/repo}",
            subscriptionsUrl = "https://api.github.com/users/Sophon/subscriptions",
            organizationsUrl = "https://api.github.com/users/Sophon/orgs",
            reposUrl = "https://api.github.com/users/Sophon/repos",
            eventsUrl = "https://api.github.com/users/Sophon/events{/privacy}",
            receivedEventsUrl = "https://api.github.com/users/Sophon/received_events",
            type = "User",
            userViewType = "public",
            siteAdmin = false,
        )
        val release = ReleaseDto(
            url = "https://api.github.com/repos/Sophon/FightingNerd/releases/251873402",
            assetsUrl = "https://api.github.com/repos/Sophon/FightingNerd/releases/251873402/assets",
            uploadUrl = "https://uploads.github.com/repos/Sophon/FightingNerd/releases/251873402/assets{?name,label}",
            htmlUrl = "https://github.com/Sophon/FightingNerd/releases/tag/$tagName",
            id = 251_873_402,
            author = author,
            nodeId = "RE_kwDOLx3Fhc4PA1x6",
            tagName = tagName,
            targetCommitish = "release_app",
            name = tagName,
            draft = false,
            immutable = false,
            prerelease = isPreRelease,
            createdAt = "2026-09-28T19:42:11Z",
            updatedAt = "2026-09-28T19:44:03Z",
            publishedAt = "2026-09-28T19:44:03Z",
            assets = emptyList(),
            tarballUrl = "https://api.github.com/repos/Sophon/FightingNerd/tarball/$tagName",
            zipballUrl = "https://api.github.com/repos/Sophon/FightingNerd/zipball/$tagName",
            body = body,
        )
        return release
    }
}

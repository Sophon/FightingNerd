package io.github.sophon.botdiscord.adapter.outbound.config

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.network.HttpClientFactory
import io.github.sophon.discord.adapter.outbound.config.ConfigAdapter
import io.github.sophon.discord.app.model.discord.DiscordConfig
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.io.path.createTempFile
import kotlin.test.AfterTest
import kotlin.test.Test

class ConfigAdapterTest {
    private val bundledConfigFile: File = createTempFile(suffix = ".json").toFile()
        .apply { writeText(json.encodeToString(bundledConfig)) }

    @AfterTest
    fun tearDown() {
        bundledConfigFile.delete()
    }

    @Test
    fun `refresh returns the remote config`() = runTest {
        // given
        val expected = Result.Success(remoteConfig)
        val adapter = configAdapter(remote = FakeRemote(isAvailable = true))

        // when
        val result = adapter.refresh()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `refreshed config is cached`() = runTest {
        // given
        val expected = Result.Success(remoteConfig)
        val adapter = configAdapter(remote = FakeRemote(isAvailable = true))
        adapter.refresh()

        // when
        val result = adapter.load()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `failed refresh keeps the cached config`() = runTest {
        // given
        val expected = Result.Success(remoteConfig)
        val remote = FakeRemote(isAvailable = true)
        val adapter = configAdapter(remote = remote)
        adapter.refresh()
        remote.isAvailable = false

        // when
        val result = adapter.refresh()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `failed refresh falls back to the bundled config`() = runTest {
        // given
        val expected = Result.Success(bundledConfig)
        val adapter = configAdapter(remote = FakeRemote(isAvailable = false))

        // when
        val result = adapter.refresh()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `failed refresh without a bundled config is an error`() = runTest {
        // given
        val adapter = configAdapter(
            remote = FakeRemote(isAvailable = false),
            bundledConfigPath = "res/missingConfig.json",
        )

        // when
        val result = adapter.refresh()

        // then
        assertThat(result).isInstanceOf<Result.Error<*>>()
    }

    @Test
    fun `load reads the bundled config before any refresh`() = runTest {
        // given
        val expected = Result.Success(bundledConfig)
        val adapter = configAdapter(remote = FakeRemote(isAvailable = true))

        // when
        val result = adapter.load()

        // then
        assertThat(result).isEqualTo(expected)
    }


    /**
     * Serves [remoteConfig] as `text/plain` like raw.githubusercontent.com; a 503 while unavailable.
     */
    private class FakeRemote(var isAvailable: Boolean) {
        val engine = MockEngine {
            if (isAvailable) {
                respond(
                    content = json.encodeToString(remoteConfig),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "text/plain; charset=utf-8"),
                )
            } else {
                respond(content = "", status = HttpStatusCode.ServiceUnavailable)
            }
        }
    }

    private fun configAdapter(
        remote: FakeRemote,
        bundledConfigPath: String = bundledConfigFile.path,
    ): ConfigAdapter {
        val adapter = ConfigAdapter(
            httpClient = HttpClientFactory.create(remote.engine, json),
            json = json,
            remoteConfigUrl = REMOTE_CONFIG_URL,
            bundledConfigPath = bundledConfigPath,
        )
        return adapter
    }
}


private val json = Json { ignoreUnknownKeys = true }

private val remoteConfig = DiscordConfig(
    featureList = listOf(
        DiscordConfig.Feature(name = "Wavu Wiki", isEnabled = true, supportedGames = listOf("Tekken_8", "Tag_2")),
        DiscordConfig.Feature(name = "DragDown Wiki", isEnabled = true, supportedGames = listOf("ROA2")),
    ),
    adminConfig = DiscordConfig.AdminConfig(
        administratorIdList = listOf("126041495038853120"),
        feedbackChannelIdList = listOf("1118576752042713148"),
        adminServerId = "717398042562658344",
    ),
    statsConfig = DiscordConfig.StatsConfig(isEnabled = true, statsChannelIdList = listOf("1480723668039503994")),
)

private val bundledConfig = remoteConfig.copy(
    featureList = listOf(
        DiscordConfig.Feature(name = "Wavu Wiki", isEnabled = true, supportedGames = listOf("Tekken_8")),
    ),
)

private const val REMOTE_CONFIG_URL = "https://raw.githubusercontent.com/Sophon/FightingNerd/dev/res/discordConfig.json"

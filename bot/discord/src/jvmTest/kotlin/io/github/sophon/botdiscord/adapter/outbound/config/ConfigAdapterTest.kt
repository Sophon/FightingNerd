package io.github.sophon.botdiscord.adapter.outbound.config

import assertk.assertThat
import assertk.assertions.isEqualTo
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
    private val configFile: File = createTempFile(suffix = ".json")
        .toFile()
        .apply { writeText(json.encodeToString(fileConfig)) }

    @AfterTest
    fun tearDown() {
        configFile.delete()
    }

    @Test
    fun `download returns the remote config`() = runTest {
        // given
        val expected = Result.Success(remoteConfig)
        val adapter = configAdapter(remote = FakeRemote(isAvailable = true))

        // when
        val result = adapter.download()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `failed download falls back to the cached config`() = runTest {
        // given
        val expected = Result.Success(remoteConfig)
        val remote = FakeRemote(isAvailable = true)
        val adapter = configAdapter(remote = remote)
        adapter.download()
        remote.isAvailable = false

        // when
        val result = adapter.download()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `failed download without a cache falls back to the config file`() = runTest {
        // given
        val expected = Result.Success(fileConfig)
        val adapter = configAdapter(remote = FakeRemote(isAvailable = false))

        // when
        val result = adapter.download()

        // then
        assertThat(result).isEqualTo(expected)
    }


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

    private fun configAdapter(remote: FakeRemote): ConfigAdapter {
        val adapter = ConfigAdapter(
            httpClient = HttpClientFactory.create(remote.engine, json),
            json = json,
            configPath = configFile.path,
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

private val fileConfig = remoteConfig.copy(
    featureList = listOf(
        DiscordConfig.Feature(name = "Wavu Wiki", isEnabled = true, supportedGames = listOf("Tekken_8")),
    ),
)

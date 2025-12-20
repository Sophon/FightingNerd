package io.github.sophon.botdiscord.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.domain.Result
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.discord.BotError
import io.github.sophon.discord.featureRegistry.BotOutput
import io.github.sophon.discord.featureRegistry.Command
import io.github.sophon.discord.featureRegistry.DiscordRegisteredFeature
import io.github.sophon.discord.featureRegistry.SupportedCommand
import io.github.sophon.discord.usecase.RouteCommandToFeatureUseCase
import io.github.sophon.domain.Source
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class RouteCommandToFeatureUseCaseTest {
    //region Test Doubles
    private class FakeWavuFeature : DiscordRegisteredFeature {
        override val featureInfo = FeatureInfo(
            name = "Wavu",
            url = "",
            version = "1.0.0"
        )
        override val defaultCommand = SupportedCommand(
            Command.FD,
            description = "",
            arguments = emptyList(),
        )
        override val otherCommands = listOf(
            SupportedCommand(
                Command.PC,
                description = "",
                arguments = emptyList()
            ),
            SupportedCommand(
                Command.HEAT,
                description = "",
                arguments = emptyList()
            ),
            SupportedCommand(
                Command.HOMING,
                description = "",
                arguments = emptyList()
            )
        )

        override suspend fun start() {}

        override suspend fun execute(
            command: Command,
            query: String,
            source: Source,
        ): Result<BotOutput, BotError> {
            val tekkenChars = setOf("lily", "ak", "jin", "kazuya")

            return when (command) {
                Command.FD -> {
                    val parts = query.split(" ")
                    val charName = parts.firstOrNull()?.lowercase()

                    if (charName !in tekkenChars) {
                        Result.Error(BotError.UnknownCharacter(charName.orEmpty()))
                    } else if (parts.size < 2) {
                        Result.Error(BotError.UnknownMove(charName.orEmpty(), query))
                    } else {
                        Result.Success(BotOutput(embedBuilder = { title = "Wavu FD: $query" }))
                    }
                }
                Command.PC, Command.HEAT, Command.HOMING -> {
                    val charName = query.lowercase()
                    if (charName in tekkenChars) {
                        Result.Success(BotOutput(embedBuilder = { title = "Wavu ${command.name}: $query" }))
                    } else {
                        Result.Error(BotError.UnknownCharacter(charName))
                    }
                }
                else -> Result.Error(BotError.BotLogicError())
            }
        }
    }

    private class FakeInfilFeature : DiscordRegisteredFeature {
        override val featureInfo = FeatureInfo(
            name = "Infil",
            url = "",
            version = "1.0.0"
        )
        override val defaultCommand = SupportedCommand(
            Command.GL,
            description = "",
            arguments = emptyList()
        )
        override val otherCommands = emptyList<SupportedCommand>()

        override suspend fun start() {}

        override suspend fun execute(
            command: Command,
            query: String,
            source: Source,
        ): Result<BotOutput, BotError> {
            val glossaryTerms = setOf(
                "frame",
                "hitbox",
                "cancel",
                "okizeme"
            )

            return when (command) {
                Command.GL -> {
                    if (query.lowercase() in glossaryTerms) {
                        Result.Success(BotOutput(embedBuilder = { title = "Infil GL: $query" }))
                    } else {
                        Result.Error(BotError.GlossaryTermNotFound(query))
                    }
                }
                else -> Result.Error(BotError.BotLogicError())
            }
        }
    }

    private class FakeSuperComboFeature: DiscordRegisteredFeature {
        override val featureInfo = FeatureInfo(
            name = "SuperCombo",
            url = "",
            version = "1.0.0"
        )
        override val defaultCommand = SupportedCommand(
            Command.FD,
            description = "",
            arguments = emptyList(),
        )
        override val otherCommands = listOf(
            SupportedCommand(
                Command.CHARSF,
                description = "",
                arguments = emptyList(),
            ),
        )

        override suspend fun start() {}

        override suspend fun execute(
            command: Command,
            query: String,
            source: Source,
        ): Result<BotOutput, BotError> {
            val sfChars = setOf(
                "lily",
                "ken",
                "ryu",
                "chun-li"
            )

            return when (command) {
                Command.FD -> {
                    val parts = query.split(" ")
                    val charName = parts.firstOrNull()?.lowercase()

                    if (charName !in sfChars) {
                        Result.Error(BotError.UnknownCharacter(charName.orEmpty()))
                    } else if (parts.size < 2) {
                        Result.Error(BotError.UnknownMove(charName.orEmpty(), query))
                    } else {
                        Result.Success(BotOutput(embedBuilder = { title = "SuperCombo FD: $query" }))
                    }
                }
                Command.CHARSF -> {
                    if (query.lowercase() in sfChars) {
                        Result.Success(BotOutput(embedBuilder = { title = "SuperCombo CHARSF6: $query" }))
                    } else {
                        Result.Error(BotError.UnknownCharacter(query))
                    }
                }
                else -> Result.Error(BotError.BotLogicError())
            }
        }
    }

    private class FakeCoreFeature : DiscordRegisteredFeature {
        override val featureInfo = FeatureInfo(
            name = "NoDefault",
            url = "",
            version = "1.0.0"
        )
        override val defaultCommand = null
        override val otherCommands = listOf(
            SupportedCommand(Command.HEAT, description = "", arguments = emptyList())
        )
        override suspend fun start() {}
        override suspend fun execute(
            command: Command,
            query: String,
            source: Source,
        ): Result<BotOutput, BotError> {
            return if (command == Command.HEAT && query.isNotBlank()) {
                Result.Success(BotOutput(embedBuilder = { title = "NoDefault HEAT: $query" }))
            } else {
                Result.Error(BotError.InvalidQuery(query))
            }
        }
    }
    //endregion

    //region Setup
    private val wavuFeature = FakeWavuFeature()
    private val infilFeature = FakeInfilFeature()
    private val superComboFeature = FakeSuperComboFeature()
    private val coreFeature = FakeCoreFeature()
    private val featureList = listOf(wavuFeature, coreFeature, infilFeature, superComboFeature)
    private val useCase = RouteCommandToFeatureUseCase(featureList)
    //endregion

    //region Invalid Input
    @Test
    fun `invoke with blank message returns InvalidQuery(query) error`() = runTest {
        // given
        val message = "@bot   "
        // when
        val result = useCase.invoke(
            Source("", "", ""),
            message
        )
        // then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertTrue((result as Result.Error).error is BotError.InvalidQuery)
    }

    @Test
    fun `invoke with only tag returns InvalidQuery(query) error`() = runTest {
        // given
        val message = "@bot"
        // when
        val result = useCase.invoke(Source("", "", ""),message)
        // then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertTrue((result as Result.Error).error is BotError.InvalidQuery)
    }
    //endregion

    //region Explicit Commands - Success
    @Test
    fun `invoke with FD explicit command and valid Tekken character returns error`() = runTest {
        // given
        val message = "@bot fd ak f21"
        // when
        val result = useCase.invoke(Source("", "", ""),message)
        // then
        assertThat(result).isInstanceOf(Result.Success::class)
    }

    @Test
    fun `invoke with PC explicit command and valid Tekken character returns success`() = runTest {
        // given
        val message = "@bot pc jin"
        // when
        val result = useCase.invoke(Source("", "", ""),message)
        // then
        assertThat(result).isInstanceOf(Result.Success::class)
    }

    @Test
    fun `invoke with GL explicit command and valid glossary term returns success`() = runTest {
        // given
        val message = "@bot gl frame"
        // when
        val result = useCase.invoke(Source("", "", ""),message)
        // then
        assertThat(result).isInstanceOf(Result.Success::class)
    }

    @Test
    fun `invoke with CHARSF6 explicit command and valid SF character returns success`() = runTest {
        // given
        val message = "@bot charsf ken"
        // when
        val result = useCase.invoke(Source("", "", ""),message)
        // then
        assertThat(result).isInstanceOf(Result.Success::class)
    }

    @Test
    fun `invoke with explicit command on feature without default command returns success`() = runTest {
        // given - Feature has no default command but has HEAT as explicit command
        val message = "@bot heat jin"
        // when
        val result = useCase.invoke(Source("", "", ""),message)
        // then
        assertThat(result).isInstanceOf(Result.Success::class)
    }
    //endregion

    //region Explicit Commands - Failure
    @Test
    fun `invoke with FD explicit command and unknown character returns error`() = runTest {
        // given
        val message = "@bot fd unknown f21"
        // when
        val result = useCase.invoke(Source("", "", ""),message)
        // then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertTrue((result as Result.Error).error is BotError.UnknownCharacter)
    }

    @Test
    fun `invoke with GL explicit command and unknown term returns error`() = runTest {
        // given
        val message = "@bot gl unknownterm"
        // when
        val result = useCase.invoke(Source("", "", ""),message)
        // then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertTrue((result as Result.Error).error is BotError.GlossaryTermNotFound)
    }
    //endregion

    //region Default Commands - Character Priority
    @Test
    fun `invoke without explicit command for Lily uses Wavu first and returns success`() = runTest {
        // given - Lily exists in both Tekken and SF, but Wavu is first
        val message = "@bot lily 5lk"
        // when
        val result = useCase.invoke(Source("", "", ""),message)
        // then
        assertThat(result).isInstanceOf(Result.Success::class)
        val embedBuilder = (result as Result.Success).data.embedBuilder
        assertThat(embedBuilder).isNotNull()
        val title = EmbedBuilder().apply(embedBuilder!!).title
        assertThat(title).isEqualTo("Wavu FD: lily 5lk")
    }

    @Test
    fun `invoke without explicit command for AK uses Wavu and returns success`() = runTest {
        // given - AK only exists in Tekken
        val message = "@bot ak f21"
        // when
        val result = useCase.invoke(Source("", "", ""),message)
        // then
        assertThat(result).isInstanceOf(Result.Success::class)
        val embedBuilder = (result as Result.Success).data.embedBuilder
        assertThat(embedBuilder).isNotNull()
        val title = EmbedBuilder().apply(embedBuilder!!).title
        assertThat(title).isEqualTo("Wavu FD: ak f21")
    }

    @Test
    fun `invoke without explicit command for Ken tries Wavu then SuperCombo and returns success`() = runTest {
        // given - Ken only exists in SF, so Wavu fails, SuperCombo succeeds
        val message = "@bot ken dp"
        // when
        val result = useCase.invoke(Source("", "", ""),message)
        // then
        assertThat(result).isInstanceOf(Result.Success::class)
        val embedBuilder = (result as Result.Success).data.embedBuilder
        assertThat(embedBuilder).isNotNull()
        val title = EmbedBuilder().apply(embedBuilder!!).title
        assertThat(title).isEqualTo("SuperCombo FD: ken dp")
    }
    //endregion

    //region Default Commands - Failure
    @Test
    fun `invoke without explicit command and completely unknown input returns error`() = runTest {
        // given - No feature recognizes this
        val message = "@bot completelyunknown whatever"
        // when
        val result = useCase.invoke(Source("", "", ""),message)
        // then
        assertThat(result).isInstanceOf(Result.Error::class)
    }

    @Test
    fun `invoke unknown command returns error`() = runTest {
        // given - Move notation without character name
        val message = "@bot 5lp"
        // when
        val result = useCase.invoke(Source("", "", ""),message)
        // then
        assertThat(result).isInstanceOf(Result.Error::class)
    }
    //endregion

    //region Slash Command Flow
    @Test
    fun `slash command invoke with valid FD command and Tekken character returns success`() = runTest {
        // given
        val commandString = "fd"
        val query = "jin f4"
        // when
        val result = useCase.invoke(commandString, Source("", "", ""), query)
        // then
        assertThat(result).isInstanceOf(Result.Success::class)
    }

    @Test
    fun `slash command invoke with valid GL command returns success`() = runTest {
        // given
        val commandString = "gl"
        val query = "hitbox"
        // when
        val result = useCase.invoke(commandString, Source("", "", ""), query)
        // then
        assertThat(result).isInstanceOf(Result.Success::class)
    }

    @Test
    fun `slash command invoke with FD command and unknown character returns error`() = runTest {
        // given
        val commandString = "fd"
        val query = "unknown move"
        // when
        val result = useCase.invoke(commandString, Source("", "", ""), query)
        // then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertTrue((result as Result.Error).error is BotError.UnknownCharacter)
    }

    @Test
    fun `slash command invoke with explicit command from feature without default returns success`() = runTest {
        // given
        val commandString = "heat"
        val query = "jin"
        // when
        val result = useCase.invoke(commandString, Source("", "", ""), query)
        // then
        assertThat(result).isInstanceOf(Result.Success::class)
    }
    //endregion

    //region Edge Cases
    @Test
    fun `invoke with command but no query parameters returns error`() = runTest {
        // given
        val message = "@bot fd"
        // when
        val result = useCase.invoke(Source("", "", ""), message)
        // then
        assertThat(result).isInstanceOf(Result.Error::class)
    }

    @Test
    fun `invoke handles case insensitivity for commands`() = runTest {
        // given
        val message = "@bot FD aK F21"
        // when
        val result = useCase.invoke(Source("", "", ""), message)
        // then
        assertThat(result).isInstanceOf(Result.Success::class)
    }
    //endregion
}

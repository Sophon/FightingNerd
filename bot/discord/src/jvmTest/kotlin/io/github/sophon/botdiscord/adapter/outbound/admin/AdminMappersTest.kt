package io.github.sophon.botdiscord.adapter.outbound.admin

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.architecture.DataError
import io.github.sophon.discord.BOT_DATA_SOURCE
import io.github.sophon.discord.adapter.outbound.admin.toBanRequest
import io.github.sophon.discord.adapter.outbound.admin.toDomain
import io.github.sophon.discord.adapter.outbound.admin.toDomainError
import io.github.sophon.discord.adapter.outbound.admin.toUnbanRequest
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.ModerationRequest
import io.github.sophon.discord.app.model.UserRequest
import io.github.sophon.discord.app.model.response.BanResponse
import io.github.sophon.model.AdminError
import io.github.sophon.model.Ban
import io.github.sophon.model.BanRequest
import io.github.sophon.model.UnbanRequest
import kotlin.test.Test
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

class AdminMappersTest {
    //region toBanRequest
    @Test
    fun `moderation request becomes a ban request`() {
        // given
        val moderationRequest = ModerationRequest(
            authorId = ADMIN_ID,
            offender = offender,
            preventBotUsage = true,
            duration = 7.days,
        )
        val expected = BanRequest(
            issuerId = ADMIN_ID,
            offenderId = OFFENDER_ID,
            preventBotUsage = true,
            duration = 7.days,
        )

        // when
        val result = moderationRequest.toBanRequest()

        // then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region toUnbanRequest
    @Test
    fun `moderation request becomes an unban request`() {
        // given
        val moderationRequest = ModerationRequest(authorId = ADMIN_ID, offender = offender)
        val expected = UnbanRequest(issuerId = ADMIN_ID, offenderId = OFFENDER_ID)

        // when
        val result = moderationRequest.toUnbanRequest()

        // then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region toDomain
    @Test
    fun `ban becomes a ban response for the offender`() {
        // given
        val ban = Ban(
            offenderId = OFFENDER_ID,
            bannedAt = bannedAt,
            expiresAt = (bannedAt + 30.days),
            issuerId = ADMIN_ID,
            preventBotUsage = true,
        )
        val expected = BanResponse(
            offender = offender,
            bannedAt = bannedAt,
            expiresAt = (bannedAt + 30.days),
            issuerId = ADMIN_ID,
            preventBotUsage = true,
            dataSource = BOT_DATA_SOURCE,
        )

        // when
        val result = ban.toDomain(offender)

        // then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region toDomainError
    @Test
    fun `permission denied stays permission denied`() {
        // given
        val error = AdminError.PermissionDenied

        // when
        val result = error.toDomainError()

        // then
        assertThat(result).isInstanceOf(BotError.PermissionDenied::class)
    }

    @Test
    fun `database error becomes an admin error carrying the cause`() {
        // given
        val error = AdminError.Database(DataError.Local.UNKNOWN)
        val expected = "AdminError(Database(error=UNKNOWN))"

        // when
        val result = error.toDomainError()

        // then
        assertThat(result.toString()).isEqualTo(expected)
    }
    //endregion
}


private const val ADMIN_ID = "111111111111111111"
private const val OFFENDER_ID = "444444444444444444"
private val offender = UserRequest.Source(
    username = "offender",
    id = OFFENDER_ID,
    channelId = "555555555555555555",
)
private val bannedAt = Instant.parse("2026-10-06T18:00:00Z")

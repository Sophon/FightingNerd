package com.example.glossaryinfil.domain

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import kotlin.test.Test

class InfilUrlProviderTest {
    private val urlProvider = InfilUrlProvider()

    @Test
    fun `termUrl returns correct URL for simple term`() {
        // Given
        val item = GlossaryItem(
            term = "Fireball",
            definition = "A projectile attack"
        )

        // When
        val result = urlProvider.termUrl(item)

        // Then
        assertThat(result).isEqualTo("${TERM_URL}Fireball")
    }

    @Test
    fun `termUrl encodes term with spaces`() {
        // Given
        val item = GlossaryItem(
            term = "Dragon Punch",
            definition = "An anti-air move"
        )

        // When
        val result = urlProvider.termUrl(item)

        // Then
        assertThat(result).isEqualTo("${TERM_URL}Dragon%20Punch")
    }

    @Test
    fun `termUrl encodes term with special characters`() {
        // Given
        val item = GlossaryItem(
            term = "Guard Crush (GC)",
            definition = "Breaking opponent's guard"
        )

        // When
        val result = urlProvider.termUrl(item)

        // Then
        assertThat(result).isEqualTo("${TERM_URL}Guard%20Crush%20%28GC%29")
    }

    @Test
    fun `videoUrl returns correct URL when item has videos`() {
        // Given
        val item = GlossaryItem(
            term = "Fireball",
            definition = "A projectile attack",
            video = listOf("video1", "video2")
        )

        // When
        val result = urlProvider.videoUrl(item)

        // Then
        assertThat(result).isEqualTo("${VIDEO_URL}Fireball.mp4")
    }

    @Test
    fun `videoUrl returns null when item has no videos`() {
        // Given
        val item = GlossaryItem(
            term = "Combo",
            definition = "A sequence of attacks",
            video = emptyList()
        )

        // When
        val result = urlProvider.videoUrl(item)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `videoUrl encodes term with spaces when item has videos`() {
        // Given
        val item = GlossaryItem(
            term = "Dragon Punch",
            definition = "An anti-air move",
            video = listOf("video1")
        )

        // When
        val result = urlProvider.videoUrl(item)

        // Then
        assertThat(result).isEqualTo("${VIDEO_URL}Dragon%20Punch.mp4")
    }

    @Test
    fun `videoUrl encodes term with special characters when item has videos`() {
        // Given
        val item = GlossaryItem(
            term = "K.O.",
            definition = "Knockout",
            video = listOf("video1")
        )

        // When
        val result = urlProvider.videoUrl(item)

        // Then
        assertThat(result).isEqualTo("${VIDEO_URL}K.O..mp4")
    }
}

private const val TERM_URL = "https://glossary.infil.net/?t="
private const val VIDEO_URL = "https://glossary.infil.net/videos/"
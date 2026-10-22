package io.github.sophon.fightingnerd.adapter.inbound.quiz

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.fightingnerd.adapter.inbound.quiz.model.QuizQuestion
import io.github.sophon.fightingnerd.app.model.Character
import io.github.sophon.fightingnerd.app.model.Move
import io.github.sophon.fightingnerd.app.model.Question
import kotlinx.collections.immutable.persistentListOf
import kotlin.test.Test

internal class UiMappersTest {
    private val kazuya = Character(id = "kazuya", displayName = "Kazuya")

    private val ewgf = Move(
        input = "f,n,d,df+2",
        name = "Electric Wind God Fist",
        startup = "i11~12",
        onBlock = "+5",
        onHit = "+31a (+21) [Tornado](https://wavu.wiki/t/Mechanics#Tornado)",
        onCH = "+31a (+21) [Tornado](https://wavu.wiki/t/Mechanics#Tornado)",
        urls = Move.Urls(
            wikiUrl = "https://wavu.wiki/t/Kazuya_movelist#Kazuya-f,n,d,df+2",
            videoUrl = "https://wavu.wiki/w/images/Kazuya-f,n,d,df+2.mp4",
            hitboxImageList = listOf("https://wavu.wiki/w/images/Kazuya-f,n,d,df+2-hitbox.png"),
        ),
        groupId = "Kazuya",
    )
    private val hellSweep = Move(
        input = "f,n,d,df+4",
        startup = "i16",
        onBlock = "-23",
        onHit = "+3",
        urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Kazuya_movelist#Kazuya-f,n,d,df+4"),
        groupId = "Kazuya",
    )
    private val oneTwo = Move(
        input = "1,2",
        startup = "i10",
        onBlock = "-1",
        onHit = "+8",
        urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Kazuya_movelist#Kazuya-1,2"),
        groupId = "Kazuya",
    )
    private val demonGodFist = Move(
        input = "b+2",
        startup = "i15",
        onBlock = "-9",
        onHit = "+4",
        urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Kazuya_movelist#Kazuya-b+2"),
        groupId = "Kazuya",
    )

    @Test
    fun `question keeps the character name and the correct answer`() {
        // given
        val question = Question(character = kazuya, options = listOf(oneTwo, ewgf, hellSweep, demonGodFist), correctIndex = 1)
        val expected = Pair("Kazuya", "f,n,d,df+2")

        // when
        val result = question.toQuizQuestion()

        // then
        assertThat(result.characterName to result.correct.input).isEqualTo(expected)
    }

    @Test
    fun `options keep their order`() {
        // given
        val question = Question(character = kazuya, options = listOf(oneTwo, ewgf, hellSweep, demonGodFist), correctIndex = 1)
        val expected = listOf("1,2", "f,n,d,df+2", "f,n,d,df+4", "b+2")

        // when
        val result = question.toQuizQuestion().options.map { option -> option.input }

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `option frame data loses its markdown links and keeps its media`() {
        // given
        val question = Question(character = kazuya, options = listOf(ewgf, oneTwo, hellSweep, demonGodFist), correctIndex = 0)
        val expected = QuizQuestion.MoveOption(
            id = "f,n,d,df+2",
            input = "f,n,d,df+2",
            startup = "i11~12",
            onBlock = "+5",
            onHit = "+31a (+21) Tornado",
            onCH = "+31a (+21) Tornado",
            urls = QuizQuestion.MoveOption.Urls(
                videoUrl = "https://wavu.wiki/w/images/Kazuya-f,n,d,df+2.mp4",
                hitboxImageList = persistentListOf("https://wavu.wiki/w/images/Kazuya-f,n,d,df+2-hitbox.png"),
                moveImageList = persistentListOf(),
            ),
        )

        // when
        val result = question.toQuizQuestion().options.first()

        // then
        assertThat(result).isEqualTo(expected)
    }
}

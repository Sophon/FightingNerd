package io.github.sophon.glossaryinfil.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.glossaryinfil.integration.GlossaryItem
import kotlin.test.Test

class ItemMapperTest {
    //region toMarkdown
    @Test
    fun `toMarkdown handles html`() {
        //given
        val string = "A specific type of !<'projectile'> that travels horizontally " +
                "and is traditionally input using a !<'quarter circle'> command. " +
                "Ryu, Sagat, Jago and Sol all throw fireballs, and they are perhaps the most" +
                " iconic special moves in all of fighting games. !<'beam','Beams'> that " +
                "travel the whole screen instantly and !<'sonic boom','Sonic Booms'> that " +
                "require a !<'charge'> to execute aren't usually called fireballs. \"Fireball\" " +
                "can even be used as shorthand for \"quarter circle forward\", if you're trying " +
                "to quickly describe a special move input. \"The input for your " +
                "!<'command dash'> is fireball + kick\" would be a valid sentence, for example."
        val expected = "A specific type of **__projectile__** that travels horizontally and is " +
                "traditionally input using a **__quarter circle__** command. Ryu, Sagat, Jago " +
                "and Sol all throw fireballs, and they are perhaps the most iconic special moves " +
                "in all of fighting games. **__beam__** that travel the whole screen instantly " +
                "and **__sonic boom__** that require a **__charge__** to execute aren't " +
                "usually called fireballs. \"Fireball\" can even be used as shorthand for " +
                "\"quarter circle forward\", if you're trying to quickly describe a special move " +
                "input. \"The input for your **__command dash__** is fireball + kick\" would " +
                "be a valid sentence, for example."

        //when
        val result = string.toMarkdown()

        //then
        assertThat(result).isEqualTo(expected)
    }
    
    @Test
    fun `toMarkdown handles links`() {
        //given
        val string = "A complete list of the inner workings of every move in a fighting game. " +
                "Pretty much everything will be measured with !<'frame','frames'>, a " +
                "fighting game's fundamental building block of time. You can learn the " +
                "!<'startup'>, !<'active'>, and !<'recovery'> frames of each move, what " +
                "the !<'frame advantage'> is when the move hits or is blocked, how much " +
                "damage each move does, and any other special properties the move might have, " +
                "like hitting !<'overhead'> or !<'low'>.<br><br>Frame data can intimidate " +
                "people, because it's a ?<'https://docs.google.com/spreadsheets/d/1EBONXi2TCD1gTS2GbcB4z6ZYEoq3NNYp99kSxstKACE/edit#gid=0','giant spreadsheet'> " +
                "that looks pretty overwhelming. But, really, frame data is not intended to be memorized " +
                "like a list of formulas for your high school math class. The two most important numbers " +
                "are the !<'startup'> of a move (\"how fast is it?\") and how !<'safe'> or !<'unsafe'> " +
                "the move is if it gets blocked (\"how risky is it to use?\"). When you're getting started " +
                "with frame data, you can generally skip all the other numbers and focus on these. Look for " +
                "fast moves, and safe moves, then try these out in matches and see how you do! Then, " +
                "when you get more practice with the game, the other numbers will make more sense naturally."
        val expected = "A complete list of the inner workings of every move in a fighting game. " +
                "Pretty much everything will be measured with **__frame__**, a fighting " +
                "game's fundamental building block of time. You can learn the **__startup__**," +
                " **__active__**, and **__recovery__** frames of each move, what the " +
                "**__frame advantage__** is when the move hits or is blocked, how much " +
                "damage each move does, and any other special properties the move might have, " +
                "like hitting **__overhead__** or **__low__**.\n\n" +
                "Frame data can intimidate people, because it's a " +
                "[**giant spreadsheet**](https://docs.google.com/spreadsheets/d/1EBONXi2TCD1gTS2GbcB4z6ZYEoq3NNYp99kSxstKACE/edit#gid=0) " +
                "that looks pretty overwhelming. But, really, frame data is not intended to be memorized " +
                "like a list of formulas for your high school math class. The two most important numbers " +
                "are the **__startup__** of a move (\"how fast is it?\") and how **__safe__** or " +
                "**__unsafe__** the move is if it gets blocked (\"how risky is it to use?\"). " +
                "When you're getting started with frame data, you can generally skip all the other numbers " +
                "and focus on these. Look for fast moves, and safe moves, then try these out in matches " +
                "and see how you do! Then, when you get more practice with the game, the other numbers " +
                "will make more sense naturally."

        //when
        val result = string.toMarkdown()

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region URL
    @Test
    fun `toUrl handles basic term url`() {
        // given
        val string = "Fireball"
        val expected = "https://glossary.infil.net/?t=Fireball"

        // when
        val result = string.toUrl()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `toUrl handles spaces`() {
        // given
        val string = "White Girl Sweep"
        val expected = "https://glossary.infil.net/?t=White%20Girl%20Sweep"

        // when
        val result = string.toUrl()

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    @Test
    fun `toDomain handles image`() {
        // given
        val term = GlossaryItemDto(
            term = "White Girl Sweep",
            def = "",
            image = listOf(
                "jpg",
                "descriptionWhite Girl Sweeps from Karin (Street Fighter V), Lili (Tekken 8), Wagner (Under Night In-Birth II), and Powered Ciel (Melty Blood: Type Lumina)."
            ),
        )
        val expected = GlossaryItem(
            term = "White Girl Sweep",
            definition = "",
            url = GlossaryItem.Url(
                term = "https://glossary.infil.net/?t=White%20Girl%20Sweep",
                image = "https://glossary.infil.net/images/terms/White%20Girl%20Sweep.jpg"
            )
        )

        // when
        val result = term.toDomain()

        //then
        assertThat(result).isEqualTo(expected)
    }
}
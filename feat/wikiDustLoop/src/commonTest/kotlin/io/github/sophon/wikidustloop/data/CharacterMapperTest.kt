package io.github.sophon.wikidustloop.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.featureConfig.model.Game
import kotlin.test.Test

class CharacterMapperTest {
    val gg = Game.GGST.id
    val bb = Game.BBCF.id
    val mt = Game.MTFS.id
    
    //region ID
    @Test
    fun `mapper forms ID from standard name`() {
        //given
        val char = CharacterSource.magneto
        val expected = "magneto"

        //when
        val result = char.toDomain(emptyMap(), mt)

        //then
        assertThat(result.id).isEqualTo(expected)
    }

    @Test
    fun `mapper forms ID from name with space`() {
        //given
        val char = CharacterSource.jam
        val expected = "jam_kuradoberi"

        //when
        val result = char.toDomain(emptyMap(), gg)

        //then
        assertThat(result.id).isEqualTo(expected)
    }

    @Test
    fun `mapper froms ID from name with dots`() {
        //given
        val char = CharacterSource.aba
        val expected = "aba"

        //when
        val result = char.toDomain(emptyMap(), gg)

        //then
        assertThat(result.id).isEqualTo(expected)
    }

    @Test
    fun `mapper forms ID from name with special symbols`() {
        //given
        val char = CharacterSource.jacko
        val expected = "jacko"

        //when
        val result = char.toDomain(emptyMap(), gg)

        //then
        assertThat(result.id).isEqualTo(expected)
    }
    //endregion

    //region query
    @Test
    fun `formQuery keeps spaces`() {
        //given
        val char = CharacterSource.jam
        val expected = "Jam Kuradoberi"

        //when
        val result = char.toDomain(emptyMap(), gg)

        //then
        assertThat(result.remoteQueryId).isEqualTo(expected)
    }

    @Test
    fun `formQuery handles name with special symbols`() {
        //given
        val char = CharacterSource.jacko
        val expected = "Jack-O"

        //when
        val result = char.toDomain(emptyMap(), gg)

        //then
        assertThat(result.remoteQueryId).isEqualTo(expected)
    }
    //endregion

    //region wiki url
    @Test
    fun `formWikiUrl handles standard name`() {
        //given
        val char = CharacterSource.magneto
        val expected = "https://www.dustloop.com/w/MTFS/Magneto"

        //when
        val result = char.toDomain(emptyMap(), mt)

        //then
        assertThat(result.wikiUrl).isEqualTo(expected)
    }

    @Test
    fun `formWikiUrl handles spaces`() {
        //given
        val char = CharacterSource.jam
        val expected = "https://www.dustloop.com/w/GGST/Jam_Kuradoberi"

        //when
        val result = char.toDomain(emptyMap(), gg)

        //then
        assertThat(result.wikiUrl).isEqualTo(expected)
    }

    @Test
    fun `formWikiUrl handles apostrophe`() {
        // given
        val char = CharacterSource.susano
        val expected = "https://www.dustloop.com/w/BBCF/Susano%27o"

        // when
        val result = char.toDomain(emptyMap(), bb)

        //then
        assertThat(result.wikiUrl).isEqualTo(expected)
    }

    @Test
    fun `formWikiUrl handles dots`() {
        // given
        val char = CharacterSource.hellsing
        val expected = "https://www.dustloop.com/w/BBCF/Valkenhayn_R._Hellsing"

        // when
        val result = char.toDomain(emptyMap(), bb)

        //then
        assertThat(result.wikiUrl).isEqualTo(expected)
    }

    @Test
    fun `formWikiUrl handles special symbols`() {
        //given
        val char = CharacterSource.aba
        val expected = "https://www.dustloop.com/w/GGST/A.B.A"

        //when
        val result = char.toDomain(emptyMap(), gg)

        //then
        assertThat(result.wikiUrl).isEqualTo(expected)
    }
    //endregion
    
    //region aliases
    @Test
    fun `createAliases handles one word name`() {
        //given
        val char = CharacterSource.nagoriyuki
        val expected = listOf("na")
        
        //when
        val result = char.toDomain(emptyMap(), gg)

        //then
        assertThat(result.aliasList).isEqualTo(expected)
    }

    @Test
    fun `createAliases handles two word names`() {
        //given
        val char = CharacterSource.ky
        val expected = listOf("ky", "kk",)

        //when
        val result = char.toDomain(emptyMap(), gg)

        //then
        assertThat(result.aliasList).isEqualTo(expected)
    }

    @Test
    fun `createAliases handles special chars`() {
        //given
        val char1 = CharacterSource.jacko
        val expected1 = listOf("jc", "jacko")
        val char2 = CharacterSource.aba
        val expected2 = listOf("ab", "aba")
        
        //when
        val result1 = char1.toDomain(emptyMap(), gg)
        val result2 = char2.toDomain(emptyMap(), gg)

        //then
        assertThat(result1.aliasList).isEqualTo(expected1)
        assertThat(result2.aliasList).isEqualTo(expected2)
    }

    @Test
    fun `createAliases only forms initials from multi char word`() {
        //given
        val char = CharacterSource.jam
        val expected = listOf("ja", "jam", "jk")

        //when
        val result = char.toDomain(emptyMap(), gg)

        //then
        assertThat(result.aliasList).isEqualTo(expected)
    }

    @Test
    fun `createAliases removes numbers`() {
        //given
        val char = CharacterSource.zato
        val expected = listOf("za", "zato", "zato1")

        //when
        val result = char.toDomain(emptyMap(), gg)

        //then
        assertThat(result.aliasList).isEqualTo(expected)
    }

    @Test
    fun `create aliases for BB handles dots`() {
        // given
        val char = CharacterSource.hellsing
        val expected = listOf("valkenhayn", "valk", "vh")

        // when
        val result = char.toDomain(emptyMap(), bb)

        //then
        assertThat(result.aliasList).isEqualTo(expected)
    }

    @Test
    fun `create aliases for BB handles hyphens`() {
        // given
        val char = CharacterSource.lambda
        val expected = listOf(
            "lambda",
            "rm",
        )

        // when
        val result = char.toDomain(emptyMap(), bb)
        
        //then
        assertThat(result.aliasList).isEqualTo(expected)
    }

    @Test
    fun `create aliases for BB handles apostrophe`() {
        // given
        val string = CharacterSource.susano
        val expected = listOf("susano", "susanoo", "su")

        // when
        val result = string.toDomain(emptyMap(), bb)

        //then
        assertThat(result.aliasList).isEqualTo(expected)
    }

    //Taokaka → tk
    @Test
    fun `createAliases for BB doesn't create an alias for single name`() {
        // given
        val string = CharacterSource.taokaka
        val expected = listOf("tao", "tk")

        // when
        val result = string.toDomain(emptyMap(), bb)

        //then
        assertThat(result.aliasList).isEqualTo(expected)
    }
    //endregion

    //region umo
    @Test
    fun `mapper formats regular umo`() {
        //given
        val char = CharacterSource.hellsing
        val expected = listOf("Wolf Movement")

        //when
        val result = char.toDomain(emptyMap(), bb)

        //then
        assertThat(result.umo).isEqualTo(expected)
    }

    @Test
    fun `mapper formats link umo`() {
        //given
        val char = CharacterSource.jam
        val expected = listOf(
            "[Bakushuu](https://www.dustloop.com/w/GGST/Jam_Kuradoberi#Bakushuu)",
            "[Choujin](https://www.dustloop.com/w/GGST/Jam_Kuradoberi#Choujin)",
        )

        //when
        val result = char.toDomain(emptyMap(), gg)

        //then
        assertThat(result.umo).isEqualTo(expected)
    }

    @Test
    fun `mapper formats html umo`() {
        //given
        val char = CharacterSource.magneto
        val expected = listOf("Free Flight", "Multi-dash")

        //when
        val result = char.toDomain(emptyMap(), mt)

        //then
        assertThat(result.umo).isEqualTo(expected)
    }
    //endregion
}

private object CharacterSource {
    val magneto = CharacterDto(
        name = "Magneto",
        prejump = "?F",
        backdash = "??F Duration",
        team = "Knights of Doom",
        umo = "<span class=\"tooltip\">Free Flight<span class=\"tooltiptext\">From MARVEL Tōkon: Fighting Souls<span class=\"tooltiptext-hr\"></span>Unique air option that allows the character to hover for a set duration by holding [[File:InputIcon_8.png|24x24px|link=|alt=8]] in the air.</span></span>, <span class=\"tooltip\">Multi-dash<span class=\"tooltiptext\">From MARVEL Tōkon: Fighting Souls<span class=\"tooltiptext-hr\"></span>Unique air option that allows the character to double tap any direction in the air to perform a directional air dash.</span></span>",
        portrait = "MTFS_Magneto_Portrait.png",
        icon = "MTFS_Magneto_Icon.png",
    )

    val jam = CharacterDto(
        name = "Jam Kuradoberi",
        defense = "-14",
        guts = "2",
        guardBalance = "32",
        prejump = "4",
        backdash = "",
        backdashDuration = "17",
        backdashInvuln = "1-4F",
        backdashAirborne = "",
        backdashDistance = "",
        forwardDash = "",
        umo = "[[GGST/Jam Kuradoberi#Bakushuu|Bakushuu]], [[GGST/Jam Kuradoberi#Choujin|Choujin]]",
        jumpDuration = "",
        highJumpDuration = "",
        jumpHeight = "",
        highJumpHeight = "",
        earliestIad = "",
        adDuration = "18/24",
        abdDuration = "6/11",
        adDistance = "",
        abdDistance = "",
        movementTension = "10",
        jumpTension = "3",
        airDashTension = "5",
        walkSpeed = "7.9",
        backWalkSpeed = "6.1",
        dashInitialSpeed = "17",
        dashAcceleration = "0.66",
        dashFriction = "100",
        jumpGravity = "1.9",
        highJumpGravity = "1.9",
        boostAttack = "0",
        boostDefense = "0",
        portrait = "GGST_Jam_Kuradoberi_Portrait.png",
        icon = "GGST_Jam_Kuradoberi_Icon.png",
        navImage = "GGST_Jam_Kuradoberi_Navigation_Icon.png",
    )
    val aba = CharacterDto(
        name = "A.B.A",
        defense = "-26",
        guts = "2",
        guardBalance = "32",
        prejump = "4",
        backdash = "24",
        backdashDuration = "24",
        backdashInvuln = "1-6F",
        backdashAirborne = "1-15F",
        backdashDistance = "228.188",
        forwardDash = "",
        umo = "[[GGST/A.B.A#Haul and Heed|Haul and Heed]]",
        jumpDuration = "43",
        highJumpDuration = "51",
        jumpHeight = "340.1",
        highJumpHeight = "479",
        earliestIad = "8 [high jump: 7]",
        adDuration = "18/24",
        abdDuration = "6/11",
        adDistance = "468.125",
        abdDistance = "195.75",
        movementTension = "",
        jumpTension = "",
        airDashTension = "",
        walkSpeed = "4.4",
        backWalkSpeed = "3.8",
        dashInitialSpeed = "6",
        dashAcceleration = "0.15",
        dashFriction = "100",
        jumpGravity = "1.9",
        highJumpGravity = "1.9",
        boostAttack = "0",
        boostDefense = "0",
        portrait = "GGST_A.B.A_Portrait.png",
        icon = "GGST_A.B.A_Icon.png",
        navImage = "GGST_A.B.A_Navigation_Icon.png",
    )
    val jacko = CharacterDto(
        name = "Jack-O",
        defense = "16",
        guts = "2",
        guardBalance = "34",
        prejump = "4",
        backdash = "18/1-5 strike invuln/1-13 airborne",
        backdashDuration = "18",
        backdashInvuln = "1-5F",
        backdashAirborne = "1-13F",
        backdashDistance = "250.212",
        forwardDash = "",
        umo = "",
        jumpDuration = "42",
        highJumpDuration = "48",
        jumpHeight = "420",
        highJumpHeight = "522.5",
        earliestIad = "7",
        adDuration = "18/24",
        abdDuration = "6/11",
        adDistance = "735.625",
        abdDistance = "228.367",
        movementTension = "10",
        jumpTension = "3",
        airDashTension = "5",
        walkSpeed = "6.6",
        backWalkSpeed = "5",
        dashInitialSpeed = "11.9",
        dashAcceleration = "0.555",
        dashFriction = "100",
        jumpGravity = "2",
        highJumpGravity = "1.9",
        boostAttack = "0",
        boostDefense = "0",
        portrait = "GGST_Jack-O'_Portrait.png",
        icon = "GGST_Jack-O'_Icon.png",
        navImage = "GGST_Jack-O_Navigation_Icon.png",
    )
    val nagoriyuki = CharacterDto(
        name = "Nagoriyuki",
        defense = "-20",
        guts = "3",
        guardBalance = "30",
        prejump = "5",
        backdash = "23/1-6 strike invuln/1-16 airborne",
        backdashDuration = "23",
        backdashInvuln = "1-6F",
        backdashAirborne = "1-16F",
        backdashDistance = "241.027",
        forwardDash = "",
        umo = "[[GGST/Nagoriyuki#Fukyo|Fukyo]], Unique High Jump, No Run, No Airdash, No Double Jump",
        jumpDuration = "47",
        highJumpDuration = "49",
        jumpHeight = "389",
        highJumpHeight = "437",
        earliestIad = "",
        adDuration = "",
        abdDuration = "",
        adDistance = "",
        abdDistance = "",
        movementTension = "",
        jumpTension = "",
        airDashTension = "",
        walkSpeed = "5",
        backWalkSpeed = "5",
        dashInitialSpeed = "",
        dashAcceleration = "",
        dashFriction = "",
        jumpGravity = "1.9",
        highJumpGravity = "2.5",
        boostAttack = "0",
        boostDefense = "0",
        portrait = "GGST_Nagoriyuki_Portrait.png",
        icon = "GGST_Nagoriyuki_Icon.png",
        navImage = "GGST_Nagoriyuki_Navigation_Icon.png",
    )
    val ky = CharacterDto(
        name = "Ky Kiske",
        defense = "-12",
        guts = "2",
        guardBalance = "32",
        prejump = "4",
        backdash = "20/1-5 strike invuln/1-15 airborne",
        backdashDuration = "20",
        backdashInvuln = "1-5F",
        backdashAirborne = "1-15F",
        backdashDistance = "248.712",
        forwardDash = "",
        umo = "",
        jumpDuration = "43",
        highJumpDuration = "53",
        jumpHeight = "340.1",
        highJumpHeight = "524.4",
        earliestIad = "8 [high jump: 7]",
        adDuration = "18/24",
        abdDuration = "6/11",
        adDistance = "735.625",
        abdDistance = "228.367",
        movementTension = "",
        jumpTension = "",
        airDashTension = "",
        walkSpeed = "6.6",
        backWalkSpeed = "5",
        dashInitialSpeed = "15",
        dashAcceleration = "0.555",
        dashFriction = "100",
        jumpGravity = "1.9",
        highJumpGravity = "1.9",
        boostAttack = "0",
        boostDefense = "0",
        portrait = "GGST_Ky_Kiske_Portrait.png",
        icon = "GGST_Ky_Kiske_Icon.png",
        navImage = "GGST_Ky_Kiske_Navigation_Icon.png",
    )
    val zato = CharacterDto(
        name = "Zato-1",
        defense = "4",
        guts = "0",
        guardBalance = "36",
        prejump = "4",
        backdash = "20/1-5 strike invuln/1-15 airborne",
        backdashDuration = "20",
        backdashInvuln = "1-5F",
        backdashAirborne = "1-15F",
        backdashDistance = "240.462",
        forwardDash = "",
        umo = "[[GGST/Zato-1#Flight|Flight]], No Double Jump, [[GGST/Zato-1#Break_The_Law|Break The Law]], [[GGST/Zato-1#Eddie_Teleport|Eddie Teleport]]",
        jumpDuration = "38",
        highJumpDuration = "57",
        jumpHeight = "404.7",
        highJumpHeight = "751.8",
        earliestIad = "8 [high jump: 7]",
        adDuration = "14/61",
        abdDuration = "14/61",
        adDistance = "260",
        abdDistance = "242",
        movementTension = "",
        jumpTension = "",
        airDashTension = "",
        walkSpeed = "4.9",
        backWalkSpeed = "3.8",
        dashInitialSpeed = "13.5",
        dashAcceleration = "0.416",
        dashFriction = "100",
        jumpGravity = "2.3",
        highJumpGravity = "1.9",
        boostAttack = "0",
        boostDefense = "0",
        portrait = "GGST_Zato-1_Portrait.png",
        icon = "GGST_Zato-1_Icon.png",
        navImage = "GGST_Zato-1_Navigation_Icon.png",
    )

    val susano = CharacterDto(
        name = "Susano'o",
        health = "12,500",
        prejump = "4F",
        backdash = "22F (1~5F Inv All, 2~15 airborne)",
        forwardDash = "",
        umo = "",
        portrait = "BBCF_Susanoo_Portrait.png",
        icon = "BBCF_Susano'o_Icon.png",
    )
    val hellsing = CharacterDto(
        name = "Valkenhayn R. Hellsing",
        health = "10,500",
        prejump = "Human: 4F / Wolf: 4F",
        backdash = "Human: 22F (1~7F Inv All, 1~14 airborne)<br>Wolf: 25F (1~7F Inv All, 1~20 airborne)",
        forwardDash = "Human: 23F (5~14 airborne)",
        umo = "Wolf Movement",
        portrait = "BBCF_Valkenhayn_Portrait.png",
        icon = "BBCF_Valkenhayn_Icon.png",
    )
    val lambda = CharacterDto(
        name = "Lambda-11",
        health = "10,500",
        prejump = "4F",
        backdash = "25F (1~7F Inv All)",
        forwardDash = "",
        umo = "",
        portrait = "BBCF_Lambda_Portrait.png",
        icon = "BBCF_Lambda-11_Icon.png",
    )
    val taokaka = CharacterDto(
        name = "Taokaka",
        health = "10,000",
        prejump = "4F",
        backdash = "22F (1~7F Inv All, 1~11 airborne)",
        forwardDash = "",
        umo = "Extra air option<br>[[BBCF/Taokaka#Drive Moves|Dancing Edge]]<br/>[[BBCF/Taokaka#Crawl|Crawl]]",
        portrait = "BBCF_Taokaka_Portrait.png",
        icon = "BBCF_Taokaka_Icon.png",
    )
}

package io.github.sophon.discord.domain.model

internal enum class Emoji(val id: String) {
    TK_HEAT("fn_tk_heat:1487469502286532618"),
    TK_BALCONY("fn_tk_balcony:1487473524229804063"),
    TK_FLOOR("fn_tk_floor:1487469431327031328"),
    TK_TORNADO("fn_tk_tornado:1487469696923078726"),
    TK_HOMING("fn_tk_homing:1487469538776711390"),
    TK_THROW("fn_tk_throw:1487469629919199456"),
    TK_PC("fn_tk_pc:1487469585556045885"),
    TK_CHIP("fn_tk_chip:1487469368945414275");

    override fun toString() = "<:$id> "
}

package io.github.sophon.discord.app.port.outbound

internal interface CheckFileExistsPort {
    fun exists(path: String): Boolean
}

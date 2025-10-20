package com.example.cornerman

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
package io.github.sophon.cornerman.screens.home.data

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

fun getCharacterListDatabaseBuilder(): RoomDatabase.Builder<CharacterListDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), "character_list.db")
    return Room.databaseBuilder<CharacterListDatabase>(name = dbFile.absolutePath)
}
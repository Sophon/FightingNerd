package io.github.sophon.fightingnerd.screens.home.data

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSHomeDirectory

fun getCharacterListDatabaseBuilder(
    dbName: String = "characterList.db"
): RoomDatabase.Builder<CharacterListDatabase> {
    val dbFile = NSHomeDirectory() + "/$dbName"
    return Room.databaseBuilder<CharacterListDatabase>(name = dbFile)
}
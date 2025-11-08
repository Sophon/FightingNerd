package io.github.sophon.cornerman.screens.home.data

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSHomeDirectory

fun getCharacterListDatabaseBuilder(): RoomDatabase.Builder<CharacterListDatabase> {
    val dbFile = NSHomeDirectory() + "/characterList.db"
    return Room.databaseBuilder<CharacterListDatabase>(name = dbFile)
}
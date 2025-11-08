package io.github.sophon.cornerman.screens.home.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.sophon.cornerman.screens.home.data.CharacterListDatabase

fun getCharacterListDatabaseBuilder(
    context: Context
): RoomDatabase.Builder<CharacterListDatabase> {
    val dbFile = context.getDatabasePath("characterList.db")
    return Room.databaseBuilder<CharacterListDatabase>(
        context = context,
        name = dbFile.absolutePath,
    )
}
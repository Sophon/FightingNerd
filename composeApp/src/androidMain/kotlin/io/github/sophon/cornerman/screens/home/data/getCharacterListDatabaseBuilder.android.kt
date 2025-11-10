package io.github.sophon.cornerman.screens.home.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getCharacterListDatabaseBuilder(
    context: Context,
    dbName: String = "characterList.db"
): RoomDatabase.Builder<CharacterListDatabase> {
    val dbFile = context.getDatabasePath(dbName)
    return Room.databaseBuilder<CharacterListDatabase>(
        context = context,
        name = dbFile.absolutePath,
    )
}
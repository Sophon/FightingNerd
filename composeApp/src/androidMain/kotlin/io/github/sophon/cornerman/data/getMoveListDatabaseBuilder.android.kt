package io.github.sophon.cornerman.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.sophon.cornerman.screens.moveList.data.MoveListDatabase

fun getMoveListDatabaseBuilder(context: Context): RoomDatabase.Builder<MoveListDatabase> {
    val dbFile = context.getDatabasePath("moveList.db")
    return Room.databaseBuilder<MoveListDatabase>(
        context = context,
        name = dbFile.absolutePath,
    )
}
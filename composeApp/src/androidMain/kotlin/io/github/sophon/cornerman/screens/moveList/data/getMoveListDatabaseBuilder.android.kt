package io.github.sophon.cornerman.screens.moveList.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getMoveListDatabaseBuilder(
    context: Context,
    dbName: String = "moveList.db"
): RoomDatabase.Builder<MoveListDatabase> {
    val dbFile = context.getDatabasePath(dbName)
    return Room.databaseBuilder<MoveListDatabase>(
        context = context,
        name = dbFile.absolutePath,
    )
}
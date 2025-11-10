package io.github.sophon.cornerman.screens.moveList.data

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSHomeDirectory

fun getMoveListDatabaseBuilder(
    dbName: String = "moveList.db"
): RoomDatabase.Builder<MoveListDatabase> {
    val dbFile = NSHomeDirectory() + "/$dbName"
    return Room.databaseBuilder<MoveListDatabase>(name = dbFile)
}
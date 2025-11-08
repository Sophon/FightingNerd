package io.github.sophon.cornerman.screens.moveList.data

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSHomeDirectory

fun getMoveListDatabaseBuilder(): RoomDatabase.Builder<MoveListDatabase> {
    val dbFile = NSHomeDirectory() + "/moveList.db"
    return Room.databaseBuilder<MoveListDatabase>(name = dbFile)
}
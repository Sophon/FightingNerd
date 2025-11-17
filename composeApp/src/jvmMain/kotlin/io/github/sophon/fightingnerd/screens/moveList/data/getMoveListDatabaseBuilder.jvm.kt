package io.github.sophon.fightingnerd.screens.moveList.data

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

fun getMoveListDatabaseBuilder(
    dbName: String = "moveList.db"
): RoomDatabase.Builder<MoveListDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), dbName)
    return Room.databaseBuilder<MoveListDatabase>(name = dbFile.absolutePath)
}
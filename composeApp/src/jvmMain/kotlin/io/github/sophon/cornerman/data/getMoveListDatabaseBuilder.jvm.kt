package io.github.sophon.cornerman.data

import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.sophon.cornerman.screens.moveList.data.MoveListDatabase
import java.io.File

fun getMoveListDatabaseBuilder(): RoomDatabase.Builder<MoveListDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), "wavu_move_list.db")
    return Room.databaseBuilder<MoveListDatabase>(name = dbFile.absolutePath)
}
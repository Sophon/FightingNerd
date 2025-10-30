package com.example.cornerman.screens.moveList.data

import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cornerman.AppDatabase
import platform.Foundation.NSHomeDirectory

actual fun getDatabaseBuilder(context: Any): RoomDatabase.Builder<AppDatabase> {
    val dbFilePath = NSHomeDirectory() + "/wavu_move_list.db"
    return Room.databaseBuilder<AppDatabase>(name = dbFilePath)
}
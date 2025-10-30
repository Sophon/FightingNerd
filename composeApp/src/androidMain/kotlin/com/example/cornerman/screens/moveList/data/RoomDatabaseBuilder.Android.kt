package com.example.cornerman.screens.moveList.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cornerman.AppDatabase

actual fun getDatabaseBuilder(
    context: Any,
): RoomDatabase.Builder<AppDatabase> {
    val appContext = context as Context
    val dbFile = appContext.getDatabasePath("wavu_move_list.db")
    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath,
    )
}
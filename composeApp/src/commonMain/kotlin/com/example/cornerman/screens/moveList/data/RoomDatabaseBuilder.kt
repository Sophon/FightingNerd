package com.example.cornerman.screens.moveList.data

import androidx.room.RoomDatabase
import com.example.cornerman.AppDatabase

expect fun getDatabaseBuilder(context: Any): RoomDatabase.Builder<AppDatabase>
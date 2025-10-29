package com.example.cornerman

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cornerman.screens.moveList.data.MoveDao
import com.example.cornerman.screens.moveList.data.MoveEntity

@Database(
    entities = [
        MoveEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun moveDao(): MoveDao
}
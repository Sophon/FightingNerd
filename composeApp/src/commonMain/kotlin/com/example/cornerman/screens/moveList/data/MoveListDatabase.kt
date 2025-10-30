package com.example.cornerman.screens.moveList.data

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [
        MoveEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@ConstructedBy(MoveListDatabaseFactory::class)
abstract class MoveListDatabase: RoomDatabase() {
    abstract fun moveListDao(): MoveListDao
}

/**
 * Room creates the expect classes itself
 */
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object MoveListDatabaseFactory: RoomDatabaseConstructor<MoveListDatabase>


fun getMoveListDatabase(
    builder: RoomDatabase.Builder<MoveListDatabase>,
): MoveListDatabase = builder
    .setDriver(BundledSQLiteDriver()) //multiplatform driver
    .setQueryCoroutineContext(Dispatchers.IO)
    .build()
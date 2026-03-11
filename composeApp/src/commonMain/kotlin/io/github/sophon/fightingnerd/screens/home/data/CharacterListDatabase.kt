package io.github.sophon.fightingnerd.screens.home.data

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [
        CharacterEntity::class,
    ],
    version = 5,
    exportSchema = true,
)
@ConstructedBy(CharacterListDatabaseFactory::class)
abstract class CharacterListDatabase: RoomDatabase() {
    abstract fun characterListDao(): CharacterListDao
}


/**
 * Room creates the expect classes itself
 */
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object CharacterListDatabaseFactory: RoomDatabaseConstructor<CharacterListDatabase>

fun getCharacterListDatabase(
    builder: RoomDatabase.Builder<CharacterListDatabase>,
): CharacterListDatabase = builder
    .setDriver(BundledSQLiteDriver()) //multiplatform driver
    .setQueryCoroutineContext(Dispatchers.IO)
    .build()
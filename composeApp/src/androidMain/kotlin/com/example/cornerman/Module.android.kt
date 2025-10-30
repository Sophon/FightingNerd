package com.example.cornerman

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.cornerman.screens.moveList.data.RoomMoveListDB
import com.example.cornerman.screens.moveList.data.getDatabaseBuilder
import com.example.wikiwavu.data.MoveListDB
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule = module {
    single {
        getDatabaseBuilder(androidContext())
            .setDriver(BundledSQLiteDriver())
            .build()
    }
    single { get<AppDatabase>().moveDao() }
    single< MoveListDB> { RoomMoveListDB(get()) }
}
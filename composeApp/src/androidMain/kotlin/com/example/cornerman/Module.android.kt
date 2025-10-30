package com.example.cornerman

import androidx.room.RoomDatabase
import com.example.cornerman.screens.moveList.data.MoveListDatabase
import com.example.cornerman.screens.moveList.data.getMoveListDatabaseBuilder
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule = module {
    singleOf(::getMoveListDatabaseBuilder)
        .bind<RoomDatabase.Builder<MoveListDatabase>>()
}
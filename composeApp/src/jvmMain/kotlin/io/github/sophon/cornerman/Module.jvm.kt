package io.github.sophon.cornerman

import androidx.room.RoomDatabase
import io.github.sophon.cornerman.screens.moveList.data.MoveListDatabase
import io.github.sophon.cornerman.data.getMoveListDatabaseBuilder
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule: Module = module {
    singleOf(::getMoveListDatabaseBuilder)
        .bind<RoomDatabase.Builder<MoveListDatabase>>()
}
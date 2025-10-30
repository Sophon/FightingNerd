package com.example.cornerman

import org.koin.dsl.module

actual val platformModule = module {
    single {
        getDatabaseBuilder()
            .setDriver(BundledSQLiteDriver())
            .build()
    }
    single { get<AppDatabase>().moveDao() }
    single<MoveListDB> { RoomMoveListDB(get()) }
}
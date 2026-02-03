package io.github.sophon.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.sophon.ewgf.data.EwgfDatabase
import java.io.File

actual class DatabaseDriverFactory(
    private val databasePath: String,
) {
    actual fun createDriver(): SqlDriver {
        val databaseFile = File(databasePath)
        val versionFile = File("$databasePath.version")

        val currentSchemaVersion = 2 // Increment when schema changes
        val savedVersion = versionFile.takeIf { it.exists() }?.readText()?.toIntOrNull() ?: 0

        val driver = JdbcSqliteDriver("jdbc:sqlite:$databasePath")

        if (savedVersion != currentSchemaVersion) {
            // Schema changed - drop and recreate
            driver.execute(null, "DROP TABLE IF EXISTS ban", 0)
            EwgfDatabase.Schema.create(driver)
            versionFile.writeText(currentSchemaVersion.toString())
        } else if (!databaseFile.exists()) {
            // New database
            EwgfDatabase.Schema.create(driver)
            versionFile.writeText(currentSchemaVersion.toString())
        }

        return driver
    }
    
    
    companion object {
        fun getDatabasePath(): String {
            // Fly.io: /data/playerlist.db
            // Local: playerlist.db (in working directory)
            return System.getenv("DATABASE_PATH") ?: "playerlist.db"
        }
    }
}
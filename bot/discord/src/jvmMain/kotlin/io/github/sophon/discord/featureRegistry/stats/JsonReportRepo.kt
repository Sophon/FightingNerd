package io.github.sophon.discord.featureRegistry.stats

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.mapError
import io.github.sophon.data.ReportRepo
import io.github.sophon.discord.data.FileManager
import io.github.sophon.domain.StatsError
import io.github.sophon.domain.model.DailyReport
import kotlinx.serialization.json.Json

internal class JsonReportRepo(
    private val json: Json,
    private val fileManager: FileManager,
    private val path: String,
): ReportRepo {
    override suspend fun init(): EmptyResult<StatsError> {
        return if (fileManager.exists(path)) {
            Result.Success(Unit)
        } else {
            fileManager.create(path)
                .map {
                    fileManager.write(path, "[]")
                    Unit
                }
                .mapError { StatsError.FileError(it.toString()) }
        }
    }

    override suspend fun load(): Result<List<DailyReport>, StatsError> {
        return fileManager.read(path)
            .map { content ->
                json.decodeFromString<List<DailyReport>>(content)
            }
            .mapError { StatsError.FileError(it.toString()) }
    }

    override suspend fun save(dailyReportList: List<DailyReport>): EmptyResult<StatsError> {
        val content = json.encodeToString(dailyReportList)
        return fileManager.write(path, content)
            .mapError { StatsError.FileError(it.toString()) }
    }


    companion object {
        fun getReportLogPath(): String {
            return System.getenv("COMMAND_LOG_PATH") ?: "commandLog.json"
        }
    }
}

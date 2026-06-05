package io.github.sophon.discord.feat.core.data

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.mapError
import io.github.sophon.integration.data.ReportRepo
import io.github.sophon.integration.model.StatsError
import io.github.sophon.integration.model.DailyReport
import kotlinx.serialization.json.Json

internal class JsonReportRepo(
    private val json: Json,
    private val fileManager: FileManager,
): ReportRepo {
    private val path = getReportLogPath()

    override suspend fun init(): EmptyResult<StatsError> {
        if (fileManager.exists(path).not()) {
            return fileManager.create(path)
                .map {
                    fileManager.write(path, "[]")
                    Unit
                }
                .mapError { StatsError.FileError(it.toString()) }
        }

        val content = fileManager.read(path)
        if (content is Result.Success && content.data.isBlank()) {
            return fileManager.write(path, "[]")
                .mapError { StatsError.FileError(it.toString()) }
        }

        return Result.Success(Unit)
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


    private fun getReportLogPath(): String {
        return System.getenv("COMMAND_LOG_PATH") ?: "commandLog.json"
    }
}
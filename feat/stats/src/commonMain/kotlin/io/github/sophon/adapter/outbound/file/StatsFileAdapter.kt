package io.github.sophon.adapter.outbound.file

import io.github.sophon.app.outPort.DayReportPort
import io.github.sophon.app.outPort.MonthReportPort
import io.github.sophon.app.outPort.PrepareStoragePort
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.model.DailyReport
import io.github.sophon.model.StatsError
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.IOException
import okio.Path.Companion.toPath

internal class StatsFileAdapter(
    private val json: Json,
    private val fileSystem: FileSystem,
    directory: String,
) : PrepareStoragePort, DayReportPort, MonthReportPort {
    private val directoryPath = directory.toPath()

    override suspend fun prepare(): EmptyResult<StatsError> {
        return try {
            fileSystem.createDirectories(directoryPath)
            Result.Success(Unit)
        } catch (e: IOException) {
            Result.Error(StatsError.FileError(directoryPath.toString(), e.toString()))
        }
    }

    override suspend fun loadDay(): Result<DailyReport?, StatsError> {
        return load<DailyReport>(DAY_FILE_NAME)
    }

    override suspend fun saveDay(dailyReport: DailyReport): EmptyResult<StatsError> {
        return save(DAY_FILE_NAME, dailyReport)
    }

    override suspend fun loadMonth(): Result<List<DailyReport>, StatsError> {
        val result = load<List<DailyReport>>(MONTH_FILE_NAME)
            .map { dailyReportList -> dailyReportList.orEmpty() }
        return result
    }

    override suspend fun saveMonth(dailyReportList: List<DailyReport>): EmptyResult<StatsError> {
        return save(MONTH_FILE_NAME, dailyReportList)
    }


    /**
     * A missing or blank file is no data yet, not an error.
     */
    private inline fun <reified T> load(fileName: String): Result<T?, StatsError> {
        val path = (directoryPath / fileName)
        return try {
            if (fileSystem.exists(path).not()) {
                return Result.Success(null)
            }

            val content = fileSystem.read(path) { readUtf8() }
            val decoded = if (content.isBlank()) null else json.decodeFromString<T>(content)
            Result.Success(decoded)
        } catch (e: IOException) {
            Result.Error(StatsError.FileError(path.toString(), e.toString()))
        } catch (e: SerializationException) {
            Result.Error(StatsError.SerializationError(path.toString(), e.toString()))
        }
    }

    /**
     * Writes to a temp file and moves it over the target, so a deploy killing the process mid-write
     * leaves the previous file intact instead of a truncated one.
     */
    private inline fun <reified T> save(fileName: String, value: T): EmptyResult<StatsError> {
        val path = (directoryPath / fileName)
        val tempPath = (directoryPath / "$fileName$TEMP_SUFFIX")
        return try {
            val content = json.encodeToString(value)
            fileSystem.write(tempPath) { writeUtf8(content) }
            fileSystem.atomicMove(tempPath, path)
            Result.Success(Unit)
        } catch (e: IOException) {
            Result.Error(StatsError.FileError(path.toString(), e.toString()))
        } catch (e: SerializationException) {
            Result.Error(StatsError.SerializationError(path.toString(), e.toString()))
        }
    }


    private companion object {
        const val DAY_FILE_NAME = "day.json"
        const val MONTH_FILE_NAME = "month.json"
        const val TEMP_SUFFIX = ".tmp"
    }
}

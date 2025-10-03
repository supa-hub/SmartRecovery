package com.kunto.smartrecovery.backend.filehandling

import androidx.compose.ui.util.fastZip
import com.kunto.smartrecovery.getPlatform
import com.kunto.smartrecovery.json.UserProfile
import kotlinx.datetime.LocalDate
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.internal.readJson
import kotlinx.serialization.json.okio.decodeFromBufferedSource
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.SYSTEM
import okio.buffer
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


@OptIn(ExperimentalTime::class)
class FileHandler
{
    companion object {
        val base = getPlatform().cwd()
    }

    val parentDir = base / "sessions"
    val profileDir = base / "profile"

    init {
        if (!FileSystem.SYSTEM.exists(parentDir)) {
            try {
                FileSystem.SYSTEM
                    .createDirectories(parentDir)
            }
            catch (e: Exception) { }
        }
        if (!FileSystem.SYSTEM.exists(profileDir)) {
            try {
                FileSystem.SYSTEM
                    .createDirectories(profileDir)
            }
            catch (e: Exception) { }
        }
    }

    fun allFileNamesWithString(name: String): List<String> =
        FileSystem.SYSTEM
            .list(parentDir)
            .filter { it.name.contains(name) }
            .map { it.name }

    /**
     * Separates every row and separates the values of a row which are separated by a comma.
     * It stores every row as an array of String values.
     */
    fun getStringData(fileName: String): List<List<String>> =
        FileSystem.SYSTEM
            .read(parentDir / fileName.toPath()) {
                readUtf8()
            }
            .split("\n")
            .drop(1)
            .map { it.split(",") }


    /**
     * Returns the name of the file and the values from all of the files whose last modified date is within the given range
     */
    fun filesWithDateInRange(fileName: String, start: Instant, end: Instant): List<Triple<String, List<List<String>>, Instant>> =
        FileSystem.SYSTEM
            .list(parentDir)
            .filter { it.name.contains(fileName) }
            .filter { FileSystem.SYSTEM.metadata(it).createdAtMillis != null }
            .filter {
                val creationDate = Instant.fromEpochMilliseconds(FileSystem.SYSTEM.metadata(it).createdAtMillis ?: 0)
                start <= creationDate && creationDate <= end
            }
            .map { Triple(it.name, getStringData(it.name), Instant.fromEpochMilliseconds(FileSystem.SYSTEM.metadata(it).createdAtMillis ?: 0)) }
    fun getFileCreationDate(fileName: String): Instant?
    {
        val millis = FileSystem.SYSTEM
            .metadata(parentDir / fileName.toPath())
            .createdAtMillis
            ?: return null
        return Instant.fromEpochMilliseconds(millis)
    }

    fun deleteFile(fileName: String)
    {
        FileSystem.SYSTEM
            .delete(parentDir / fileName.toPath())
    }

    fun deleteFile(fileDir: Path)
    {
        FileSystem.SYSTEM
            .delete(parentDir / fileDir)
    }

    fun deleteAllFilesWith(fileName: String)
    {
        FileSystem.SYSTEM
            .list(parentDir)
            .filter {
                it.name.takeWhile { it != '.' }
                    .takeLastWhile { it != '_' } == fileName
            }
            .forEach { FileSystem.SYSTEM.delete(it) }
    }

    fun deleteAll()
    {
        FileSystem.SYSTEM
            .list(parentDir)
            .forEach { FileSystem.SYSTEM.delete(it) }
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun getUserProfile(): UserProfile?
    {
        return try {
            Json.decodeFromBufferedSource<UserProfile>(
                FileSystem.SYSTEM
                    .source(profileDir / "userProfile.json".toPath())
                    .buffer()
            )
        }
        catch (e: Exception) {
            null
        }
    }

    fun saveUserProfile(profile: UserProfile): Unit
    {
        FileSystem.SYSTEM
            .write(profileDir / "userProfile.json".toPath()) {
                writeUtf8(Json.encodeToString<UserProfile>(profile))
        }
    }
}
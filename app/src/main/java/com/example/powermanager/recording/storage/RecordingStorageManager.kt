package com.example.powermanager.recording.storage

import com.example.powermanager.recording.model.RecordingResult
import com.example.powermanager.utils.DOT_JSON
import com.google.gson.GsonBuilder
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.util.Calendar

object RecordingStorageManager {

    fun getMostRecentRecordingResultsNames(limit: Int, directory: File): List<String> {
        val allFiles: MutableList<File> = mutableListOf()

        if (!directory.exists())
            return listOf()

        // look at all files in the directory
        directory.walk().forEach { file ->
            if (file.isFile && file.name.endsWith(DOT_JSON))
                allFiles.add(file)
        }

        // sort the files by timestamp descending
        allFiles.sortByDescending { file ->
            val attributes: BasicFileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes::class.java)
            attributes.lastModifiedTime().toMillis()
        }

        // return the last `limit` files
        return allFiles.take(limit).map {
            it.nameWithoutExtension
        }
    }

    fun deleteRecordingResult(name: String, directory: File) : Boolean {
        return try {
            File(directory, "$name$DOT_JSON").delete()
            true
        } catch (_ : Exception) {
            false
        }
    }

    fun saveRecordingResult(result : RecordingResult, directory: File) : String {
        // first convert the result to JSON format
        val gson = GsonBuilder().setPrettyPrinting().create()
        val fileContent = gson.toJson(result)

        // check if the directory exists and if not, create it
        try {
            if (!directory.exists())
                directory.mkdirs()
        } catch (_ : Exception) {

        }

        var fileName : String = result.sessionName

        // check to see if a file with the same name already exists and if so, append
        // a timestamp (millis since Epoch) to the name of the current file
        if (File(directory, "$fileName$DOT_JSON").exists()) {
            fileName = "${fileName}_${Calendar.getInstance().timeInMillis}"
        }

        File(directory, "$fileName$DOT_JSON").writeText(fileContent)

        return fileName
    }
}
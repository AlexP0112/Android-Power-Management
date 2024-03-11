package com.example.powermanager.control.storage

import com.example.powermanager.utils.DOT_JSON
import com.example.powermanager.utils.NO_VALUE_STRING
import com.example.powermanager.utils.getPrettyStringFromNumberOfBytes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.util.Calendar

object CpuConfigurationsStorageManager {

    fun getSavedCpuConfigurationsNames(directory: File): List<String> {
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

        return allFiles.map {
            it.nameWithoutExtension
        }
    }

    fun deleteConfiguration(name: String, directory: File) : Boolean {
        return try {
            File(directory, "$name$DOT_JSON").delete()
            true
        } catch (_ : Exception) {
            false
        }
    }

    suspend fun saveCpuConfiguration(configuration : CpuConfiguration, directory: File) {
        withContext (Dispatchers.IO) {
            // first convert the result to JSON format
            val gson = GsonBuilder().setPrettyPrinting().create()
            val fileContent = gson.toJson(configuration)

            // check if the directory exists and if not, create it
            try {
                if (!directory.exists())
                    directory.mkdirs()
            } catch (_ : Exception) {
                return@withContext
            }

            var fileName : String = configuration.name

            // check to see if a file with the same name already exists and if so, append
            // a timestamp (millis since Epoch) to the name of the current file
            if (File(directory, "$fileName$DOT_JSON").exists()) {
                fileName = "${fileName}_${Calendar.getInstance().timeInMillis}"
            }

            File(directory, "$fileName$DOT_JSON").writeText(fileContent)
        }
    }

    fun getFileContent(fileName: String, directory: File) : String {
        val file = File(directory, "$fileName$DOT_JSON")
        if (!file.exists())
            return NO_VALUE_STRING

        val attributes: BasicFileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes::class.java)
        val fileSizeInBytes = attributes.size()

        return "File ${file.name} (${getPrettyStringFromNumberOfBytes(fileSizeInBytes)})\n\n${file.readText()}"
    }

    fun getCpuConfigurationForFileName(fileName: String, directory: File) : CpuConfiguration? {
        val file = File(directory, "$fileName$DOT_JSON")
        if (!file.exists())
            return null

        val fileContent = file.readText()

        val gson = Gson()

        return gson.fromJson(fileContent, CpuConfiguration::class.java)
    }

}

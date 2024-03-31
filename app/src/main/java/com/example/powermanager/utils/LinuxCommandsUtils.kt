package com.example.powermanager.utils

import com.example.powermanager.preferences.LoadAverageTypes
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Calendar

object LinuxCommandsUtils {

    fun readProtectedFileContent(filePath : String) : String {
        val command = String.format(CAT_FILE_AS_ROOT_COMMAND, filePath)
        val process = Runtime.getRuntime().exec(command)
        val fileContent = BufferedReader(InputStreamReader(process.inputStream)).readText()
        process.waitFor()

        return fileContent
    }

    fun getLoadAverageFromUptimeCommandOutput(commandOutput: String, loadAverageType: LoadAverageTypes): Float {
        val allLoads = commandOutput.split(LOAD_AVERAGE_SEMICOLON)[1].trim()
        val load = allLoads.split(COMMA)[loadAverageType.ordinal]

        return load.toFloat()
    }

    fun getInterfacesFromIfConfigOutput(output: String) : List<String> {
        return output.split("\n")
            .filter { it.trim().isNotEmpty() }
            .map { line ->
                line.split(" ", "\t")[0]
            }
    }

    fun getOnlineCoresFromFileContent(fileContent: String) : List<Int> {
        val result : MutableList<Int> = mutableListOf()

        fileContent
            .trim()
            .split("\n")
            .filter { line ->
                line.startsWith(PROCESSOR)
            }
            .forEach { processorLine ->
                // this is a line that looks like this: "processor       : <index>"
                val parts = processorLine.split("\\s+".toRegex())
                result.add(parts[2].toInt())
            }

        return result
    }

    fun getBytesSentAndReceivedByAllInternetInterfacesFromFileContent(fileContent: String) : List<Long> {
        var totalBytesReceived = 0L
        var totalBytesSent = 0L

        try {
            fileContent
                .split("\n")
                .drop(2)
                .map { it.trim() }
                .filter { interfaceInfo ->
                    interfaceInfo.startsWith(WLAN) || interfaceInfo.startsWith(RMNET)
                }
                .forEach { interfaceInfo ->
                    val parts = interfaceInfo.split("\\s+".toRegex())
                    totalBytesReceived += parts[1].toLong()
                    totalBytesSent += parts[9].toLong()
                }
        } catch (_ : Exception) {

        }

        return listOf(totalBytesReceived, totalBytesSent)
    }

    fun determineSystemBootTimestamp() : Long {
        try {
            // execute the uptime command
            val process = Runtime.getRuntime().exec(UPTIME_COMMAND)
            val uptimeOutput = BufferedReader(InputStreamReader(process.inputStream)).readText()
            process.waitFor()

            var systemUptimeMillis = 0L

            // get the section of the output that represents the actual uptime
            val uptimeParts = uptimeOutput.split(UP)[1] // output after "up"
                .split(USERS)[0] // output before "users"
                .split(COMMA).dropLast(1) // exclude the number of users
                .map { it.trim() } // eliminate the white spaces in the parts

            // parse this section
            for (part in uptimeParts) {
                if (part.contains(MIN)) // X min
                    systemUptimeMillis += part.split(SPACE)[0].trim().toLong() * MILLIS_IN_A_MINUTE

                if (part.contains(DAYS)) // X days
                    systemUptimeMillis += part.split(SPACE)[0].trim().toLong() * MILLIS_IN_A_DAY

                if (part.contains(SEMICOLON)) // X:Y, where X = hours, Y = minutes
                    systemUptimeMillis += part.split(SEMICOLON)[0].trim().toLong() * MILLIS_IN_AN_HOUR +
                            part.split(SEMICOLON)[1].trim().toLong() * MILLIS_IN_A_MINUTE
            }

            return Calendar.getInstance().timeInMillis - systemUptimeMillis
        } catch (e: Exception) {
            return Calendar.getInstance().timeInMillis
        }
    }
}

package com.example.powermanager.control.cpufreq

import com.example.powermanager.utils.AFFECTED_CPUS
import com.example.powermanager.utils.AVAILABLE_SCALING_GOVERNORS_PATH
import com.example.powermanager.utils.CHANGE_SCALING_GOVERNOR_FOR_CPU_COMMAND
import com.example.powermanager.utils.CPUFREQ_DIRECTORY_PATH
import com.example.powermanager.utils.CURRENT_SCALING_GOVERNOR_PATH
import com.example.powermanager.utils.readProtectedFileContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

object CpuFreqManager {

    fun getCurrentScalingGovernor() : String {
        val filePath = String.format(CURRENT_SCALING_GOVERNOR_PATH, 0)

        return readProtectedFileContent(filePath).trim()
    }

    fun getAvailableScalingGovernors() : List<String> {
        val fileContent = readProtectedFileContent(AVAILABLE_SCALING_GOVERNORS_PATH).trim()

        return fileContent.split("\\s+".toRegex())
    }

    suspend fun changeScalingGovernor(newGovernor: String, numberOfCores : Int) {
        withContext(Dispatchers.IO) {
            for (coreNumber in 0 until numberOfCores) {
                val command = String.format(CHANGE_SCALING_GOVERNOR_FOR_CPU_COMMAND, newGovernor, coreNumber)

                val process = Runtime.getRuntime().exec(command)
                process.waitFor()
            }
        }
    }

    /*
     * This function looks inside `/sys/devices/system/cpu/cpufreq/policyX/affected_cpus` files
     * and chooses the first half of the cores listed there as master cores
     */
    fun determineMasterCores(): List<Int> {
        val result : MutableList<Int> = mutableListOf()

        // determine all policy directories
        val policyDirectories = Files.walk(Paths.get(CPUFREQ_DIRECTORY_PATH), 1)
            .filter { Files.isDirectory(it) }
            .map { policy ->
                policy.toString()
            }
            .toArray()
            .filter { it != CPUFREQ_DIRECTORY_PATH }
            .map { it as String }

        policyDirectories.forEach { directory ->
            val affectedCpusFile = File(directory, AFFECTED_CPUS)
            val cpus = affectedCpusFile
                .readText()
                .trim()
                .split("\\s+".toRegex())
                .map { it.toInt() }

            if (cpus.size == 1 || cpus.size == 2) {
                result.add(cpus[0])
            } else if (cpus.size > 2) {
                result.addAll(cpus.dropLast(cpus.size / 2))
            }
        }

        result.sort()

        return result
    }

}
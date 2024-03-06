package com.example.powermanager.control.cpufreq

import com.example.powermanager.utils.AVAILABLE_SCALING_GOVERNORS_PATH
import com.example.powermanager.utils.CHANGE_SCALING_GOVERNOR_FOR_CPU_COMMAND
import com.example.powermanager.utils.CURRENT_SCALING_GOVERNOR_PATH
import com.example.powermanager.utils.readProtectedFileContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

}
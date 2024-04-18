package com.example.powermanager.control.cpu

import com.example.powermanager.utils.AVAILABLE_CPUIDLE_GOVERNORS_PATH
import com.example.powermanager.utils.CHANGE_CPUIDLE_GOVERNOR_COMMAND
import com.example.powermanager.utils.CURRENT_CPUIDLE_GOVERNOR_PATH
import com.example.powermanager.utils.LinuxCommandsUtils.readProtectedFileContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CpuIdleManager {

    fun getCurrentGovernor() : String {
        return readProtectedFileContent(CURRENT_CPUIDLE_GOVERNOR_PATH).trim()
    }

    fun getAvailableGovernors() : List<String> {
        val fileContent = readProtectedFileContent(AVAILABLE_CPUIDLE_GOVERNORS_PATH).trim()

        return fileContent.split(" ")
    }

    suspend fun changeCpuIdleGovernor(newGovernorName : String) {
        withContext(Dispatchers.IO) {
            val command = String.format(CHANGE_CPUIDLE_GOVERNOR_COMMAND, newGovernorName)

            val process = Runtime.getRuntime().exec(command)
            process.waitFor()
        }
    }

}

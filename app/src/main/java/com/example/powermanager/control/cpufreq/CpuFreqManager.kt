package com.example.powermanager.control.cpufreq

import com.example.powermanager.utils.AFFECTED_CPUS
import com.example.powermanager.utils.AVAILABLE_SCALING_GOVERNORS_PATH
import com.example.powermanager.utils.CHANGE_SCALING_GOVERNOR_FOR_CPU_COMMAND
import com.example.powermanager.utils.CPUFREQ_DIRECTORY_PATH
import com.example.powermanager.utils.CURRENT_SCALING_GOVERNOR_PATH
import com.example.powermanager.utils.NUMBER_OF_KILOHERTZ_IN_A_MEGAHERTZ
import com.example.powermanager.utils.SCALING_AVAILABLE_FREQUENCIES
import com.example.powermanager.utils.convertKHzToGHz
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
        val policyDirectories = getPolicyDirectories()

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

    fun determineAllCpuFreqPolicies() : Map<String, CpuFreqPolicy> {
        val result : MutableMap<String, CpuFreqPolicy> = mutableMapOf()

        // determine all policy directories
        val policyDirectories = getPolicyDirectories()

        policyDirectories.forEach { policyDirectory ->
            val policyName = File(policyDirectory).name
            val affectedCoresFile = File(policyDirectory, AFFECTED_CPUS)
            val availableFrequenciesFile = File(policyDirectory, SCALING_AVAILABLE_FREQUENCIES)

            val affectedCores = affectedCoresFile
                .readText()
                .trim()
                .split("\\s+".toRegex())
                .map { it.toInt() }

            val availableFrequenciesKhz = availableFrequenciesFile
                .readText()
                .trim()
                .split("\\s+".toRegex())
                .map { it.toInt() }
                .toMutableList()

            availableFrequenciesKhz.sort()

            val frequenciesMhz = if (availableFrequenciesKhz.size <= 9)
                availableFrequenciesKhz.drop(1).map { it / NUMBER_OF_KILOHERTZ_IN_A_MEGAHERTZ } else
                    availableFrequenciesKhz.filterIndexed { index, _ -> index % 2 == 1 }.map { it / NUMBER_OF_KILOHERTZ_IN_A_MEGAHERTZ }

            val maximumFrequencyGhz = convertKHzToGHz(availableFrequenciesKhz.last())

            val policy = CpuFreqPolicy(
                name = policyName,
                affectedCores = affectedCores,
                frequenciesMhz = frequenciesMhz,
                maximumFrequencyGhz = maximumFrequencyGhz
            )

            result[policyName] = policy
        }

        return result
    }

    private fun getPolicyDirectories() : List<String> {
        return Files.walk(Paths.get(CPUFREQ_DIRECTORY_PATH), 1)
            .filter { Files.isDirectory(it) }
            .map { policy ->
                policy.toString()
            }
            .toArray()
            .filter { it != CPUFREQ_DIRECTORY_PATH }
            .map { it as String }
    }

    fun getCoreToPolicyMap(existingPolicies : Map<String, CpuFreqPolicy>) : Map<Int, String> {
        val result : MutableMap<Int, String> = mutableMapOf()

        for (policyName in existingPolicies.keys) {
            for (affectedCore in existingPolicies[policyName]!!.affectedCores) {
                result[affectedCore] = policyName
            }
        }

        return result
    }

}

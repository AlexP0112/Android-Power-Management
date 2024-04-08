package com.example.powermanager.control.cpufreq

import com.example.powermanager.control.storage.CpuConfiguration
import com.example.powermanager.utils.AVAILABLE_SCALING_GOVERNORS_PATH
import com.example.powermanager.utils.CHANGE_SCALING_GOVERNOR_FOR_POLICY_COMMAND
import com.example.powermanager.utils.CHANGE_SCALING_MAX_FREQUENCY_FOR_POLICY_COMMAND
import com.example.powermanager.utils.CPUFREQ_DIRECTORY_PATH
import com.example.powermanager.utils.CURRENT_SCALING_GOVERNOR_PATH
import com.example.powermanager.utils.ConversionUtils.convertKHzToGHz
import com.example.powermanager.utils.LinuxCommandsUtils.readProtectedFileContent
import com.example.powermanager.utils.NUMBER_OF_KILOHERTZ_IN_A_MEGAHERTZ
import com.example.powermanager.utils.POLICY_MAX_FREQUENCY_PATH
import com.example.powermanager.utils.RELATED_CPUS
import com.example.powermanager.utils.SCALING_AVAILABLE_FREQUENCIES
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

object CpuFreqManager {

    fun getCurrentScalingGovernor() : String {
        return readProtectedFileContent(CURRENT_SCALING_GOVERNOR_PATH).trim()
    }

    fun getAvailableScalingGovernors() : List<String> {
        val fileContent = readProtectedFileContent(AVAILABLE_SCALING_GOVERNORS_PATH).trim()

        return fileContent.split("\\s+".toRegex())
    }

    // change scaling governor for all policies at the same time
    suspend fun changeScalingGovernor(newGovernor: String, policyNames : List<String>) {
        withContext(Dispatchers.IO) {
            for (policy in policyNames) {
                val command = String.format(CHANGE_SCALING_GOVERNOR_FOR_POLICY_COMMAND, newGovernor, policy)

                val process = Runtime.getRuntime().exec(command)
                process.waitFor()
            }
        }
    }

    fun getCurrentMaxFrequencyForPolicyKhz(policyName: String) : Int {
        val filePath = String.format(POLICY_MAX_FREQUENCY_PATH, policyName)

        return readProtectedFileContent(filePath).trim().toInt()
    }

    suspend fun changeMaxFrequencyForPolicy(policyName: String, maxFrequencyKhz: Int) {
        withContext(Dispatchers.IO) {
            val command = String.format(
                CHANGE_SCALING_MAX_FREQUENCY_FOR_POLICY_COMMAND,
                maxFrequencyKhz,
                policyName
            )

            val process = Runtime.getRuntime().exec(command)
            process.waitFor()
        }
    }

    /*
     * This function looks inside `/sys/devices/system/cpu/cpufreq/policyX/related_cpus` files
     * and chooses the first core listed there as master core
     */
    fun determineMasterCores(): List<Int> {
        val result : MutableList<Int> = mutableListOf()
        val policyDirectories = getPolicyDirectories()

        policyDirectories.forEach { directory ->
            val relatedCoresFile = File(directory, RELATED_CPUS)
            val cpus = relatedCoresFile
                .readText()
                .trim()
                .split("\\s+".toRegex())
                .map { it.toInt() }

            if (cpus.isNotEmpty())
                result.add(cpus[0])
        }

        result.sort()

        return result
    }

    /*
     * Creates a map from policy name (e.g policy0) to a CpuFreqPolicy object containing
     * information about that policy
     */
    fun determineAllCpuFreqPolicies() : Map<String, CpuFreqPolicy> {
        val result : MutableMap<String, CpuFreqPolicy> = mutableMapOf()

        // determine all policy directories
        val policyDirectories = getPolicyDirectories()

        policyDirectories.forEach { policyDirectory ->
            val policyName = File(policyDirectory).name
            val relatedCoresFile = File(policyDirectory, RELATED_CPUS)
            val availableFrequenciesFile = File(policyDirectory, SCALING_AVAILABLE_FREQUENCIES)

            val relatedCores = relatedCoresFile
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

            val frequenciesMhz = NUMBER_OF_AVAILABLE_FREQUENCIES_TO_CHOSEN_INDICES[availableFrequenciesKhz.size]!!.map {
                 availableFrequenciesKhz[it] / NUMBER_OF_KILOHERTZ_IN_A_MEGAHERTZ
            }

            val maximumFrequencyGhz = convertKHzToGHz(availableFrequenciesKhz.last())

            val policy = CpuFreqPolicy(
                name = policyName,
                relatedCores = relatedCores,
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
            for (relatedCore in existingPolicies[policyName]!!.relatedCores) {
                result[relatedCore] = policyName
            }
        }

        return result
    }

    suspend fun applyCpuConfiguration(configuration: CpuConfiguration, policyNames: List<String>, numberOfCores : Int) {
        withContext(Dispatchers.IO) {
            // first change scaling governor
            changeScalingGovernor(configuration.scalingGovernor, policyNames)

            // then set frequency limits
            configuration.policyToFrequencyLimitMHz.forEach { (policyName, limitMHz) ->
                changeMaxFrequencyForPolicy(policyName, limitMHz * NUMBER_OF_KILOHERTZ_IN_A_MEGAHERTZ)
            }

            // and then set cpus states
            for (coreIndex in 0 until numberOfCores) {
                CpuHotplugManager.changeCoreState(coreIndex, coreIndex in configuration.onlineCores)
            }
        }
    }

}

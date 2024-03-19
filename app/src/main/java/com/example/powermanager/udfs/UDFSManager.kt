package com.example.powermanager.udfs

import com.example.powermanager.control.cpufreq.CpuFreqPolicy
import com.example.powermanager.utils.UDFS_NUMBER_OF_LEVELS

object UDFSManager {

    /*
     * current frequency level when performing UDFS. It ranges from 1 to 9,
     * 1 meaning powersave governor, 9 meaning performance governor and the other levels
     * are given by the values of `CpuFreqPolicy.frequenciesMhz`. Frequencies for all
     * cores are changed at the same time
     */
    private var currentLevel : Int = 0

    // the cpufreq policies received from the view model when starting the UDFS process
    private var cpuFreqPolicies : Map<String, CpuFreqPolicy> = mapOf()

    // callbacks to the view model for sending notifications and updating the control screen UI
    private var onCurrentLevelChanged : (Int) -> Unit = {}
    private var onUDFSProcessFinished : (Int) -> Unit = {}

    fun startUDFS(
        policies : Map<String, CpuFreqPolicy>,
        onLevelChanged : (Int) -> Unit,
        onProcessFinished : (Int) -> Unit
    ) {
        cpuFreqPolicies = policies
        onCurrentLevelChanged = onLevelChanged
        onUDFSProcessFinished = onProcessFinished
        currentLevel = UDFS_NUMBER_OF_LEVELS

        onCurrentLevelChanged(currentLevel)
    }

    fun decreaseFrequency() {
        currentLevel--

        if (currentLevel == 0)
            onUDFSProcessFinished(currentLevel)
        else
            onCurrentLevelChanged(currentLevel)
    }

    // this is the end of the process
    fun increaseFrequency() {
        currentLevel++
        onUDFSProcessFinished(currentLevel)
    }

}

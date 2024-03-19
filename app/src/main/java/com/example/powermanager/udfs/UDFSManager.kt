package com.example.powermanager.udfs

import com.example.powermanager.utils.UDFS_NUMBER_OF_LEVELS

object UDFSManager {

    /*
     * current frequency level when performing UDFS. It ranges from 1 to 9,
     * 1 meaning powersave governor, 9 meaning performance governor and the other levels
     * are given by the values of `CpuFreqPolicy.frequenciesMhz`. Frequencies for all
     * cores are changed at the same time
     */
    private var currentLevel : Int = 0

    /*
     * callbacks to the view model used for changing frequency limits,
     * sending notifications and updating the control screen UI as these things cannot be
     * achieved from this singleton class. These are set when `startUDFS` is called
     */
    private var onCurrentLevelChanged : (Int) -> Unit = {}
    private var onUDFSProcessFinished : (Int) -> Unit = {}

    fun startUDFS(
        onLevelChanged : (Int) -> Unit,
        onProcessFinished : (Int) -> Unit
    ) {
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

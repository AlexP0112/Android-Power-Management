package com.example.powermanager.control.cpufreq

import com.example.powermanager.utils.CHANGE_CORE_STATE_COMMAND
import com.example.powermanager.utils.CPUINFO_PATH
import com.example.powermanager.utils.CPU_REGEX
import com.example.powermanager.utils.DEVICES_SYSTEM_CPU_PATH
import com.example.powermanager.utils.getOnlineCoresFromFileContent
import com.example.powermanager.utils.readProtectedFileContent
import java.io.File
import java.io.FileFilter
import java.util.regex.Pattern

object CpuHotplugManager {

    fun determineTotalNumberOfCPUCores(): Int {

        class CpuFilter : FileFilter {
            override fun accept(pathname: File): Boolean {
                return Pattern.matches(CPU_REGEX, pathname.name)
            }
        }

        return try {
            val dir = File(DEVICES_SYSTEM_CPU_PATH)
            val files = dir.listFiles(CpuFilter()) ?: return 1
            files.size
        } catch (e: Exception) {
            1
        }
    }

    // returns a list of active cores indices like [0, 1, 3]
    fun getOnlineCores(): List<Int> {
        val fileContent = readProtectedFileContent(CPUINFO_PATH)

        return getOnlineCoresFromFileContent(fileContent)
    }

    fun changeCoreState(coreIndex: Int, enable: Boolean) {
        val command = String.format(CHANGE_CORE_STATE_COMMAND, if (enable) 1 else 0, coreIndex)

        val process = Runtime.getRuntime().exec(command)
        process.waitFor()
    }

}
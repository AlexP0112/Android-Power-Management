package com.example.powermanager.utils

import com.example.powermanager.preferences.LoadAverageTypes
import org.junit.Test

class LinuxCommandsUtilsTest {

    private val uptimeCommandExampleOutput      = "00:01:31 up 19 days, 13:02,  0 users,  load average: 1.29, 3.42, 31.83"
    private val ifconfigWlanExampleOutput       = "wlan0     Link encap:UNSPEC\nwlan1     Link encap:UNSPEC\n"
    private val ifconfigWlanExampleOutput2      = "wlan0     Link encap:UNSPEC\nwlan1     Link encap:UNSPEC\nwlan2      Link encap:UNSPEC\n"

    @Test
    fun `load average from uptime command last minute`() {
        val expected = 1.29f
        val result = LinuxCommandsUtils.getLoadAverageFromUptimeCommandOutput(
            commandOutput = uptimeCommandExampleOutput,
            loadAverageType = LoadAverageTypes.LAST_MINUTE
        )

        assert(expected == result)
    }

    @Test
    fun `load average from uptime command last 5 minutes`() {
        val expected = 3.42f
        val result = LinuxCommandsUtils.getLoadAverageFromUptimeCommandOutput(
            commandOutput = uptimeCommandExampleOutput,
            loadAverageType = LoadAverageTypes.LAST_FIVE_MINUTES
        )

        assert(expected == result)
    }

    @Test
    fun `load average from uptime command last 15 minutes`() {
        val expected = 31.83f
        val result = LinuxCommandsUtils.getLoadAverageFromUptimeCommandOutput(
            commandOutput = uptimeCommandExampleOutput,
            loadAverageType = LoadAverageTypes.LAST_FIFTEEN_MINUTES
        )

        assert(expected == result)
    }

    @Test
    fun `wlan interfaces parsing from ifconfig command output 2 interfaces`() {
        val expected = listOf("wlan0", "wlan1")
        val result = LinuxCommandsUtils.getInterfacesFromIfConfigOutput(ifconfigWlanExampleOutput)

        assert(expected == result)
    }

    @Test
    fun `wlan interfaces parsing from ifconfig command output 3 interfaces`() {
        val expected = listOf("wlan0", "wlan1", "wlan2")
        val result = LinuxCommandsUtils.getInterfacesFromIfConfigOutput(ifconfigWlanExampleOutput2)

        assert(expected == result)
    }

    @Test
    fun `online cores from proc cpuinfo file content all cores active`() {
        val expected = listOf(0, 1, 2, 3, 4, 5, 6, 7)
        val result = LinuxCommandsUtils.getOnlineCoresFromFileContent("0-7")

        assert(expected == result)
    }

    @Test
    fun `online cores from proc cpuinfo file content some cores disabled`() {
        val expected = listOf(0, 1, 2, 4, 6)
        val result = LinuxCommandsUtils.getOnlineCoresFromFileContent("0-2,4,6")

        assert(expected == result)
    }

    @Test
    fun `online cores from proc cpuinfo file content some cores disabled 2`() {
        val expected = listOf(0, 1, 2, 4, 5, 6)
        val result = LinuxCommandsUtils.getOnlineCoresFromFileContent("0-2,4-6")

        assert(expected == result)
    }

    @Test
    fun `network stats from proc net dev`() {
        val expected = listOf(2057537163L, 187758831L)
        val result = LinuxCommandsUtils.getBytesSentAndReceivedByAllInternetInterfacesFromFileContent(ExampleFileContents.PROC_NET_DEV_FILE_CONTENT_EXAMPLE)

        assert(expected == result)
    }

}

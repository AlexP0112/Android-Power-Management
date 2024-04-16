package com.example.powermanager.utils

import org.junit.Test
import java.time.Duration

class FormattingUtilsTest {

    @Test
    fun `format null duration`() {
        val result = FormattingUtils.formatDuration(null)

        assert(result == NO_VALUE_STRING)
    }

    @Test
    fun `format duration minutes seconds`() {
        val duration = Duration.ofMinutes(1) + Duration.ofSeconds(24)
        val expected = "1min"
        val result = FormattingUtils.formatDuration(duration)

        assert(expected == result)
    }

    @Test
    fun `format duration minutes seconds millis`() {
        val duration = Duration.ofMinutes(10) + Duration.ofSeconds(16) + Duration.ofMillis(450)
        val expected = "10min"
        val result = FormattingUtils.formatDuration(duration)

        assert(expected == result)
    }

    @Test
    fun `format duration less than a minute`() {
        val duration = Duration.ofSeconds(25)
        val expected = "25s"
        val result = FormattingUtils.formatDuration(duration)

        assert(expected == result)
    }

    @Test
    fun `format duration containing only minutes`() {
        val duration = Duration.ofMinutes(2)
        val expected = "2min"
        val result = FormattingUtils.formatDuration(duration)

        assert(expected == result)
    }

    @Test
    fun `format duration with hours and minutes`() {
        val duration = Duration.ofMinutes(93) + Duration.ofMillis(159) + Duration.ofSeconds(23)
        val expected = "1h 33min"
        val result = FormattingUtils.formatDuration(duration)

        assert(expected == result)
    }

    @Test
    fun `format duration with days and minutes`() {
        val duration = Duration.ofDays(12) + Duration.ofMillis(159) + Duration.ofMinutes(56)
        val expected = "12d 56min"
        val result = FormattingUtils.formatDuration(duration)

        assert(expected == result)
    }

    @Test
    fun `format duration with days, hours and minutes`() {
        val duration = Duration.ofDays(13) + Duration.ofMillis(159) + Duration.ofMinutes(34) + Duration.ofHours(6)
        val expected = "13d 6h 34min"
        val result = FormattingUtils.formatDuration(duration)

        assert(expected == result)
    }

    @Test
    fun `format duration with days and hours`() {
        val duration = Duration.ofDays(13) + Duration.ofHours(6)
        val expected = "13d 6h"
        val result = FormattingUtils.formatDuration(duration)

        assert(expected == result)
    }

    @Test
    fun `get hour and minute from timestamp test 1`() {
        val expected = "19:48"
        val result = FormattingUtils.getHourAndMinuteFromLongTimestamp(1711903731000L)

        assert(expected == result)
    }

    @Test
    fun `get hour and minute from timestamp test 2`() {
        val expected = "14:13"
        val result = FormattingUtils.getHourAndMinuteFromLongTimestamp(1705061611000L)

        assert(expected == result)
    }

    @Test
    fun `convert size bytes`() {
        val expected = "1013B"
        val result = FormattingUtils.getPrettyStringFromNumberOfBytes(1013L)

        assert(expected == result)
    }

    @Test
    fun `convert size bytes 2`() {
        val expected = "1023B"
        val result = FormattingUtils.getPrettyStringFromNumberOfBytes(1023L)

        assert(expected == result)
    }

    @Test
    fun `convert size one kilo`() {
        val expected = "1.0KB"
        val result = FormattingUtils.getPrettyStringFromNumberOfBytes(1024L)

        assert(expected == result)
    }

    @Test
    fun `convert size kilobytes`() {
        val expected = "2.1KB"
        val result = FormattingUtils.getPrettyStringFromNumberOfBytes(2100L)

        assert(expected == result)
    }

    @Test
    fun `convert size megabytes`() {
        val expected = "5.0MB"
        val result = FormattingUtils.getPrettyStringFromNumberOfBytes(1024L * 1024 * 5)

        assert(expected == result)
    }

    @Test
    fun `convert size megabytes 2`() {
        val expected = "15.6MB"
        val result = FormattingUtils.getPrettyStringFromNumberOfBytes(16351622L)

        assert(expected == result)
    }

    @Test
    fun `convert size gigabytes`() {
        val expected = "159.5GB"
        val result = FormattingUtils.getPrettyStringFromNumberOfBytes(171263716235L)

        assert(expected == result)
    }

}

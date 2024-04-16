package com.example.powermanager.utils

import org.junit.Test

class ValidationUtilsTest {

    @Test
    fun `number of samples too low`() {
        val result = ValidationUtils.isRecordingNumberOfSamplesStringValid("3")

        assert(!result)
    }

    @Test
    fun `number of samples too high`() {
        val result = ValidationUtils.isRecordingNumberOfSamplesStringValid("300")

        assert(!result)
    }

    @Test
    fun `number of samples in between bounds test 1`() {
        val result = ValidationUtils.isRecordingNumberOfSamplesStringValid("30")

        assert(result)
    }

    @Test
    fun `number of samples in between bounds test 2`() {
        val result = ValidationUtils.isRecordingNumberOfSamplesStringValid("150")

        assert(result)
    }

    @Test
    fun `number of samples alright lower bound`() {
        val result = ValidationUtils.isRecordingNumberOfSamplesStringValid(MINIMUM_NUMBER_OF_RECORDING_SAMPLES_ALLOWED.toString())

        assert(result)
    }

    @Test
    fun `number of samples alright upper bound`() {
        val result = ValidationUtils.isRecordingNumberOfSamplesStringValid(MAXIMUM_NUMBER_OF_RECORDING_SAMPLES_ALLOWED.toString())

        assert(result)
    }

    @Test
    fun `invalid file name whitespace`() {
        val result = ValidationUtils.isFileNameValid("some name")

        assert(!result)
    }

    @Test
    fun `invalid file name special characters`() {
        val result = ValidationUtils.isFileNameValid("test!1@#")

        assert(!result)
    }

    @Test
    fun `invalid file name dot`() {
        val result = ValidationUtils.isFileNameValid("test.")

        assert(!result)
    }

    @Test
    fun `valid file name underscores`() {
        val result = ValidationUtils.isFileNameValid("some_name_1")

        assert(result)
    }

    @Test
    fun `valid file name underscores and dash`() {
        val result = ValidationUtils.isFileNameValid("some-name_1212_aa")

        assert(result)
    }
}

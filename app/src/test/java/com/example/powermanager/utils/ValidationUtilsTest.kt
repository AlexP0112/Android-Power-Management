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
    fun `number of samples alright test 1`() {
        val result = ValidationUtils.isRecordingNumberOfSamplesStringValid("30")

        assert(result)
    }

    @Test
    fun `number of samples alright test 2`() {
        val result = ValidationUtils.isRecordingNumberOfSamplesStringValid("150")

        assert(result)
    }

    @Test
    fun `invalid file name test 1`() {
        val result = ValidationUtils.isFileNameValid("some name")

        assert(!result)
    }

    @Test
    fun `invalid file name test 2`() {
        val result = ValidationUtils.isFileNameValid("test!1")

        assert(!result)
    }

    @Test
    fun `valid file name test 1`() {
        val result = ValidationUtils.isFileNameValid("some_name_1")

        assert(result)
    }

    @Test
    fun `valid file name test 2`() {
        val result = ValidationUtils.isFileNameValid("some-name_1212_aa")

        assert(result)
    }
}

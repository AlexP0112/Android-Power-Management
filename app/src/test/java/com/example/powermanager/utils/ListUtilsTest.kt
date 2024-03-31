package com.example.powermanager.utils

import org.junit.Test
import kotlin.math.abs


class ListUtilsTest {

    private val epsilon = 0.000001

    @Test
    fun `list average test 1`() {
        val input = listOf(1.3f, 4.5f, 3.8f, 10.12f)
        val expected = 4.93f

        val result = ListUtils.computeListAverage(input)
        assert(abs(expected - result) < epsilon)
    }

    @Test
    fun `list average test 2`() {
        val input = listOf(1.3f, 4.5f, 3.8f, 10.12f, 76.2f)
        val expected = 19.184f

        val result = ListUtils.computeListAverage(input)
        assert(abs(expected - result) < epsilon)
    }

    @Test
    fun `list maximum test 1`() {
        val input = listOf(1.3f, 4.5f, 3.8f, 10.12f)
        val expected = 10.12f

        val result = ListUtils.getListMaximum(input)
        assert(expected == result)
    }

    @Test
    fun `list maximum test 2`() {
        val input = listOf(1.3f, 4.5f, 89.4f, 3.8f, 10.12f)
        val expected = 89.4f

        val result = ListUtils.getListMaximum(input)
        assert(expected == result)
    }

    @Test
    fun `list minimum test 1`() {
        val input = listOf(1.3f, 3.8f, 1.08f, 10.12f)
        val expected = 1.08f

        val result = ListUtils.getListMinimum(input)
        assert(expected == result)
    }

    @Test
    fun `list minimum test 2`() {
        val input = listOf(5.3f, 4.5f, 89.4f, 3.8f, 10.12f)
        val expected = 3.8f

        val result = ListUtils.getListMinimum(input)
        assert(expected == result)
    }
}

package com.example.powermanager.utils

import org.junit.Test
import kotlin.math.abs

class ListUtilsTest {

    private val epsilon = 0.000001

    @Test
    fun `list average even length`() {
        val input = listOf(1.3f, 4.5f, 3.8f, 10.12f)
        val expected = 4.93f

        val result = ListUtils.computeListAverage(input)
        assert(abs(expected - result) < epsilon)
    }

    @Test
    fun `list average odd length`() {
        val input = listOf(1.3f, 4.5f, 3.8f, 10.12f, 76.2f)
        val expected = 19.184f

        val result = ListUtils.computeListAverage(input)
        assert(abs(expected - result) < epsilon)
    }

    @Test
    fun `list average empty list`() {
        val expected = 0f

        val result = ListUtils.computeListAverage(listOf())
        assert(result == expected)
    }

    @Test
    fun `list maximum even length`() {
        val input = listOf(1.3f, 4.5f, 3.8f, 10.12f)
        val expected = 10.12f

        val result = ListUtils.getListMaximum(input)
        assert(expected == result)
    }

    @Test
    fun `list maximum odd length`() {
        val input = listOf(1.3f, 4.5f, 89.4f, 3.8f, 10.12f)
        val expected = 89.4f

        val result = ListUtils.getListMaximum(input)
        assert(expected == result)
    }

    @Test
    fun `list maximum empty list`() {
        val expected = 0f

        val result = ListUtils.getListMaximum(listOf())
        assert(result == expected)
    }

    @Test
    fun `list minimum even length`() {
        val input = listOf(1.3f, 3.8f, 1.08f, 10.12f)
        val expected = 1.08f

        val result = ListUtils.getListMinimum(input)
        assert(expected == result)
    }

    @Test
    fun `list minimum odd length`() {
        val input = listOf(5.3f, 4.5f, 89.4f, 3.8f, 10.12f)
        val expected = 3.8f

        val result = ListUtils.getListMinimum(input)
        assert(expected == result)
    }

    @Test
    fun `list minimum empty list`() {
        val expected = 0f

        val result = ListUtils.getListMinimum(listOf())
        assert(result == expected)
    }
}

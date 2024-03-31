package com.example.powermanager.utils

object ListUtils {

    fun computeListAverage(list : List<Float>) : Float {
        var sum = 0f

        list.forEach {
            sum += it
        }

        return sum / list.size
    }

    fun getListMaximum(list : List<Float>) : Float {
        if (list.isEmpty())
            return 0f

        var max = list[0]

        list.forEach {
            if (it > max)
                max = it
        }

        return max
    }

    fun getListMinimum(list : List<Float>) : Float {
        if (list.isEmpty())
            return 0f

        var min = list[0]

        list.forEach {
            if (it < min)
                min = it
        }

        return min
    }

}

package com.example.powermanager.ui.charts.common

import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.entry.ChartEntryModel

class CustomAxisValuesOverrider(
    minYValue : Float,
    maxYValue: Float
) : AxisValuesOverrider<ChartEntryModel> {

    private var maxY: Float
    private var minY : Float

    init {
        maxY = maxYValue
        minY = minYValue
    }

    override fun getMaxX(model: ChartEntryModel): Float {
        return model.maxX
    }

    override fun getMaxY(model: ChartEntryModel): Float {
        return maxY
    }

    override fun getMinX(model: ChartEntryModel): Float {
        return model.minX
    }

    override fun getMinY(model: ChartEntryModel): Float {
        return minY
    }
}

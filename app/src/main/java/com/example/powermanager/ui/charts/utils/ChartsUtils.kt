package com.example.powermanager.ui.charts.utils

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.core.DefaultAlpha
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders

fun getLineSpecsFromColors(
    lineColors : List<Color>
): List<LineChart.LineSpec> {
    return lineColors.map { lineColor ->
        LineChart.LineSpec(
            lineColor = lineColor.toArgb(),
            lineBackgroundShader = DynamicShaders.fromBrush(
                // vertical color gradient under the chart lines
                brush = Brush.verticalGradient(
                    listOf(
                        lineColor.copy(DefaultAlpha.LINE_BACKGROUND_SHADER_START),
                        lineColor.copy(DefaultAlpha.LINE_BACKGROUND_SHADER_END)
                    )
                )
            )
        )
    }
}

package com.example.financialapp.report

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp

@Composable
fun CircularProgressBar(
    progress: Float,
    max: Float,
    color: Color,
    backgroundColor: Color,
    stroke: Dp,
    modifier: Modifier = Modifier
){
    Canvas(modifier=modifier) {
        drawArc(
            color = backgroundColor,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = stroke.toPx(), cap = StrokeCap.Round)
        )
        drawArc(
            color = backgroundColor,
            startAngle = -90f,
            sweepAngle = (progress/max)*360f,
            useCenter = false,
            style = Stroke(width = stroke.toPx(), cap = StrokeCap.Round)
        )
    }
}



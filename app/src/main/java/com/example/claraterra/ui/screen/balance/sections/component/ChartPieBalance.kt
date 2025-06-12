package com.example.claraterra.ui.screen.balance.sections.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.example.claraterra.ui.theme.TerraError
import com.example.claraterra.ui.theme.TerraPrimary
import com.example.claraterra.ui.theme.TerraSecondary
import com.example.claraterra.ui.theme.TerraTertiary
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.abs // Necesitarás importar abs

@Composable
fun ChartPie(
    modifier: Modifier = Modifier, // Modificador para el composable ChartPie completo
    earnings: Float, // Tus ganancias
    expenses: Float // Tus gastos
) {
    val profit = earnings - expenses

    // Definimos colores específicos para cada tipo de segmento
    val earningsColor = Color(TerraPrimary.value) // Azul para Ganancias
    val expensesColor = Color(TerraSecondary.value) // Rojo para Gastos
    val profitColor = Color(TerraTertiary.value)  // Verde para Utilidad
    val lossColor = Color(TerraError.value)    // Naranja oscuro para Pérdida

    val slicesToDraw = mutableListOf<Triple<String, Float, Color>>() // (etiqueta, valor, color)
    var totalForChart = 0f // El valor total que representará el 100% del pastel

    // Agrega los segmentos de Ganancias y Gastos primero
    if (earnings > 0f) {
        slicesToDraw.add(Triple("Ganancias", earnings, earningsColor))
    }
    if (expenses > 0f) {
        slicesToDraw.add(Triple("Gastos", expenses, expensesColor))
    }

    // Agrega el segmento de Utilidad o Pérdida
    if (profit >= 0) {
        if (profit > 0f) { // Solo si hay utilidad positiva
            slicesToDraw.add(Triple("Utilidad", profit, profitColor))
        }
    } else {
        // Si hay pérdida, añadimos el valor absoluto como segmento de "Pérdida"
        slicesToDraw.add(Triple("Pérdida", abs(profit), lossColor))
    }

    // Calcula el total del gráfico sumando todos los valores que se van a dibujar
    totalForChart = slicesToDraw.sumOf { it.second.toDouble() }.toFloat()

    Box(modifier = modifier) { // Aplica el modificador pasado al Box principal
        if (totalForChart <= 0f) {
            // Muestra un mensaje si no hay datos válidos para dibujar el gráfico
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay datos para mostrar el gráfico")
            }
        } else {
            Canvas(modifier = Modifier.fillMaxSize()) { // El Canvas llenará el Box
                val centerX = size.width / 2
                val centerY = size.height / 2
                val radius = minOf(centerX, centerY) * 0.8f // 80% del radio para dejar espacio para etiquetas

                var currentAngle = -90f // Comienza desde la parte superior

                slicesToDraw.forEach { (label, value, color) ->
                    val slicePercentage = value / totalForChart
                    val sweepAngle = slicePercentage * 360f

                    // Dibuja el arco (segmento del pastel)
                    drawArc(
                        color = color,
                        startAngle = currentAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        topLeft = androidx.compose.ui.geometry.Offset(centerX - radius, centerY - radius),
                        size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                        style = Fill
                    )

                    // Calcula las coordenadas para el texto del porcentaje
                    val midAngle = currentAngle + sweepAngle / 2f
                    val textRadius = radius * 0.65f // Posición del texto del porcentaje
                    val textX = centerX + textRadius * cos(Math.toRadians(midAngle.toDouble())).toFloat()
                    val textY = centerY + textRadius * sin(Math.toRadians(midAngle.toDouble())).toFloat()

                    // Dibuja el texto del porcentaje
                    val percentageValue = (slicePercentage * 100).toInt()
                    val percentageText = "$percentageValue%"
                    val textPaint = Paint().asFrameworkPaint().apply {
                        this.color = Color.Black.toArgb()
                        textSize = 40f
                        isFakeBoldText = true
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                    drawContext.canvas.nativeCanvas.drawText(percentageText, textX, textY + textPaint.textSize / 3, textPaint)

                    // Calcula las coordenadas para la etiqueta (ej. "Gastos", "Utilidad")
                    val labelRadius = radius * 1.1f // Posición de la etiqueta
                    val labelX = centerX + labelRadius * cos(Math.toRadians(midAngle.toDouble())).toFloat()
                    val labelY = centerY + labelRadius * sin(Math.toRadians(midAngle.toDouble())).toFloat()

                    val labelPaint = Paint().asFrameworkPaint().apply {
                        this.color = Color.Black.toArgb()//Color de texto
                        textSize = 28f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                    drawContext.canvas.nativeCanvas.drawText(label, labelX, labelY + labelPaint.textSize / 3, labelPaint)

                    currentAngle += sweepAngle // Actualiza el ángulo para el siguiente segmento
                }
            }
        }
    }
}
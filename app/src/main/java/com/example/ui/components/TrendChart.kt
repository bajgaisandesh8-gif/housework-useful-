package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.DailyBreakdown
import com.example.domain.FinancialCalculator
import com.example.ui.theme.TrisaktiCardBg
import com.example.ui.theme.TrisaktiCardBorder
import com.example.ui.theme.TrisaktiCoral
import com.example.ui.theme.TrisaktiEmerald
import com.example.ui.theme.TrisaktiGold
import com.example.ui.theme.TrisaktiTextMuted
import com.example.ui.theme.TrisaktiTextPrimary
import com.example.ui.theme.TrisaktiTextSecondary

@Composable
fun TrendChart(
    breakdowns: List<DailyBreakdown>,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(TrisaktiCardBg)
            .border(1.dp, TrisaktiCardBorder, cardShape)
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WEEKLY BUSINESS TREND",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TrisaktiTextPrimary,
                    letterSpacing = 0.5.sp
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    LegendItem(color = TrisaktiGold, label = "Sales")
                    Spacer(modifier = Modifier.width(10.dp))
                    LegendItem(color = TrisaktiEmerald, label = "Profit")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (breakdowns.isEmpty() || breakdowns.all { it.sales == 0.0 }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No sales recorded for this period",
                        fontSize = 13.sp,
                        color = TrisaktiTextMuted
                    )
                }
            } else {
                val maxVal = (breakdowns.maxOfOrNull { maxOf(it.sales, it.profit) } ?: 1.0).coerceAtLeast(1000.0)

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                ) {
                    val width = size.width
                    val height = size.height
                    val barSpacing = width / breakdowns.size
                    val barWidth = (barSpacing * 0.3f).coerceIn(8f, 22f)

                    // Baseline guide
                    drawLine(
                        color = Color(0xFF334155),
                        start = Offset(0f, height - 20f),
                        end = Offset(width, height - 20f),
                        strokeWidth = 1.5f
                    )

                    breakdowns.forEachIndexed { index, item ->
                        val centerX = (index * barSpacing) + (barSpacing / 2)
                        val chartHeight = height - 30f

                        // Sales Bar
                        val salesHeight = ((item.sales / maxVal) * chartHeight).toFloat().coerceAtLeast(4f)
                        val salesTop = (height - 20f) - salesHeight
                        drawRoundRect(
                            color = TrisaktiGold,
                            topLeft = Offset(centerX - barWidth - 2f, salesTop),
                            size = Size(barWidth, salesHeight),
                            cornerRadius = CornerRadius(4f, 4f)
                        )

                        // Profit Bar
                        val profitHeight = ((item.profit.coerceAtLeast(0.0) / maxVal) * chartHeight).toFloat().coerceAtLeast(4f)
                        val profitTop = (height - 20f) - profitHeight
                        drawRoundRect(
                            color = TrisaktiEmerald,
                            topLeft = Offset(centerX + 2f, profitTop),
                            size = Size(barWidth, profitHeight),
                            cornerRadius = CornerRadius(4f, 4f)
                        )
                    }
                }

                // Day labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    breakdowns.forEach { item ->
                        Text(
                            text = item.dayName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TrisaktiTextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = TrisaktiTextSecondary,
            fontWeight = FontWeight.Medium
        )
    }
}

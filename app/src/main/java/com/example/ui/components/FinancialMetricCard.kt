package com.example.ui.components

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.FinancialCalculator
import com.example.ui.theme.TrisaktiCardBg
import com.example.ui.theme.TrisaktiCardBorder
import com.example.ui.theme.TrisaktiEmerald
import com.example.ui.theme.TrisaktiEmeraldDark
import com.example.ui.theme.TrisaktiTextMuted
import com.example.ui.theme.TrisaktiTextPrimary
import com.example.ui.theme.TrisaktiTextSecondary

@Composable
fun FinancialMetricCard(
    title: String,
    amount: Double,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    isProminent: Boolean = false,
    testTag: String = ""
) {
    val cardShape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .testTag(testTag)
            .shadow(
                elevation = if (isProminent) 8.dp else 2.dp,
                shape = cardShape,
                spotColor = if (isProminent) accentColor.copy(alpha = 0.4f) else Color.Transparent
            )
            .clip(cardShape)
            .background(
                brush = if (isProminent) {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF064E3B),
                            Color(0xFF0F291E),
                            TrisaktiCardBg
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(
                            TrisaktiCardBg,
                            Color(0xFF161F2E)
                        )
                    )
                }
            )
            .border(
                width = if (isProminent) 1.5.dp else 1.dp,
                color = if (isProminent) accentColor.copy(alpha = 0.8f) else TrisaktiCardBorder,
                shape = cardShape
            )
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isProminent) accentColor else TrisaktiTextSecondary,
                    letterSpacing = 1.sp
                )

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f))
                        .border(1.dp, accentColor.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Large visible number
            Text(
                text = FinancialCalculator.formatNpr(amount),
                fontSize = if (isProminent) 28.sp else 22.sp,
                fontWeight = FontWeight.Black,
                color = if (isProminent) Color.White else TrisaktiTextPrimary,
                letterSpacing = (-0.5).sp
            )

            if (subtitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isProminent) TrisaktiEmerald.copy(alpha = 0.9f) else TrisaktiTextMuted
                )
            }
        }
    }
}

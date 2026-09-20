package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.FinancialCalculator
import com.example.ui.theme.TrisaktiEmerald
import com.example.ui.theme.TrisaktiGold
import com.example.ui.theme.TrisaktiSurface
import com.example.ui.theme.TrisaktiTextMuted
import com.example.ui.theme.TrisaktiTextPrimary
import com.example.ui.theme.TrisaktiTextSecondary

@Composable
fun TrisaktiTopBar(
    subtitle: String = "Today's Business",
    showDemoBadge: Boolean = true,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        TrisaktiSurface,
                        Color(0xFF0D1322)
                    )
                )
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Brand & Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, TrisaktiGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.trisakti_icon),
                        contentDescription = "Trisakti Traders Brand Logo",
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "TRISAKTI TRADERS",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = TrisaktiTextPrimary,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = subtitle,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TrisaktiGold
                    )
                }
            }

            // Right: Date & Demo indicator
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = FinancialCalculator.formatDisplayDate(FinancialCalculator.todayDateString()),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TrisaktiTextSecondary
                )
                if (showDemoBadge) {
                    Box(
                        modifier = Modifier
                            .padding(top = 3.dp)
                            .background(
                                color = TrisaktiEmerald.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .border(
                                width = 0.8.dp,
                                color = TrisaktiEmerald.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "DEMO DATA",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = TrisaktiEmerald,
                            letterSpacing = 0.8.sp
                        )
                    }
                }
            }
        }
    }
}

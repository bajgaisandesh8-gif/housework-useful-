package com.example.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Subtle floating decorative background words:
 * SALES, PROFIT, CREDIT, EXPENSE, GROWTH, RECORDS
 * Rendered behind the screen content with very low opacity so it feels
 * intentional and premium without obstructing readability or interactions.
 */
@Composable
fun BackgroundWatermark(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .alpha(0.035f)
    ) {
        // Upper right: SALES
        Text(
            text = "SALES",
            fontSize = 42.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            color = Color.White,
            letterSpacing = 6.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 20.dp, y = 40.dp)
                .rotate(12f)
        )

        // Mid left: PROFIT
        Text(
            text = "PROFIT",
            fontSize = 54.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            color = Color.White,
            letterSpacing = 8.sp,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = (-30).dp, y = (-80).dp)
                .rotate(-15f)
        )

        // Mid right: CREDIT
        Text(
            text = "CREDIT",
            fontSize = 46.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            color = Color.White,
            letterSpacing = 6.sp,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 10.dp, y = 80.dp)
                .rotate(8f)
        )

        // Bottom left: EXPENSE
        Text(
            text = "EXPENSE",
            fontSize = 48.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            color = Color.White,
            letterSpacing = 6.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-10).dp, y = (-120).dp)
                .rotate(-10f)
        )

        // Bottom right: GROWTH
        Text(
            text = "GROWTH",
            fontSize = 40.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            color = Color.White,
            letterSpacing = 5.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 10.dp, y = (-40).dp)
                .rotate(6f)
        )
    }
}

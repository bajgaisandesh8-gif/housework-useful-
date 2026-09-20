package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import android.content.Intent
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.DailyClosingEntity
import com.example.domain.FinancialCalculator
import com.example.domain.TimePeriod
import com.example.ui.components.BackgroundWatermark
import com.example.ui.components.FinancialMetricCard
import com.example.ui.components.TrendChart
import com.example.ui.components.TrisaktiTopBar
import com.example.ui.theme.TrisaktiBackground
import com.example.ui.theme.TrisaktiCardBg
import com.example.ui.theme.TrisaktiCardBorder
import com.example.ui.theme.TrisaktiCoral
import com.example.ui.theme.TrisaktiEmerald
import com.example.ui.theme.TrisaktiGold
import com.example.ui.theme.TrisaktiSky
import com.example.ui.theme.TrisaktiSurface
import com.example.ui.theme.TrisaktiTextMuted
import com.example.ui.theme.TrisaktiTextPrimary
import com.example.ui.theme.TrisaktiTextSecondary
import com.example.ui.viewmodel.TrisaktiViewModel

@Composable
fun ReportsScreen(
    viewModel: TrisaktiViewModel,
    modifier: Modifier = Modifier
) {
    val reportPeriod by viewModel.reportPeriod.collectAsStateWithLifecycle()
    val summary by viewModel.reportSummary.collectAsStateWithLifecycle()
    val weeklyBreakdown by viewModel.weeklyBreakdown.collectAsStateWithLifecycle()
    val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    val allClosings by viewModel.allClosings.collectAsStateWithLifecycle()

    var closingToDelete by remember { mutableStateOf<DailyClosingEntity?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TrisaktiBackground)
    ) {
        BackgroundWatermark()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("reports_screen"),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 100.dp)
        ) {
            item {
                TrisaktiTopBar(subtitle = "Business Reports & Financial Insights")
            }

            // Period Selector Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TimePeriod.values().forEach { period ->
                        val isSelected = reportPeriod == period
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) TrisaktiEmerald else TrisaktiCardBg)
                                .border(
                                    1.dp,
                                    if (isSelected) TrisaktiEmerald else TrisaktiCardBorder,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { viewModel.setReportPeriod(period) }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = period.displayName,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.Black else TrisaktiTextSecondary
                            )
                        }
                    }
                }
            }

            // Profit Banner for Selected Period
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    FinancialMetricCard(
                        title = "${reportPeriod.displayName} Estimated Profit",
                        amount = summary.estimatedProfit,
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        accentColor = TrisaktiEmerald,
                        subtitle = "Net Margin: ${FinancialCalculator.calculateProfitMargin(summary.totalSales, summary.estimatedProfit)}%",
                        isProminent = true,
                        testTag = "reports_profit_card",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Share or Export Summary Button
            item {
                val context = LocalContext.current
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    Button(
                        onClick = {
                            val shareReportText = """
*TRISAKTI TRADERS - Financial Report*
Period: ${reportPeriod.displayName}
Generated: ${FinancialCalculator.todayDateString()}
━━━━━━━━━━━━━━━━━━━━━━
• Total Sales: ${FinancialCalculator.formatNpr(summary.totalSales)}
  - Cash Sales: ${FinancialCalculator.formatNpr(summary.cashSales)}
  - Credit Sales: ${FinancialCalculator.formatNpr(summary.creditSales)}
• Product Cost (COGS): ${FinancialCalculator.formatNpr(summary.totalProductCost)}
• Operating Expenses: ${FinancialCalculator.formatNpr(summary.totalExpenses)}
━━━━━━━━━━━━━━━━━━━━━━
★ ESTIMATED NET PROFIT: ${FinancialCalculator.formatNpr(summary.estimatedProfit)}
  (Margin: ${FinancialCalculator.calculateProfitMargin(summary.totalSales, summary.estimatedProfit)}%)
━━━━━━━━━━━━━━━━━━━━━━
• Credit (Udhaar) Given: ${FinancialCalculator.formatNpr(summary.creditGiven)}
• Credit Collected: ${FinancialCalculator.formatNpr(summary.creditCollected)}
• Total Outstanding Udhaar: ${FinancialCalculator.formatNpr(summary.outstandingCredit)}
━━━━━━━━━━━━━━━━━━━━━━
Trisakti Traders • Digital Store Management
                            """.trimIndent()

                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareReportText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Business Summary"))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("btn_share_report"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TrisaktiCardBg,
                            contentColor = TrisaktiEmerald
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TrisaktiEmerald.copy(alpha = 0.6f))
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Export / Share Summary (WhatsApp / SMS)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Overview Breakdown Grid
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FinancialMetricCard(
                            title = "Total Sales",
                            amount = summary.totalSales,
                            icon = Icons.Default.Assessment,
                            accentColor = TrisaktiGold,
                            subtitle = "Revenue",
                            modifier = Modifier.weight(1f)
                        )
                        FinancialMetricCard(
                            title = "Product Cost",
                            amount = summary.totalProductCost,
                            icon = Icons.Default.Info,
                            accentColor = Color(0xFF94A3B8),
                            subtitle = "COGS",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FinancialMetricCard(
                            title = "Operating Exp.",
                            amount = summary.totalExpenses,
                            icon = Icons.Default.Assessment,
                            accentColor = TrisaktiCoral,
                            subtitle = "Overheads",
                            modifier = Modifier.weight(1f)
                        )
                        FinancialMetricCard(
                            title = "Credit Collected",
                            amount = summary.creditCollected,
                            icon = Icons.Default.CheckCircle,
                            accentColor = TrisaktiEmerald,
                            subtitle = "Udhaar recovered",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FinancialMetricCard(
                            title = "Cash vs Credit Sales",
                            amount = summary.cashSales,
                            icon = Icons.Default.Assessment,
                            accentColor = TrisaktiGold,
                            subtitle = "Credit: ${FinancialCalculator.formatNpr(summary.creditSales)}",
                            modifier = Modifier.weight(1f)
                        )
                        FinancialMetricCard(
                            title = "Credit Given",
                            amount = summary.creditGiven,
                            icon = Icons.Default.People,
                            accentColor = TrisaktiSky,
                            subtitle = "Udhaar given",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Weekly Trend Chart
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    TrendChart(breakdowns = weeklyBreakdown)
                }
            }

            // Credit Health Assessment Card
            item {
                Spacer(modifier = Modifier.height(14.dp))
                val creditGiven = summary.creditGiven
                val creditCollected = summary.creditCollected
                val isHealthy = creditCollected >= creditGiven || creditGiven == 0.0

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(TrisaktiCardBg)
                        .border(
                            1.dp,
                            if (isHealthy) TrisaktiEmerald.copy(alpha = 0.5f) else TrisaktiGold.copy(alpha = 0.5f),
                            RoundedCornerShape(16.dp)
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
                                text = "CREDIT HEALTH CHECK",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isHealthy) TrisaktiEmerald else TrisaktiGold,
                                letterSpacing = 0.8.sp
                            )
                            Icon(
                                imageVector = if (isHealthy) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (isHealthy) TrisaktiEmerald else TrisaktiGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isHealthy)
                                "Credit collections are pacing well compared to credit extended."
                            else
                                "Credit given (${FinancialCalculator.formatNpr(creditGiven)}) exceeds credit collected (${FinancialCalculator.formatNpr(creditCollected)}). Keep follow-ups active.",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TrisaktiTextPrimary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Credit Extended: ${FinancialCalculator.formatNpr(creditGiven)}",
                                fontSize = 11.sp,
                                color = TrisaktiGold
                            )
                            Text(
                                text = "Credit Collected: ${FinancialCalculator.formatNpr(creditCollected)}",
                                fontSize = 11.sp,
                                color = TrisaktiEmerald
                            )
                        }
                    }
                }
            }

            // Expense Breakdown by Category
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "EXPENSE BREAKDOWN BY CATEGORY",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = TrisaktiTextSecondary,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val categoryTotals = allExpenses.groupBy { it.category }
                        .mapValues { (_, list) -> list.sumOf { it.amount } }
                    val totalAllExpenses = categoryTotals.values.sum().coerceAtLeast(1.0)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(TrisaktiCardBg)
                            .border(1.dp, TrisaktiCardBorder, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            categoryTotals.entries.sortedByDescending { it.value }.forEach { (cat, amount) ->
                                val pct = ((amount / totalAllExpenses) * 100).toInt()
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = cat,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TrisaktiTextPrimary
                                        )
                                        Text(
                                            text = "${FinancialCalculator.formatNpr(amount)} ($pct%)",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TrisaktiCoral
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = { (amount / totalAllExpenses).toFloat() },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = TrisaktiCoral,
                                        trackColor = TrisaktiBackground
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Daily Closing History Section
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "DAILY CLOSING DRAWER HISTORY (${allClosings.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = TrisaktiTextSecondary,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            if (allClosings.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(TrisaktiCardBg)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No daily closing records yet. Use 'Daily Cash Closing' from Dashboard.",
                            fontSize = 12.sp,
                            color = TrisaktiTextMuted
                        )
                    }
                }
            } else {
                items(allClosings, key = { it.id }) { closing ->
                    DailyClosingHistoryCard(
                        closing = closing,
                        onDelete = { closingToDelete = closing },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // Delete Closing Dialog
        if (closingToDelete != null) {
            AlertDialog(
                onDismissRequest = { closingToDelete = null },
                containerColor = TrisaktiSurface,
                title = { Text("Delete Closing Record?", color = TrisaktiTextPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "Are you sure you want to delete daily closing for ${closingToDelete!!.date}?",
                        color = TrisaktiTextSecondary
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteClosing(closingToDelete!!)
                            closingToDelete = null
                        }
                    ) {
                        Text("Delete", color = TrisaktiCoral, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { closingToDelete = null }) {
                        Text("Cancel", color = TrisaktiTextPrimary)
                    }
                }
            )
        }
    }
}

@Composable
private fun DailyClosingHistoryCard(
    closing: DailyClosingEntity,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val diff = closing.difference
    val isBalanced = diff == 0.0

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(TrisaktiCardBg)
            .border(1.dp, TrisaktiCardBorder, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LockClock,
                        contentDescription = null,
                        tint = TrisaktiSky,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = FinancialCalculator.formatDisplayDate(closing.date),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TrisaktiTextPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isBalanced) TrisaktiEmerald.copy(alpha = 0.2f)
                                else if (diff > 0) TrisaktiGold.copy(alpha = 0.2f)
                                else TrisaktiCoral.copy(alpha = 0.2f)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isBalanced) "BALANCED" else if (diff > 0) "+SURPLUS" else "SHORTFALL",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isBalanced) TrisaktiEmerald else if (diff > 0) TrisaktiGold else TrisaktiCoral
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = TrisaktiTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Expected: ${FinancialCalculator.formatNpr(closing.expectedCash)}",
                        fontSize = 12.sp,
                        color = TrisaktiTextSecondary
                    )
                    Text(
                        text = "Actual: ${FinancialCalculator.formatNpr(closing.actualCash)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TrisaktiTextPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Difference",
                        fontSize = 10.sp,
                        color = TrisaktiTextSecondary
                    )
                    Text(
                        text = (if (diff >= 0) "+" else "") + FinancialCalculator.formatNpr(diff),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isBalanced) TrisaktiEmerald else if (diff > 0) TrisaktiGold else TrisaktiCoral
                    )
                }
            }

            if (closing.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Note: ${closing.notes}",
                    fontSize = 11.sp,
                    color = TrisaktiTextMuted
                )
            }
        }
    }
}

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.FinancialCalculator
import com.example.domain.FinancialSummary
import com.example.ui.components.BackgroundWatermark
import com.example.ui.components.FinancialMetricCard
import com.example.ui.components.TrisaktiTopBar
import com.example.ui.theme.TrisaktiBackground
import com.example.ui.theme.TrisaktiCardBg
import com.example.ui.theme.TrisaktiCardBorder
import com.example.ui.theme.TrisaktiCoral
import com.example.ui.theme.TrisaktiEmerald
import com.example.ui.theme.TrisaktiGold
import com.example.ui.theme.TrisaktiSky
import com.example.ui.theme.TrisaktiTextMuted
import com.example.ui.theme.TrisaktiTextPrimary
import com.example.ui.theme.TrisaktiTextSecondary
import com.example.ui.viewmodel.TrisaktiViewModel

@Composable
fun DashboardScreen(
    viewModel: TrisaktiViewModel,
    onOpenAddSale: () -> Unit,
    onOpenAddExpense: () -> Unit,
    onOpenAddCredit: () -> Unit,
    onOpenRecordPayment: () -> Unit,
    onOpenDailyClosing: () -> Unit,
    onNavigateToRecords: () -> Unit,
    onNavigateToCredit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val todaySummary by viewModel.todaySummary.collectAsStateWithLifecycle()
    val allSales by viewModel.allSales.collectAsStateWithLifecycle()
    val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val customers by viewModel.allCustomers.collectAsStateWithLifecycle()
    val customerBalances by viewModel.customerBalances.collectAsStateWithLifecycle()
    val showWelcomeBanner by viewModel.showWelcomeBanner.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TrisaktiBackground)
    ) {
        // Decorative floating background words (SALES, PROFIT, CREDIT, etc.)
        BackgroundWatermark()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("dashboard_screen"),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 96.dp)
        ) {
            // Brand Top Bar
            item {
                TrisaktiTopBar(
                    subtitle = "Today • ${FinancialCalculator.todayDateString()}",
                    showDemoBadge = false
                )
            }

            // Welcome / Intro Banner (One-time dismissable banner for parents)
            if (showWelcomeBanner) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(TrisaktiGold.copy(alpha = 0.12f))
                            .border(1.2.dp, TrisaktiGold.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Welcome to Trisakti Traders 👋",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TrisaktiGold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Your business dashboard is now ready. Record daily sales, track product costs, manage customer udhaar, and see your real profit.",
                                    fontSize = 12.sp,
                                    color = TrisaktiTextPrimary,
                                    lineHeight = 16.sp
                                )
                            }

                            IconButton(
                                onClick = { viewModel.dismissWelcomeBanner() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = TrisaktiTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Prominent Estimated Profit Card (Visually prominent as requested)
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    FinancialMetricCard(
                        title = "Today's Estimated Profit",
                        amount = todaySummary.estimatedProfit,
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        accentColor = TrisaktiEmerald,
                        subtitle = "Formula: Sales - Product Cost - Operating Expenses",
                        isProminent = true,
                        testTag = "dashboard_profit_card",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Core Financial Cards Grid (Sales, Product Cost, Expenses, Outstanding Credit)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Row 1: Total Sales & Product Cost
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FinancialMetricCard(
                            title = "Total Sales",
                            amount = todaySummary.totalSales,
                            icon = Icons.Default.ShoppingCart,
                            accentColor = TrisaktiGold,
                            subtitle = "Cash: ${FinancialCalculator.formatNpr(todaySummary.cashSales)}",
                            modifier = Modifier.weight(1f),
                            testTag = "dashboard_sales_card"
                        )
                        FinancialMetricCard(
                            title = "Product Cost",
                            amount = todaySummary.totalProductCost,
                            icon = Icons.Default.Inventory,
                            accentColor = Color(0xFF94A3B8),
                            subtitle = "Cost of Goods Sold",
                            modifier = Modifier.weight(1f),
                            testTag = "dashboard_cogs_card"
                        )
                    }

                    // Row 2: Total Expenses & Credit Collected
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FinancialMetricCard(
                            title = "Total Expenses",
                            amount = todaySummary.totalExpenses,
                            icon = Icons.Default.AccountBalanceWallet,
                            accentColor = TrisaktiCoral,
                            subtitle = "Operating expenses",
                            modifier = Modifier.weight(1f),
                            testTag = "dashboard_expenses_card"
                        )
                        FinancialMetricCard(
                            title = "Credit Collected",
                            amount = todaySummary.creditCollected,
                            icon = Icons.Default.CheckCircle,
                            accentColor = TrisaktiEmerald,
                            subtitle = "Collected today",
                            modifier = Modifier.weight(1f),
                            testTag = "dashboard_credit_collected_card"
                        )
                    }

                    // Row 3: Total Credit Outstanding
                    FinancialMetricCard(
                        title = "Total Credit Outstanding (Udhaar)",
                        amount = todaySummary.outstandingCredit,
                        icon = Icons.Default.People,
                        accentColor = TrisaktiGold,
                        subtitle = "${customers.size} credit customers on ledger",
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "dashboard_credit_outstanding_card"
                    )
                }
            }

            // Quick Actions Section (Large touch-friendly buttons for parents)
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "QUICK ACTIONS",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = TrisaktiTextSecondary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickActionItem(
                            label = "+ Add Sale",
                            icon = Icons.Default.Add,
                            color = TrisaktiGold,
                            onClick = onOpenAddSale,
                            testTag = "quick_add_sale_button",
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionItem(
                            label = "+ Add Expense",
                            icon = Icons.Default.AccountBalanceWallet,
                            color = TrisaktiCoral,
                            onClick = onOpenAddExpense,
                            testTag = "quick_add_expense_button",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickActionItem(
                            label = "+ Add Credit",
                            icon = Icons.Default.People,
                            color = TrisaktiSky,
                            onClick = onOpenAddCredit,
                            testTag = "quick_add_credit_button",
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionItem(
                            label = "Record Payment",
                            icon = Icons.Default.MonetizationOn,
                            color = TrisaktiEmerald,
                            onClick = onOpenRecordPayment,
                            testTag = "quick_record_payment_button",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Daily Closing Button (Prominent banner button)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0284C7).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                            .clickable { onOpenDailyClosing() }
                            .testTag("quick_daily_closing_button")
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LockClock,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "DAILY CASH CLOSING & DRAWER BALANCE",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8),
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }

            // Important Alerts Section
            item {
                Spacer(modifier = Modifier.height(20.dp))
                val highestDebtor = customers.maxByOrNull { customerBalances.getOrDefault(it.id, 0.0) }
                val highestDue = highestDebtor?.let { customerBalances.getOrDefault(it.id, 0.0) } ?: 0.0

                if (highestDue > 5000.0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(TrisaktiGold.copy(alpha = 0.1f))
                            .border(1.dp, TrisaktiGold.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = TrisaktiGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Credit Alert: ${highestDebtor?.name}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TrisaktiGold
                                )
                                Text(
                                    text = "Outstanding balance is ${FinancialCalculator.formatNpr(highestDue)}. Consider following up for repayment.",
                                    fontSize = 11.sp,
                                    color = TrisaktiTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Recent Transactions Activity
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "RECENT ACTIVITY",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = TrisaktiTextSecondary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "View All Records →",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TrisaktiGold,
                            modifier = Modifier.clickable { onNavigateToRecords() }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (allSales.isEmpty() && allExpenses.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(TrisaktiCardBg)
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No transactions recorded yet.",
                                color = TrisaktiTextMuted,
                                fontSize = 13.sp
                            )
                        }
                    } else {
                        // Display latest 4 transactions
                        val recentSales = allSales.take(3)
                        recentSales.forEach { sale ->
                            ActivityCard(
                                title = sale.description.ifBlank { "Sale (${sale.paymentType})" },
                                subtitle = "${FinancialCalculator.formatDisplayDate(sale.date)} • Paid via ${sale.paymentType}",
                                amount = sale.amount,
                                isPositive = true,
                                icon = Icons.Default.ArrowUpward,
                                color = TrisaktiGold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        val recentExpense = allExpenses.firstOrNull()
                        if (recentExpense != null) {
                            ActivityCard(
                                title = recentExpense.description.ifBlank { recentExpense.category },
                                subtitle = "${FinancialCalculator.formatDisplayDate(recentExpense.date)} • ${recentExpense.category}",
                                amount = recentExpense.amount,
                                isPositive = false,
                                icon = Icons.Default.ArrowDownward,
                                color = TrisaktiCoral
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionItem(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(52.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = TrisaktiCardBg,
            contentColor = TrisaktiTextPrimary
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TrisaktiTextPrimary
            )
        }
    }
}

@Composable
private fun ActivityCard(
    title: String,
    subtitle: String,
    amount: Double,
    isPositive: Boolean,
    icon: ImageVector,
    color: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(TrisaktiCardBg)
            .border(1.dp, TrisaktiCardBorder, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TrisaktiTextPrimary,
                        maxLines = 1
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = TrisaktiTextSecondary
                    )
                }
            }

            Text(
                text = (if (isPositive) "+" else "-") + FinancialCalculator.formatNpr(amount),
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
        }
    }
}

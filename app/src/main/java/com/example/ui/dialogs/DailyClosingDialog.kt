package com.example.ui.dialogs

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.domain.FinancialCalculator
import com.example.domain.FinancialSummary
import com.example.ui.theme.TrisaktiCardBg
import com.example.ui.theme.TrisaktiCardBorder
import com.example.ui.theme.TrisaktiCoral
import com.example.ui.theme.TrisaktiEmerald
import com.example.ui.theme.TrisaktiGold
import com.example.ui.theme.TrisaktiSurface
import com.example.ui.theme.TrisaktiTextMuted
import com.example.ui.theme.TrisaktiTextPrimary
import com.example.ui.theme.TrisaktiTextSecondary

@Composable
fun DailyClosingDialog(
    summary: FinancialSummary,
    onDismiss: () -> Unit,
    onConfirm: (date: String, openingCash: Double, actualCash: Double, notes: String) -> Unit
) {
    var date by remember { mutableStateOf(FinancialCalculator.todayDateString()) }
    var openingCashStr by remember { mutableStateOf("10000") }
    var actualCashStr by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    val openingCash = openingCashStr.toDoubleOrNull() ?: 0.0
    val actualCash = actualCashStr.toDoubleOrNull() ?: 0.0

    val cashSales = summary.cashSales
    val cashExpenses = summary.totalExpenses
    val creditSales = summary.creditSales
    val creditPayments = summary.creditCollected

    val expectedCash = FinancialCalculator.calculateExpectedCash(
        openingCash = openingCash,
        cashSales = cashSales,
        cashExpenses = cashExpenses,
        creditPayments = creditPayments
    )
    val difference = actualCash - expectedCash

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 20.dp)
                .testTag("daily_closing_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = TrisaktiSurface),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, TrisaktiSkyBorder())
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "DAILY CASH CLOSING",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF38BDF8),
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = "Verify cash drawer balance for today",
                            fontSize = 12.sp,
                            color = TrisaktiTextSecondary
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("close_daily_closing_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TrisaktiTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Date
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Closing Date", color = TrisaktiTextSecondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("closing_date_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TrisaktiTextPrimary,
                        unfocusedTextColor = TrisaktiTextPrimary,
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = TrisaktiCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Opening Cash
                OutlinedTextField(
                    value = openingCashStr,
                    onValueChange = { openingCashStr = it },
                    label = { Text("Opening Morning Cash (Rs.)", color = TrisaktiTextSecondary) },
                    placeholder = { Text("e.g. 10000", color = TrisaktiTextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("opening_cash_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TrisaktiTextPrimary,
                        unfocusedTextColor = TrisaktiTextPrimary,
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = TrisaktiCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Calculated Cash Breakdown Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(TrisaktiCardBg)
                        .border(1.dp, TrisaktiCardBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ClosingRow(title = "Opening Cash:", value = FinancialCalculator.formatNpr(openingCash), color = TrisaktiTextPrimary)
                        ClosingRow(title = "(+) Cash Sales:", value = FinancialCalculator.formatNpr(cashSales), color = TrisaktiEmerald)
                        ClosingRow(title = "(-) Cash Expenses:", value = FinancialCalculator.formatNpr(cashExpenses), color = TrisaktiCoral)
                        ClosingRow(title = "(+) Credit Payments:", value = FinancialCalculator.formatNpr(creditPayments), color = TrisaktiEmerald)
                        ClosingRow(title = "(Ref) Credit Sales:", value = FinancialCalculator.formatNpr(creditSales), color = TrisaktiGold)

                        HorizontalDivider(color = TrisaktiCardBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                        ClosingRow(
                            title = "EXPECTED CASH:",
                            value = FinancialCalculator.formatNpr(expectedCash),
                            color = Color(0xFF38BDF8),
                            isBold = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Actual Cash Counted
                OutlinedTextField(
                    value = actualCashStr,
                    onValueChange = { actualCashStr = it },
                    label = { Text("Actual Cash in Drawer (Rs.) *", color = TrisaktiTextSecondary) },
                    placeholder = { Text("Count cash and enter amount", color = TrisaktiTextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("actual_cash_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TrisaktiTextPrimary,
                        unfocusedTextColor = TrisaktiTextPrimary,
                        focusedBorderColor = if (difference == 0.0 && actualCash > 0) TrisaktiEmerald else Color(0xFF38BDF8),
                        unfocusedBorderColor = TrisaktiCardBorder
                    )
                )

                // Difference indicator
                if (actualCashStr.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    val diffColor = when {
                        difference == 0.0 -> TrisaktiEmerald
                        difference > 0 -> TrisaktiGold
                        else -> TrisaktiCoral
                    }
                    val diffText = when {
                        difference == 0.0 -> "Drawer Matched Perfectly! (Rs. 0 difference)"
                        difference > 0 -> "+${FinancialCalculator.formatNpr(difference)} Cash Surplus"
                        else -> "-${FinancialCalculator.formatNpr(Math.abs(difference))} Cash Shortfall"
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(diffColor.copy(alpha = 0.15f))
                            .border(1.dp, diffColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Closing Difference:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = diffColor
                            )
                            Text(
                                text = diffText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = diffColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Discrepancy Reason", color = TrisaktiTextSecondary) },
                    placeholder = { Text("e.g. Small coin change given to customer", color = TrisaktiTextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("closing_notes_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TrisaktiTextPrimary,
                        unfocusedTextColor = TrisaktiTextPrimary,
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = TrisaktiCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (isSubmitting) return@Button
                        isSubmitting = true
                        onConfirm(date, openingCash, actualCash, notes.trim())
                        onDismiss()
                    },
                    enabled = !isSubmitting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("save_daily_closing_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF38BDF8),
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = if (isSubmitting) "SAVING..." else "SAVE CLOSING RECORD",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ClosingRow(
    title: String,
    value: String,
    color: Color,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = if (isBold) 14.sp else 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = if (isBold) color else TrisaktiTextSecondary
        )
        Text(
            text = value,
            fontSize = if (isBold) 16.sp else 13.sp,
            fontWeight = if (isBold) FontWeight.Black else FontWeight.SemiBold,
            color = color
        )
    }
}

private fun TrisaktiSkyBorder(): Color = Color(0xFF0284C7).copy(alpha = 0.5f)

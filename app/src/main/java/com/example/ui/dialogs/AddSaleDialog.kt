package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.data.entity.SaleEntity
import com.example.domain.FinancialCalculator
import com.example.domain.PaymentType
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
fun AddSaleDialog(
    initialSale: SaleEntity? = null,
    onDismiss: () -> Unit,
    onConfirm: (date: String, amount: Double, productCost: Double, paymentType: String, description: String, notes: String) -> Unit
) {
    var date by remember { mutableStateOf(initialSale?.date ?: FinancialCalculator.todayDateString()) }
    var amountStr by remember {
        mutableStateOf(
            initialSale?.let { if (it.amount % 1.0 == 0.0) it.amount.toInt().toString() else it.amount.toString() } ?: ""
        )
    }
    var costStr by remember {
        mutableStateOf(
            initialSale?.let { if (it.productCost % 1.0 == 0.0) it.productCost.toInt().toString() else it.productCost.toString() } ?: ""
        )
    }
    var selectedPaymentType by remember {
        mutableStateOf(
            initialSale?.let { sale ->
                PaymentType.values().firstOrNull { it.displayName.equals(sale.paymentType, ignoreCase = true) }
            } ?: PaymentType.CASH
        )
    }
    var description by remember { mutableStateOf(initialSale?.description ?: "") }
    var notes by remember { mutableStateOf(initialSale?.notes ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    val amount = amountStr.toDoubleOrNull() ?: 0.0
    val cost = costStr.toDoubleOrNull() ?: 0.0
    val grossProfit = amount - cost
    val isGrossLoss = cost > amount && amount > 0.0

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 24.dp)
                .testTag("add_sale_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = TrisaktiSurface),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, TrisaktiGold.copy(alpha = 0.5f))
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
                            text = if (initialSale != null) "EDIT SALE RECORD" else "+ ADD NEW SALE",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = TrisaktiGold,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = if (initialSale != null) "Update transaction details" else "Record customer sale & product cost",
                            fontSize = 12.sp,
                            color = TrisaktiTextSecondary
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("close_add_sale_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TrisaktiTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Date
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (YYYY-MM-DD)", color = TrisaktiTextSecondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sale_date_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TrisaktiTextPrimary,
                        unfocusedTextColor = TrisaktiTextPrimary,
                        focusedBorderColor = TrisaktiGold,
                        unfocusedBorderColor = TrisaktiCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Sales Amount
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Total Sale Amount (Rs.) *", color = TrisaktiTextSecondary) },
                    placeholder = { Text("e.g. 25000", color = TrisaktiTextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sale_amount_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TrisaktiTextPrimary,
                        unfocusedTextColor = TrisaktiTextPrimary,
                        focusedBorderColor = TrisaktiGold,
                        unfocusedBorderColor = TrisaktiCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Product Cost (COGS)
                OutlinedTextField(
                    value = costStr,
                    onValueChange = { costStr = it },
                    label = { Text("Product Cost / COGS (Rs.) *", color = TrisaktiTextSecondary) },
                    placeholder = { Text("e.g. 17000", color = TrisaktiTextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sale_cost_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TrisaktiTextPrimary,
                        unfocusedTextColor = TrisaktiTextPrimary,
                        focusedBorderColor = TrisaktiEmerald,
                        unfocusedBorderColor = TrisaktiCardBorder
                    )
                )

                if (amount > 0 && cost > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    val marginColor = if (isGrossLoss) TrisaktiCoral else TrisaktiEmerald
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(marginColor.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                            .border(1.dp, marginColor.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isGrossLoss) "Gross Loss (Cost > Sale):" else "Gross Margin:",
                                fontSize = 12.sp,
                                color = marginColor,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = FinancialCalculator.formatNpr(grossProfit),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = marginColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Payment Type Chips
                Text(
                    text = "Payment Method:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TrisaktiTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PaymentType.values().forEach { type ->
                        val isSelected = selectedPaymentType == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) TrisaktiGold else TrisaktiCardBg)
                                .border(
                                    1.dp,
                                    if (isSelected) TrisaktiGold else TrisaktiCardBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedPaymentType = type }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = type.displayName,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.Black else TrisaktiTextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description / Items Sold", color = TrisaktiTextSecondary) },
                    placeholder = { Text("e.g. 25kg Rice bags & Mustard oil", color = TrisaktiTextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sale_description_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TrisaktiTextPrimary,
                        unfocusedTextColor = TrisaktiTextPrimary,
                        focusedBorderColor = TrisaktiGold,
                        unfocusedBorderColor = TrisaktiCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (Optional)", color = TrisaktiTextSecondary) },
                    placeholder = { Text("e.g. Delivered to customer address", color = TrisaktiTextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sale_notes_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TrisaktiTextPrimary,
                        unfocusedTextColor = TrisaktiTextPrimary,
                        focusedBorderColor = TrisaktiGold,
                        unfocusedBorderColor = TrisaktiCardBorder
                    )
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = errorMessage!!,
                        color = Color(0xFFFB7185),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Submit Button
                Button(
                    onClick = {
                        if (isSubmitting) return@Button
                        if (amount <= 0.0) {
                            errorMessage = "Please enter a valid sale amount."
                            return@Button
                        }
                        if (cost < 0.0) {
                            errorMessage = "Product cost cannot be negative."
                            return@Button
                        }
                        isSubmitting = true
                        onConfirm(
                            date,
                            amount,
                            cost,
                            selectedPaymentType.displayName,
                            if (description.isBlank()) "Counter sale" else description.trim(),
                            notes.trim()
                        )
                        onDismiss()
                    },
                    enabled = !isSubmitting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("save_sale_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TrisaktiGold,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = if (isSubmitting) "SAVING..." else if (initialSale != null) "UPDATE SALE RECORD" else "SAVE SALE RECORD",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

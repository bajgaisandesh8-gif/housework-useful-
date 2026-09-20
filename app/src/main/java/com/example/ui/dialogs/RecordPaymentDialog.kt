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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import com.example.data.entity.CreditCustomerEntity
import com.example.domain.FinancialCalculator
import com.example.ui.theme.TrisaktiCardBg
import com.example.ui.theme.TrisaktiCardBorder
import com.example.ui.theme.TrisaktiEmerald
import com.example.ui.theme.TrisaktiSurface
import com.example.ui.theme.TrisaktiTextMuted
import com.example.ui.theme.TrisaktiTextPrimary
import com.example.ui.theme.TrisaktiTextSecondary

@Composable
fun RecordPaymentDialog(
    customers: List<CreditCustomerEntity>,
    customerBalances: Map<Long, Double>,
    preselectedCustomerId: Long? = null,
    onDismiss: () -> Unit,
    onConfirm: (customerId: Long, date: String, amount: Double, description: String) -> Unit
) {
    var selectedCustomer by remember {
        mutableStateOf(
            if (preselectedCustomerId != null) {
                customers.find { it.id == preselectedCustomerId } ?: customers.firstOrNull()
            } else {
                customers.firstOrNull()
            }
        )
    }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var date by remember { mutableStateOf(FinancialCalculator.todayDateString()) }
    var amountStr by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("Cash counter repayment") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    val currentDue = selectedCustomer?.let { customerBalances.getOrDefault(it.id, 0.0) } ?: 0.0

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 24.dp)
                .testTag("record_payment_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = TrisaktiSurface),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, TrisaktiEmerald.copy(alpha = 0.5f))
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
                            text = "RECORD PAYMENT RECEIVED",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = TrisaktiEmerald,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = "Customer repayment against credit",
                            fontSize = 12.sp,
                            color = TrisaktiTextSecondary
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("close_record_payment_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TrisaktiTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Customer Selection
                Text(
                    text = "Customer Paying *",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TrisaktiTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(TrisaktiCardBg)
                        .border(1.dp, TrisaktiCardBorder, RoundedCornerShape(10.dp))
                        .clickable { dropdownExpanded = true }
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = TrisaktiEmerald,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Column {
                                Text(
                                    text = selectedCustomer?.name ?: "Choose customer",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TrisaktiTextPrimary
                                )
                                Text(
                                    text = "Current Due: ${FinancialCalculator.formatNpr(currentDue)}",
                                    fontSize = 11.sp,
                                    color = TrisaktiEmerald
                                )
                            }
                        }
                        Text(
                            text = "Change ▼",
                            fontSize = 12.sp,
                            color = TrisaktiEmerald,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                        modifier = Modifier.background(TrisaktiSurface)
                    ) {
                        customers.forEach { cust ->
                            val due = customerBalances.getOrDefault(cust.id, 0.0)
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(cust.name, color = TrisaktiTextPrimary, fontWeight = FontWeight.Bold)
                                        Text(
                                            "Outstanding: ${FinancialCalculator.formatNpr(due)}",
                                            color = TrisaktiEmerald,
                                            fontSize = 11.sp
                                        )
                                    }
                                },
                                onClick = {
                                    selectedCustomer = cust
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Date
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (YYYY-MM-DD)", color = TrisaktiTextSecondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("payment_date_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TrisaktiTextPrimary,
                        unfocusedTextColor = TrisaktiTextPrimary,
                        focusedBorderColor = TrisaktiEmerald,
                        unfocusedBorderColor = TrisaktiCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Payment Amount
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Payment Collected (Rs.) *", color = TrisaktiTextSecondary) },
                    placeholder = { Text("e.g. 1000", color = TrisaktiTextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("payment_amount_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TrisaktiTextPrimary,
                        unfocusedTextColor = TrisaktiTextPrimary,
                        focusedBorderColor = TrisaktiEmerald,
                        unfocusedBorderColor = TrisaktiCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Payment Method / Description", color = TrisaktiTextSecondary) },
                    placeholder = { Text("e.g. Cash counter repayment / eSewa", color = TrisaktiTextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("payment_description_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TrisaktiTextPrimary,
                        unfocusedTextColor = TrisaktiTextPrimary,
                        focusedBorderColor = TrisaktiEmerald,
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

                Button(
                    onClick = {
                        if (isSubmitting) return@Button
                        val cust = selectedCustomer
                        if (cust == null) {
                            errorMessage = "Please select a customer."
                            return@Button
                        }
                        val amount = amountStr.toDoubleOrNull() ?: 0.0
                        if (amount <= 0.0) {
                            errorMessage = "Please enter a valid payment amount."
                            return@Button
                        }
                        if (currentDue <= 0.0) {
                            errorMessage = "Customer currently has no outstanding balance to pay."
                            return@Button
                        }
                        if (amount > currentDue) {
                            errorMessage = "Payment amount cannot exceed customer's outstanding balance of ${FinancialCalculator.formatNpr(currentDue)}."
                            return@Button
                        }
                        isSubmitting = true
                        onConfirm(
                            cust.id,
                            date,
                            amount,
                            if (description.isBlank()) "Cash repayment" else description.trim()
                        )
                        onDismiss()
                    },
                    enabled = !isSubmitting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("save_payment_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TrisaktiEmerald,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = if (isSubmitting) "SAVING..." else "RECORD PAYMENT RECEIVED",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

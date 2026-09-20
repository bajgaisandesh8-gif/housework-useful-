package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import com.example.data.entity.ExpenseEntity
import com.example.domain.ExpenseCategory
import com.example.domain.FinancialCalculator
import com.example.ui.theme.TrisaktiCardBg
import com.example.ui.theme.TrisaktiCardBorder
import com.example.ui.theme.TrisaktiCoral
import com.example.ui.theme.TrisaktiSurface
import com.example.ui.theme.TrisaktiTextMuted
import com.example.ui.theme.TrisaktiTextPrimary
import com.example.ui.theme.TrisaktiTextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddExpenseDialog(
    initialExpense: ExpenseEntity? = null,
    onDismiss: () -> Unit,
    onConfirm: (date: String, amount: Double, category: String, description: String, notes: String) -> Unit
) {
    var date by remember { mutableStateOf(initialExpense?.date ?: FinancialCalculator.todayDateString()) }
    var amountStr by remember {
        mutableStateOf(
            initialExpense?.let { if (it.amount % 1.0 == 0.0) it.amount.toInt().toString() else it.amount.toString() } ?: ""
        )
    }
    var selectedCategory by remember {
        mutableStateOf(
            initialExpense?.let { exp ->
                ExpenseCategory.values().firstOrNull { it.displayName.equals(exp.category, ignoreCase = true) }
            } ?: ExpenseCategory.TRANSPORTATION
        )
    }
    var description by remember { mutableStateOf(initialExpense?.description ?: "") }
    var notes by remember { mutableStateOf(initialExpense?.notes ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 24.dp)
                .testTag("add_expense_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = TrisaktiSurface),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, TrisaktiCoral.copy(alpha = 0.5f))
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
                            text = if (initialExpense != null) "EDIT EXPENSE" else "+ ADD EXPENSE",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = TrisaktiCoral,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = if (initialExpense != null) "Update operating cost details" else "Record business operating cost",
                            fontSize = 12.sp,
                            color = TrisaktiTextSecondary
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("close_add_expense_button")
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
                        .testTag("expense_date_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TrisaktiTextPrimary,
                        unfocusedTextColor = TrisaktiTextPrimary,
                        focusedBorderColor = TrisaktiCoral,
                        unfocusedBorderColor = TrisaktiCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Expense Amount
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Expense Amount (Rs.) *", color = TrisaktiTextSecondary) },
                    placeholder = { Text("e.g. 4000", color = TrisaktiTextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("expense_amount_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TrisaktiTextPrimary,
                        unfocusedTextColor = TrisaktiTextPrimary,
                        focusedBorderColor = TrisaktiCoral,
                        unfocusedBorderColor = TrisaktiCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Categories (Rent, Electricity, Transportation, Staff, Maintenance, Supplies, Other)
                Text(
                    text = "Expense Category:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TrisaktiTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ExpenseCategory.values().forEach { cat ->
                        val isSelected = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) TrisaktiCoral else TrisaktiCardBg)
                                .border(
                                    1.dp,
                                    if (isSelected) TrisaktiCoral else TrisaktiCardBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cat.displayName,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TrisaktiTextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description", color = TrisaktiTextSecondary) },
                    placeholder = { Text("e.g. Delivery freight from Kalimati", color = TrisaktiTextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("expense_description_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TrisaktiTextPrimary,
                        unfocusedTextColor = TrisaktiTextPrimary,
                        focusedBorderColor = TrisaktiCoral,
                        unfocusedBorderColor = TrisaktiCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (Optional)", color = TrisaktiTextSecondary) },
                    placeholder = { Text("e.g. Paid in cash to driver", color = TrisaktiTextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("expense_notes_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TrisaktiTextPrimary,
                        unfocusedTextColor = TrisaktiTextPrimary,
                        focusedBorderColor = TrisaktiCoral,
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
                        val amount = amountStr.toDoubleOrNull() ?: 0.0
                        if (amount <= 0.0) {
                            errorMessage = "Please enter a valid expense amount."
                            return@Button
                        }
                        isSubmitting = true
                        onConfirm(
                            date,
                            amount,
                            selectedCategory.displayName,
                            if (description.isBlank()) "${selectedCategory.displayName} payment" else description.trim(),
                            notes.trim()
                        )
                        onDismiss()
                    },
                    enabled = !isSubmitting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("save_expense_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TrisaktiCoral,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (isSubmitting) "SAVING..." else if (initialExpense != null) "UPDATE EXPENSE RECORD" else "SAVE EXPENSE RECORD",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

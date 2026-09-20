package com.example.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.TrisaktiCardBorder
import com.example.ui.theme.TrisaktiEmerald
import com.example.ui.theme.TrisaktiGold
import com.example.ui.theme.TrisaktiSurface
import com.example.ui.theme.TrisaktiTextMuted
import com.example.ui.theme.TrisaktiTextPrimary
import com.example.ui.theme.TrisaktiTextSecondary

@Composable
fun AddCustomerDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, phone: String, notes: String, initialCredit: Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var initialCreditStr by remember { mutableStateOf("") }
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
                .testTag("add_customer_dialog"),
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
                            text = "+ NEW CREDIT CUSTOMER",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = TrisaktiGold,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = "Add party to udhaar ledger",
                            fontSize = 12.sp,
                            color = TrisaktiTextSecondary
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("close_add_customer_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TrisaktiTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Customer Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Customer Full Name *", color = TrisaktiTextSecondary) },
                    placeholder = { Text("e.g. Ram Sharma", color = TrisaktiTextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("customer_name_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TrisaktiTextPrimary,
                        unfocusedTextColor = TrisaktiTextPrimary,
                        focusedBorderColor = TrisaktiGold,
                        unfocusedBorderColor = TrisaktiCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Phone
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Mobile Phone Number (Optional)", color = TrisaktiTextSecondary) },
                    placeholder = { Text("e.g. 9841234567", color = TrisaktiTextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("customer_phone_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TrisaktiTextPrimary,
                        unfocusedTextColor = TrisaktiTextPrimary,
                        focusedBorderColor = TrisaktiGold,
                        unfocusedBorderColor = TrisaktiCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Initial Credit
                OutlinedTextField(
                    value = initialCreditStr,
                    onValueChange = { initialCreditStr = it },
                    label = { Text("Initial Credit Balance (Rs., Optional)", color = TrisaktiTextSecondary) },
                    placeholder = { Text("e.g. 2000 (leave empty if 0)", color = TrisaktiTextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("customer_initial_credit_input"),
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
                    label = { Text("Customer Notes / Address (Optional)", color = TrisaktiTextSecondary) },
                    placeholder = { Text("e.g. Local hotel owner in Kalanki", color = TrisaktiTextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("customer_notes_input"),
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

                Button(
                    onClick = {
                        if (isSubmitting) return@Button
                        if (name.isBlank()) {
                            errorMessage = "Please enter customer name."
                            return@Button
                        }
                        val initialCredit = initialCreditStr.toDoubleOrNull() ?: 0.0
                        if (initialCredit < 0.0) {
                            errorMessage = "Initial credit balance cannot be negative."
                            return@Button
                        }
                        isSubmitting = true
                        onConfirm(name.trim(), phone.trim(), notes.trim(), initialCredit)
                        onDismiss()
                    },
                    enabled = !isSubmitting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("save_customer_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TrisaktiGold,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = if (isSubmitting) "SAVING..." else "CREATE CUSTOMER PROFILE",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

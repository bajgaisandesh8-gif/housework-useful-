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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.CreditCustomerEntity
import com.example.data.entity.CreditTransactionEntity
import com.example.domain.FinancialCalculator
import com.example.ui.components.BackgroundWatermark
import com.example.ui.components.TrisaktiTopBar
import com.example.ui.theme.TrisaktiBackground
import com.example.ui.theme.TrisaktiCardBg
import com.example.ui.theme.TrisaktiCardBorder
import com.example.ui.theme.TrisaktiCoral
import com.example.ui.theme.TrisaktiEmerald
import com.example.ui.theme.TrisaktiGold
import com.example.ui.theme.TrisaktiSurface
import com.example.ui.theme.TrisaktiTextMuted
import com.example.ui.theme.TrisaktiTextPrimary
import com.example.ui.theme.TrisaktiTextSecondary
import com.example.ui.viewmodel.TrisaktiViewModel

@Composable
fun CreditScreen(
    viewModel: TrisaktiViewModel,
    onOpenAddCustomer: () -> Unit,
    onOpenAddCredit: (customerId: Long?) -> Unit,
    onOpenRecordPayment: (customerId: Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    val customers by viewModel.allCustomers.collectAsStateWithLifecycle()
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val customerBalances by viewModel.customerBalances.collectAsStateWithLifecycle()
    val totalOutstandingCredit by viewModel.totalOutstandingCredit.collectAsStateWithLifecycle()
    val selectedCustomer by viewModel.selectedCustomer.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var customerToDelete by remember { mutableStateOf<CreditCustomerEntity?>(null) }
    var txToDelete by remember { mutableStateOf<CreditTransactionEntity?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TrisaktiBackground)
    ) {
        BackgroundWatermark()

        if (selectedCustomer != null) {
            // Customer Detailed Udhaar Ledger Screen
            CustomerLedgerView(
                customer = selectedCustomer!!,
                balance = customerBalances.getOrDefault(selectedCustomer!!.id, 0.0),
                transactions = allTransactions.filter { it.customerId == selectedCustomer!!.id },
                onBack = { viewModel.selectCustomer(null) },
                onAddCredit = { onOpenAddCredit(selectedCustomer!!.id) },
                onRecordPayment = { onOpenRecordPayment(selectedCustomer!!.id) },
                onDeleteCustomer = { customerToDelete = selectedCustomer },
                onDeleteTransaction = { tx -> txToDelete = tx }
            )
        } else {
            // Customer List Screen
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("credit_screen"),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 100.dp)
            ) {
                item {
                    TrisaktiTopBar(subtitle = "Udhaar & Credit Ledger")
                }

                // Total Shop Credit Outstanding Card
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(TrisaktiCardBg)
                            .border(1.dp, TrisaktiGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "TOTAL SHOP CREDIT OUTSTANDING",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TrisaktiGold,
                                    letterSpacing = 0.8.sp
                                )
                                Text(
                                    text = FinancialCalculator.formatNpr(totalOutstandingCredit),
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TrisaktiTextPrimary
                                )
                                Text(
                                    text = "Total pending receivables from all customers",
                                    fontSize = 11.sp,
                                    color = TrisaktiTextSecondary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(TrisaktiGold.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.People,
                                    contentDescription = null,
                                    tint = TrisaktiGold,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }

                // Action Buttons Row: + New Customer, Record Payment
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onOpenAddCustomer,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("btn_add_customer"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TrisaktiGold, contentColor = Color.Black)
                        ) {
                            Text("+ Add Customer", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Button(
                            onClick = { onOpenRecordPayment(null) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("btn_record_payment_general"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TrisaktiEmerald, contentColor = Color.Black)
                        ) {
                            Text("Record Payment", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                // Search Filter
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search customer by name or phone...", color = TrisaktiTextMuted) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .testTag("search_customer_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TrisaktiTextPrimary,
                            unfocusedTextColor = TrisaktiTextPrimary,
                            focusedBorderColor = TrisaktiGold,
                            unfocusedBorderColor = TrisaktiCardBorder
                        )
                    )
                }

                // Customer List
                val filteredCustomers = customers.filter {
                    it.name.contains(searchQuery, ignoreCase = true) || it.phone.contains(searchQuery)
                }

                item {
                    Text(
                        text = "CUSTOMERS (${filteredCustomers.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = TrisaktiTextSecondary,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                if (filteredCustomers.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 32.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(TrisaktiCardBg)
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No customers found. Click '+ Add Customer' to register one.",
                                color = TrisaktiTextMuted,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    items(filteredCustomers, key = { it.id }) { customer ->
                        val balance = customerBalances.getOrDefault(customer.id, 0.0)
                        val lastTx = allTransactions.filter { it.customerId == customer.id }.maxByOrNull { it.date }
                        val lastActivity = lastTx?.let { "${it.type}: ${FinancialCalculator.formatDisplayDate(it.date)}" } ?: "No activity yet"
                        CustomerCard(
                            customer = customer,
                            balance = balance,
                            lastActivity = lastActivity,
                            onClick = { viewModel.selectCustomer(customer) },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }

        // Delete Customer Dialog
        if (customerToDelete != null) {
            AlertDialog(
                onDismissRequest = { customerToDelete = null },
                containerColor = TrisaktiSurface,
                title = { Text("Remove Customer?", color = TrisaktiTextPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "Are you sure you want to remove ${customerToDelete!!.name} from the ledger?",
                        color = TrisaktiTextSecondary
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteCustomer(customerToDelete!!)
                            customerToDelete = null
                        }
                    ) {
                        Text("Delete", color = TrisaktiCoral, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { customerToDelete = null }) {
                        Text("Cancel", color = TrisaktiTextPrimary)
                    }
                }
            )
        }

        // Delete Transaction Dialog
        if (txToDelete != null) {
            AlertDialog(
                onDismissRequest = { txToDelete = null },
                containerColor = TrisaktiSurface,
                title = { Text("Delete Entry?", color = TrisaktiTextPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "Are you sure you want to delete this ${txToDelete!!.type} entry of ${FinancialCalculator.formatNpr(txToDelete!!.amount)}?",
                        color = TrisaktiTextSecondary
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteCreditTransaction(txToDelete!!)
                            txToDelete = null
                        }
                    ) {
                        Text("Delete", color = TrisaktiCoral, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { txToDelete = null }) {
                        Text("Cancel", color = TrisaktiTextPrimary)
                    }
                }
            )
        }
    }
}

@Composable
private fun CustomerCard(
    customer: CreditCustomerEntity,
    balance: Double,
    lastActivity: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(TrisaktiCardBg)
            .border(1.dp, TrisaktiCardBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(TrisaktiGold.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = TrisaktiGold,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = customer.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TrisaktiTextPrimary
                    )
                    if (customer.phone.isNotBlank()) {
                        Text(
                            text = customer.phone,
                            fontSize = 12.sp,
                            color = TrisaktiTextSecondary
                        )
                    }
                    Text(
                        text = lastActivity,
                        fontSize = 11.sp,
                        color = TrisaktiTextMuted
                    )
                    if (customer.notes.isNotBlank()) {
                        Text(
                            text = customer.notes,
                            fontSize = 10.sp,
                            color = TrisaktiTextMuted.copy(alpha = 0.8f),
                            maxLines = 1
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "DUE BALANCE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TrisaktiTextSecondary
                )
                Text(
                    text = FinancialCalculator.formatNpr(balance),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = if (balance > 0) TrisaktiGold else TrisaktiEmerald
                )
                Text(
                    text = "View Ledger →",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TrisaktiEmerald
                )
            }
        }
    }
}

/**
 * Detailed Customer Ledger View (e.g. Ram's account with chronological entries)
 */
@Composable
private fun CustomerLedgerView(
    customer: CreditCustomerEntity,
    balance: Double,
    transactions: List<CreditTransactionEntity>,
    onBack: () -> Unit,
    onAddCredit: () -> Unit,
    onRecordPayment: () -> Unit,
    onDeleteCustomer: () -> Unit,
    onDeleteTransaction: (CreditTransactionEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("customer_ledger_view"),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 100.dp)
    ) {
        // Back Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TrisaktiSurface)
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TrisaktiTextPrimary
                            )
                        }
                        Column {
                            Text(
                                text = customer.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = TrisaktiTextPrimary
                            )
                            if (customer.phone.isNotBlank()) {
                                Text(
                                    text = customer.phone,
                                    fontSize = 12.sp,
                                    color = TrisaktiGold
                                )
                            }
                        }
                    }

                    IconButton(onClick = onDeleteCustomer) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Customer",
                            tint = TrisaktiCoral
                        )
                    }
                }
            }
        }

        // Customer Balance Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(TrisaktiCardBg)
                    .border(1.dp, TrisaktiGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "CURRENT OUTSTANDING BALANCE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TrisaktiGold
                            )
                            Text(
                                text = FinancialCalculator.formatNpr(balance),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = if (balance > 0) TrisaktiGold else TrisaktiEmerald
                            )
                            if (customer.notes.isNotBlank()) {
                                Text(
                                    text = "Notes: ${customer.notes}",
                                    fontSize = 11.sp,
                                    color = TrisaktiTextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onAddCredit,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TrisaktiGold, contentColor = Color.Black)
                        ) {
                            Text("+ Give Credit", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Button(
                            onClick = onRecordPayment,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TrisaktiEmerald, contentColor = Color.Black)
                        ) {
                            Text("Record Payment", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Ledger Statement Title
        item {
            Text(
                text = "TRANSACTION LEDGER HISTORY (${transactions.size})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = TrisaktiTextSecondary,
                letterSpacing = 0.8.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        if (transactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(TrisaktiCardBg)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No credit transactions recorded for this customer.",
                        color = TrisaktiTextMuted,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(transactions, key = { it.id }) { tx ->
                val isCredit = tx.type.equals("CREDIT", ignoreCase = true)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
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
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(if (isCredit) TrisaktiGold.copy(alpha = 0.2f) else TrisaktiEmerald.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isCredit) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    tint = if (isCredit) TrisaktiGold else TrisaktiEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = if (isCredit) "Credit Given" else "Payment Received",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCredit) TrisaktiGold else TrisaktiEmerald
                                )
                                Text(
                                    text = "${FinancialCalculator.formatDisplayDate(tx.date)} • ${tx.description}",
                                    fontSize = 11.sp,
                                    color = TrisaktiTextSecondary
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = (if (isCredit) "+" else "-") + FinancialCalculator.formatNpr(tx.amount),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isCredit) TrisaktiGold else TrisaktiEmerald
                            )

                            IconButton(
                                onClick = { onDeleteTransaction(tx) },
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
                }
            }
        }
    }
}

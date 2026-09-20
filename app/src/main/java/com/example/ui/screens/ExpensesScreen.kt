package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.entity.ExpenseEntity
import com.example.domain.ExpenseCategory
import com.example.domain.FinancialCalculator
import com.example.ui.components.BackgroundWatermark
import com.example.ui.components.TrisaktiTopBar
import com.example.ui.theme.TrisaktiBackground
import com.example.ui.theme.TrisaktiCardBg
import com.example.ui.theme.TrisaktiCardBorder
import com.example.ui.theme.TrisaktiCoral
import com.example.ui.theme.TrisaktiSurface
import com.example.ui.theme.TrisaktiTextMuted
import com.example.ui.theme.TrisaktiTextPrimary
import com.example.ui.theme.TrisaktiTextSecondary
import com.example.ui.viewmodel.TrisaktiViewModel

@Composable
fun ExpensesScreen(
    viewModel: TrisaktiViewModel,
    onOpenAddExpense: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedExpenseCategory.collectAsStateWithLifecycle()
    var expenseToDelete by remember { mutableStateOf<ExpenseEntity?>(null) }

    val filteredExpenses = remember(allExpenses, selectedCategory) {
        if (selectedCategory == null || selectedCategory == "All") {
            allExpenses
        } else {
            allExpenses.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        }
    }

    val totalExpenses = filteredExpenses.sumOf { it.amount }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TrisaktiBackground)
    ) {
        BackgroundWatermark()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("expenses_screen"),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 100.dp)
        ) {
            item {
                TrisaktiTopBar(subtitle = "Operating Expenses Tracker")
            }

            // Categories horizontal chip filter (Rent, Electricity, Transportation, Staff, Maintenance, Supplies, Other)
            item {
                val categories = listOf("All") + ExpenseCategory.values().map { it.displayName }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = (selectedCategory ?: "All") == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) TrisaktiCoral else TrisaktiCardBg)
                                .border(
                                    1.dp,
                                    if (isSelected) TrisaktiCoral else TrisaktiCardBorder,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { viewModel.setExpenseCategoryFilter(if (cat == "All") null else cat) }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = cat,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TrisaktiTextSecondary
                            )
                        }
                    }
                }
            }

            // Total Expense Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(TrisaktiCardBg)
                        .border(1.dp, TrisaktiCardBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "TOTAL OPERATING EXPENSES",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TrisaktiCoral,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = FinancialCalculator.formatNpr(totalExpenses),
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                color = TrisaktiTextPrimary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(TrisaktiCoral.copy(alpha = 0.15f))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "${filteredExpenses.size} Records",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TrisaktiCoral
                            )
                        }
                    }
                }
            }

            // Section Title
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "EXPENSE RECORDS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = TrisaktiTextSecondary,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            if (filteredExpenses.isEmpty()) {
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
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = TrisaktiTextMuted,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No expenses found in this category.",
                                fontSize = 14.sp,
                                color = TrisaktiTextMuted,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            } else {
                items(filteredExpenses, key = { it.id }) { expense ->
                    ExpenseCard(
                        expense = expense,
                        onDelete = { expenseToDelete = expense },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // FAB for Adding Expense
        FloatingActionButton(
            onClick = onOpenAddExpense,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 20.dp)
                .testTag("fab_add_expense"),
            containerColor = TrisaktiCoral,
            contentColor = Color.White
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Expense")
        }

        // Delete Dialog
        if (expenseToDelete != null) {
            AlertDialog(
                onDismissRequest = { expenseToDelete = null },
                containerColor = TrisaktiSurface,
                title = { Text("Delete Expense Record?", color = TrisaktiTextPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "Are you sure you want to delete this expense of ${FinancialCalculator.formatNpr(expenseToDelete!!.amount)} for ${expenseToDelete!!.category}?",
                        color = TrisaktiTextSecondary
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteExpense(expenseToDelete!!)
                            expenseToDelete = null
                        }
                    ) {
                        Text("Delete", color = TrisaktiCoral, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { expenseToDelete = null }) {
                        Text("Cancel", color = TrisaktiTextPrimary)
                    }
                }
            )
        }
    }
}

@Composable
private fun ExpenseCard(
    expense: ExpenseEntity,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
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
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(TrisaktiCoral.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = expense.category.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TrisaktiCoral
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = FinancialCalculator.formatDisplayDate(expense.date),
                        fontSize = 12.sp,
                        color = TrisaktiTextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = expense.description.ifBlank { "${expense.category} expense" },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TrisaktiTextPrimary
                )

                if (expense.notes.isNotBlank()) {
                    Text(
                        text = "Note: ${expense.notes}",
                        fontSize = 11.sp,
                        color = TrisaktiTextMuted
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "-" + FinancialCalculator.formatNpr(expense.amount),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = TrisaktiCoral
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = TrisaktiTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

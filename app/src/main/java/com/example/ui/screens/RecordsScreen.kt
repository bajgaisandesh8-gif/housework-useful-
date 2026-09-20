package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.ExpenseEntity
import com.example.data.entity.SaleEntity
import com.example.domain.FinancialCalculator
import com.example.domain.TimePeriod
import com.example.ui.components.BackgroundWatermark
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
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

/**
 * Unified record model representing both Sales and Expenses in one chronological stream.
 */
sealed class RecordItem {
    abstract val id: Long
    abstract val date: String
    abstract val amount: Double
    abstract val description: String
    abstract val notes: String

    data class Sale(val sale: SaleEntity) : RecordItem() {
        override val id: Long get() = sale.id
        override val date: String get() = sale.date
        override val amount: Double get() = sale.amount
        override val description: String get() = sale.description
        override val notes: String get() = sale.notes
        val productCost: Double get() = sale.productCost
        val paymentType: String get() = sale.paymentType
        val profit: Double get() = (sale.amount - sale.productCost).coerceAtLeast(0.0)
    }

    data class Expense(val expense: ExpenseEntity) : RecordItem() {
        override val id: Long get() = expense.id
        override val date: String get() = expense.date
        override val amount: Double get() = expense.amount
        override val description: String get() = expense.description
        override val notes: String get() = expense.notes
        val category: String get() = expense.category
    }
}

@Composable
fun RecordsScreen(
    viewModel: TrisaktiViewModel,
    onOpenAddSale: () -> Unit,
    onOpenEditSale: (SaleEntity) -> Unit,
    onOpenAddExpense: () -> Unit,
    onOpenEditExpense: (ExpenseEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val allSales by viewModel.allSales.collectAsStateWithLifecycle()
    val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    val selectedPeriod by viewModel.recordsPeriod.collectAsStateWithLifecycle()
    val customRecordsDate by viewModel.customRecordsDate.collectAsStateWithLifecycle()
    val recordsTypeFilter by viewModel.recordsTypeFilter.collectAsStateWithLifecycle()

    var saleToDelete by remember { mutableStateOf<SaleEntity?>(null) }
    var expenseToDelete by remember { mutableStateOf<ExpenseEntity?>(null) }
    var detailedRecord by remember { mutableStateOf<RecordItem?>(null) }
    var showCustomDatePicker by remember { mutableStateOf(false) }

    val todayStr = FinancialCalculator.todayDateString()
    val today = try { LocalDate.parse(todayStr) } catch (_: Exception) { LocalDate.now() }
    val weekStartStr = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).format(DateTimeFormatter.ISO_LOCAL_DATE)
    val monthStartStr = today.withDayOfMonth(1).format(DateTimeFormatter.ISO_LOCAL_DATE)

    // Date filtering logic
    val isDateMatch: (String) -> Boolean = { date ->
        when (selectedPeriod) {
            TimePeriod.TODAY -> date == todayStr
            TimePeriod.THIS_WEEK -> date in weekStartStr..todayStr
            TimePeriod.THIS_MONTH -> date in monthStartStr..todayStr
            TimePeriod.CUSTOM -> date == (customRecordsDate ?: todayStr)
            TimePeriod.ALL_TIME -> true
        }
    }

    val filteredSales = remember(allSales, selectedPeriod, customRecordsDate) {
        allSales.filter { isDateMatch(it.date) }
    }

    val filteredExpenses = remember(allExpenses, selectedPeriod, customRecordsDate) {
        allExpenses.filter { isDateMatch(it.date) }
    }

    // Combine and sort by date descending, then ID descending
    val unifiedRecords: List<RecordItem> = remember(filteredSales, filteredExpenses, recordsTypeFilter) {
        val list = mutableListOf<RecordItem>()
        if (recordsTypeFilter == "ALL" || recordsTypeFilter == "SALES") {
            list.addAll(filteredSales.map { RecordItem.Sale(it) })
        }
        if (recordsTypeFilter == "ALL" || recordsTypeFilter == "EXPENSES") {
            list.addAll(filteredExpenses.map { RecordItem.Expense(it) })
        }
        list.sortedWith(compareByDescending<RecordItem> { it.date }.thenByDescending { it.id })
    }

    // Totals for filtered records
    val totalSales = filteredSales.sumOf { it.amount }
    val totalCost = filteredSales.sumOf { it.productCost }
    val totalExpenses = filteredExpenses.sumOf { it.amount }
    val netProfit = FinancialCalculator.calculateEstimatedProfit(totalSales, totalCost, totalExpenses)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TrisaktiBackground)
    ) {
        BackgroundWatermark()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("records_screen"),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // Screen Header
            item {
                TrisaktiTopBar(
                    subtitle = "Daily Sales, Costs & Expenses",
                    showDemoBadge = false
                )
            }

            // Quick Add Action Buttons (+ Sale, + Expense)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onOpenAddSale,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("btn_records_add_sale"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TrisaktiGold,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("+ Add Sale", fontWeight = FontWeight.Black, fontSize = 13.sp)
                    }

                    Button(
                        onClick = onOpenAddExpense,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("btn_records_add_expense"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TrisaktiCoral,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("+ Add Expense", fontWeight = FontWeight.Black, fontSize = 13.sp)
                    }
                }
            }

            // Type Filter Bar: ALL | SALES | EXPENSES
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val types = listOf("ALL" to "All Records", "SALES" to "Sales Only", "EXPENSES" to "Expenses Only")
                    types.forEach { (key, label) ->
                        val isSelected = recordsTypeFilter == key
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) TrisaktiEmerald else TrisaktiCardBg)
                                .border(1.dp, if (isSelected) TrisaktiEmerald else TrisaktiCardBorder, RoundedCornerShape(10.dp))
                                .clickable { viewModel.setRecordsTypeFilter(key) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.Black else TrisaktiTextSecondary
                            )
                        }
                    }
                }
            }

            // Period Filter Bar: Today | This Week | This Month | Custom Date | All Time
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TimePeriod.values().forEach { period ->
                        val isSelected = selectedPeriod == period
                        val label = if (period == TimePeriod.CUSTOM && customRecordsDate != null) {
                            "Date: $customRecordsDate"
                        } else {
                            period.displayName
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) TrisaktiGold else TrisaktiCardBg)
                                .border(
                                    1.dp,
                                    if (isSelected) TrisaktiGold else TrisaktiCardBorder,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    if (period == TimePeriod.CUSTOM) {
                                        showCustomDatePicker = true
                                    } else {
                                        viewModel.setRecordsPeriod(period)
                                    }
                                }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (period == TimePeriod.CUSTOM) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.Black else TrisaktiTextSecondary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.Black else TrisaktiTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Financial Summary Card for Current Selection
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(TrisaktiCardBg)
                        .border(1.dp, TrisaktiCardBorder, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "PERIOD SUMMARY (${selectedPeriod.displayName.uppercase()})",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TrisaktiGold,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Net Profit: ${FinancialCalculator.formatNpr(netProfit)}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (netProfit >= 0) TrisaktiEmerald else TrisaktiCoral
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(TrisaktiBackground)
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${unifiedRecords.size} Records",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TrisaktiTextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // 3-part metric breakdown
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Total Sales", fontSize = 10.sp, color = TrisaktiTextSecondary)
                                Text(
                                    FinancialCalculator.formatNpr(totalSales),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TrisaktiGold
                                )
                            }
                            Column {
                                Text("Product Cost", fontSize = 10.sp, color = TrisaktiTextSecondary)
                                Text(
                                    FinancialCalculator.formatNpr(totalCost),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TrisaktiTextSecondary
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Total Expenses", fontSize = 10.sp, color = TrisaktiTextSecondary)
                                Text(
                                    FinancialCalculator.formatNpr(totalExpenses),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TrisaktiCoral
                                )
                            }
                        }
                    }
                }
            }

            // Transactions Header
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TRANSACTIONS LIST",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = TrisaktiTextSecondary,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "Tap for full details",
                        fontSize = 11.sp,
                        color = TrisaktiTextMuted
                    )
                }
            }

            // Transactions Stream
            if (unifiedRecords.isEmpty()) {
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
                                imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                                contentDescription = null,
                                tint = TrisaktiTextMuted,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No records found for ${selectedPeriod.displayName.lowercase()}.",
                                fontSize = 14.sp,
                                color = TrisaktiTextMuted,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = onOpenAddSale,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("+ Add Sale", color = TrisaktiGold, fontSize = 12.sp)
                                }
                                OutlinedButton(
                                    onClick = onOpenAddExpense,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("+ Add Expense", color = TrisaktiCoral, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            } else {
                items(unifiedRecords, key = {
                    when (it) {
                        is RecordItem.Sale -> "sale_${it.sale.id}"
                        is RecordItem.Expense -> "expense_${it.expense.id}"
                    }
                }) { item ->
                    CompactRecordCard(
                        item = item,
                        onClick = { detailedRecord = item },
                        onEdit = {
                            when (item) {
                                is RecordItem.Sale -> onOpenEditSale(item.sale)
                                is RecordItem.Expense -> onOpenEditExpense(item.expense)
                            }
                        },
                        onDelete = {
                            when (item) {
                                is RecordItem.Sale -> saleToDelete = item.sale
                                is RecordItem.Expense -> expenseToDelete = item.expense
                            }
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // Custom Date Picker Dialog
        if (showCustomDatePicker) {
            var inputDate by remember { mutableStateOf(customRecordsDate ?: todayStr) }
            var error by remember { mutableStateOf<String?>(null) }

            AlertDialog(
                onDismissRequest = { showCustomDatePicker = false },
                containerColor = TrisaktiSurface,
                title = {
                    Text("Filter by Specific Date", color = TrisaktiTextPrimary, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column {
                        Text(
                            "Enter the date you wish to view (YYYY-MM-DD):",
                            fontSize = 12.sp,
                            color = TrisaktiTextSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = inputDate,
                            onValueChange = { inputDate = it; error = null },
                            placeholder = { Text("2026-09-20", color = TrisaktiTextMuted) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TrisaktiTextPrimary,
                                unfocusedTextColor = TrisaktiTextPrimary,
                                focusedBorderColor = TrisaktiGold,
                                unfocusedBorderColor = TrisaktiCardBorder
                            )
                        )
                        if (error != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(error!!, color = TrisaktiCoral, fontSize = 11.sp)
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (inputDate.isBlank()) {
                                error = "Please enter a valid date."
                                return@TextButton
                            }
                            viewModel.setCustomRecordsDate(inputDate.trim())
                            showCustomDatePicker = false
                        }
                    ) {
                        Text("Apply Date", color = TrisaktiGold, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCustomDatePicker = false }) {
                        Text("Cancel", color = TrisaktiTextSecondary)
                    }
                }
            )
        }

        // Detailed Record View Dialog
        if (detailedRecord != null) {
            RecordDetailDialog(
                item = detailedRecord!!,
                onDismiss = { detailedRecord = null },
                onEdit = {
                    val record = detailedRecord!!
                    detailedRecord = null
                    when (record) {
                        is RecordItem.Sale -> onOpenEditSale(record.sale)
                        is RecordItem.Expense -> onOpenEditExpense(record.expense)
                    }
                },
                onDelete = {
                    val record = detailedRecord!!
                    detailedRecord = null
                    when (record) {
                        is RecordItem.Sale -> saleToDelete = record.sale
                        is RecordItem.Expense -> expenseToDelete = record.expense
                    }
                }
            )
        }

        // Delete Sale Confirmation Dialog
        if (saleToDelete != null) {
            AlertDialog(
                onDismissRequest = { saleToDelete = null },
                containerColor = TrisaktiSurface,
                title = { Text("Delete Sale Record?", color = TrisaktiTextPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "Are you sure you want to delete this sale of ${FinancialCalculator.formatNpr(saleToDelete!!.amount)} on ${saleToDelete!!.date}? This action cannot be undone.",
                        color = TrisaktiTextSecondary
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteSale(saleToDelete!!)
                            saleToDelete = null
                        }
                    ) {
                        Text("Delete", color = TrisaktiCoral, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { saleToDelete = null }) {
                        Text("Cancel", color = TrisaktiTextPrimary)
                    }
                }
            )
        }

        // Delete Expense Confirmation Dialog
        if (expenseToDelete != null) {
            AlertDialog(
                onDismissRequest = { expenseToDelete = null },
                containerColor = TrisaktiSurface,
                title = { Text("Delete Expense Record?", color = TrisaktiTextPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "Are you sure you want to delete this expense of ${FinancialCalculator.formatNpr(expenseToDelete!!.amount)} (${expenseToDelete!!.category}) on ${expenseToDelete!!.date}? This action cannot be undone.",
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

/**
 * Mobile-first compact transaction card matching exact specification:
 * SALE / EXPENSE badge
 * Rs. 4,500
 * Date, Time / Payment
 * Compact actions (View details, Edit, Delete)
 */
@Composable
private fun CompactRecordCard(
    item: RecordItem,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSale = item is RecordItem.Sale
    val badgeColor = if (isSale) TrisaktiGold else TrisaktiCoral

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(TrisaktiCardBg)
            .border(1.dp, TrisaktiCardBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Column {
            // Header Row: Type Badge + Date / Payment info + Action Icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(badgeColor.copy(alpha = 0.18f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (isSale) "SALE" else "EXPENSE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = badgeColor,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    val subtitle = if (isSale) {
                        "${FinancialCalculator.formatDisplayDate(item.date)} • ${(item as RecordItem.Sale).paymentType}"
                    } else {
                        "${FinancialCalculator.formatDisplayDate(item.date)} • ${(item as RecordItem.Expense).category}"
                    }

                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = TrisaktiTextSecondary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = TrisaktiTextSecondary,
                            modifier = Modifier.size(16.dp)
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

            Spacer(modifier = Modifier.height(6.dp))

            // Main Amount Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = FinancialCalculator.formatNpr(item.amount),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isSale) TrisaktiTextPrimary else TrisaktiCoral
                )

                if (isSale) {
                    val saleItem = item as RecordItem.Sale
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(TrisaktiEmerald.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Profit: +${FinancialCalculator.formatNpr(saleItem.profit)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TrisaktiEmerald
                        )
                    }
                } else {
                    val expItem = item as RecordItem.Expense
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(TrisaktiCoral.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = expItem.category,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TrisaktiCoral
                        )
                    }
                }
            }

            // Description
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.description.ifBlank { if (isSale) "Counter sale" else "Operating expense" },
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TrisaktiTextSecondary,
                maxLines = 1
            )

            // Product Cost if sale
            if (isSale) {
                val saleItem = item as RecordItem.Sale
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Product Cost: ${FinancialCalculator.formatNpr(saleItem.productCost)}",
                        fontSize = 11.sp,
                        color = TrisaktiTextMuted
                    )
                    Text(
                        text = "View details →",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TrisaktiGold
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "View details →",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TrisaktiGold
                    )
                }
            }
        }
    }
}

/**
 * Full details dialog showing all transaction attributes cleanly.
 */
@Composable
private fun RecordDetailDialog(
    item: RecordItem,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isSale = item is RecordItem.Sale

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = TrisaktiSurface),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isSale) TrisaktiGold.copy(alpha = 0.5f) else TrisaktiCoral.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isSale) "SALE RECORD DETAILS" else "EXPENSE RECORD DETAILS",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isSale) TrisaktiGold else TrisaktiCoral,
                        letterSpacing = 0.5.sp
                    )

                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TrisaktiTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Detail Attributes
                DetailRow(label = "Date", value = FinancialCalculator.formatDisplayDate(item.date))
                DetailRow(label = "Amount", value = FinancialCalculator.formatNpr(item.amount), isHighlight = true)

                if (isSale) {
                    val sale = (item as RecordItem.Sale)
                    DetailRow(label = "Product Cost (COGS)", value = FinancialCalculator.formatNpr(sale.productCost))
                    DetailRow(
                        label = "Estimated Gross Profit",
                        value = "+${FinancialCalculator.formatNpr(sale.profit)}",
                        highlightColor = TrisaktiEmerald
                    )
                    DetailRow(label = "Payment Method", value = sale.paymentType)
                } else {
                    val exp = (item as RecordItem.Expense)
                    DetailRow(label = "Category", value = exp.category)
                }

                DetailRow(label = "Description", value = item.description.ifBlank { "None" })
                if (item.notes.isNotBlank()) {
                    DetailRow(label = "Notes", value = item.notes)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons: Edit and Delete
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Edit", color = TrisaktiTextPrimary, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onDelete,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TrisaktiCoral, contentColor = Color.White)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Delete", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    isHighlight: Boolean = false,
    highlightColor: Color = TrisaktiTextPrimary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = TrisaktiTextSecondary)
        Text(
            text = value,
            fontSize = if (isHighlight) 16.sp else 13.sp,
            fontWeight = if (isHighlight) FontWeight.Black else FontWeight.SemiBold,
            color = if (isHighlight) highlightColor else TrisaktiTextPrimary
        )
    }
}

package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.entity.ExpenseEntity
import com.example.data.entity.SaleEntity
import com.example.ui.dialogs.AddCreditDialog
import com.example.ui.dialogs.AddCustomerDialog
import com.example.ui.dialogs.AddExpenseDialog
import com.example.ui.dialogs.AddSaleDialog
import com.example.ui.dialogs.DailyClosingDialog
import com.example.ui.dialogs.RecordPaymentDialog
import com.example.ui.screens.CreditScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MandatoryPasswordChangeScreen
import com.example.ui.screens.RecordsScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.TrisaktiBackground
import com.example.ui.theme.TrisaktiCardBg
import com.example.ui.theme.TrisaktiCardBorder
import com.example.ui.theme.TrisaktiCoral
import com.example.ui.theme.TrisaktiEmerald
import com.example.ui.theme.TrisaktiGold
import com.example.ui.theme.TrisaktiSurface
import com.example.ui.theme.TrisaktiTextPrimary
import com.example.ui.theme.TrisaktiTextSecondary
import com.example.ui.theme.TrisaktiTradersTheme
import com.example.ui.viewmodel.TrisaktiViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

enum class AppDestination(val title: String, val icon: ImageVector, val testTag: String) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard, "nav_dashboard"),
    RECORDS("Records", Icons.AutoMirrored.Filled.ReceiptLong, "nav_records"),
    CREDIT("Credit", Icons.Default.People, "nav_credit"),
    REPORTS("Reports", Icons.Default.Assessment, "nav_reports"),
    SETTINGS("Settings", Icons.Default.Settings, "nav_settings")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrisaktiTradersTheme {
                TrisaktiApp()
            }
        }
    }
}

@Composable
fun TrisaktiApp(
    viewModel: TrisaktiViewModel = viewModel()
) {
    var currentDestination by remember { mutableStateOf(AppDestination.DASHBOARD) }

    // Dialog control states
    var showAddSaleDialog by remember { mutableStateOf(false) }
    var saleToEdit by remember { mutableStateOf<SaleEntity?>(null) }
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var expenseToEdit by remember { mutableStateOf<ExpenseEntity?>(null) }
    var showAddCreditDialog by remember { mutableStateOf(false) }
    var preselectedCreditCustId by remember { mutableStateOf<Long?>(null) }
    var showRecordPaymentDialog by remember { mutableStateOf(false) }
    var preselectedPaymentCustId by remember { mutableStateOf<Long?>(null) }
    var showDailyClosingDialog by remember { mutableStateOf(false) }
    var showAddCustomerDialog by remember { mutableStateOf(false) }

    val customers by viewModel.allCustomers.collectAsStateWithLifecycle()
    val customerBalances by viewModel.customerBalances.collectAsStateWithLifecycle()
    val todaySummary by viewModel.todaySummary.collectAsStateWithLifecycle()
    val selectedCustomer by viewModel.selectedCustomer.collectAsStateWithLifecycle()

    // Proper back-button behavior: Customer Ledger -> Credit List -> Dashboard -> Exit
    BackHandler(enabled = selectedCustomer != null || currentDestination != AppDestination.DASHBOARD) {
        if (selectedCustomer != null) {
            viewModel.selectCustomer(null)
        } else if (currentDestination != AppDestination.DASHBOARD) {
            currentDestination = AppDestination.DASHBOARD
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()

    // Listen to ViewModel message notifications
    LaunchedEffect(Unit) {
        viewModel.messageEvents.collectLatest { msg ->
            scope.launch {
                snackbarHostState.showSnackbar(msg)
            }
        }
    }

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    if (!isLoggedIn) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(TrisaktiBackground)
                .safeDrawingPadding()
        ) {
            LoginScreen(viewModel = viewModel)
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(androidx.compose.ui.Alignment.BottomCenter)
            )
        }
        return
    }

    if (currentUser?.requiresPasswordChange == true) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(TrisaktiBackground)
                .safeDrawingPadding()
        ) {
            MandatoryPasswordChangeScreen(viewModel = viewModel, user = currentUser!!)
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(androidx.compose.ui.Alignment.BottomCenter)
            )
        }
        return
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("app_scaffold"),
        containerColor = TrisaktiBackground,
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = 70.dp)
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(TrisaktiSurface),
                containerColor = TrisaktiSurface,
                tonalElevation = 8.dp
            ) {
                AppDestination.values().forEach { destination ->
                    val isSelected = currentDestination == destination
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (currentDestination == AppDestination.CREDIT && destination == AppDestination.CREDIT) {
                                // reset customer selection if reclicking
                                viewModel.selectCustomer(null)
                            }
                            currentDestination = destination
                        },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.title,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = destination.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = TrisaktiGold,
                            indicatorColor = TrisaktiGold,
                            unselectedIconColor = TrisaktiTextSecondary,
                            unselectedTextColor = TrisaktiTextSecondary
                        ),
                        modifier = Modifier.testTag(destination.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            when (currentDestination) {
                AppDestination.DASHBOARD -> DashboardScreen(
                    viewModel = viewModel,
                    onOpenAddSale = {
                        saleToEdit = null
                        showAddSaleDialog = true
                    },
                    onOpenAddExpense = {
                        expenseToEdit = null
                        showAddExpenseDialog = true
                    },
                    onOpenAddCredit = {
                        preselectedCreditCustId = null
                        showAddCreditDialog = true
                    },
                    onOpenRecordPayment = {
                        preselectedPaymentCustId = null
                        showRecordPaymentDialog = true
                    },
                    onOpenDailyClosing = { showDailyClosingDialog = true },
                    onNavigateToRecords = { currentDestination = AppDestination.RECORDS },
                    onNavigateToCredit = { currentDestination = AppDestination.CREDIT }
                )

                AppDestination.RECORDS -> RecordsScreen(
                    viewModel = viewModel,
                    onOpenAddSale = {
                        saleToEdit = null
                        showAddSaleDialog = true
                    },
                    onOpenEditSale = { sale ->
                        saleToEdit = sale
                        showAddSaleDialog = true
                    },
                    onOpenAddExpense = {
                        expenseToEdit = null
                        showAddExpenseDialog = true
                    },
                    onOpenEditExpense = { expense ->
                        expenseToEdit = expense
                        showAddExpenseDialog = true
                    }
                )

                AppDestination.CREDIT -> CreditScreen(
                    viewModel = viewModel,
                    onOpenAddCustomer = { showAddCustomerDialog = true },
                    onOpenAddCredit = { custId ->
                        preselectedCreditCustId = custId
                        showAddCreditDialog = true
                    },
                    onOpenRecordPayment = { custId ->
                        preselectedPaymentCustId = custId
                        showRecordPaymentDialog = true
                    }
                )

                AppDestination.REPORTS -> ReportsScreen(
                    viewModel = viewModel
                )

                AppDestination.SETTINGS -> SettingsScreen(
                    viewModel = viewModel
                )
            }
        }
    }

    // Modal Dialogs
    if (showAddSaleDialog) {
        AddSaleDialog(
            initialSale = saleToEdit,
            onDismiss = {
                showAddSaleDialog = false
                saleToEdit = null
            },
            onConfirm = { date, amount, cost, type, desc, notes ->
                if (saleToEdit != null) {
                    viewModel.updateSale(
                        saleToEdit!!.copy(
                            date = date,
                            amount = amount,
                            productCost = cost,
                            paymentType = type,
                            description = desc,
                            notes = notes
                        )
                    )
                } else {
                    viewModel.addSale(date, amount, cost, type, desc, notes)
                }
                showAddSaleDialog = false
                saleToEdit = null
            }
        )
    }

    if (showAddExpenseDialog) {
        AddExpenseDialog(
            initialExpense = expenseToEdit,
            onDismiss = {
                showAddExpenseDialog = false
                expenseToEdit = null
            },
            onConfirm = { date, amount, category, desc, notes ->
                if (expenseToEdit != null) {
                    viewModel.updateExpense(
                        expenseToEdit!!.copy(
                            date = date,
                            amount = amount,
                            category = category,
                            description = desc,
                            notes = notes
                        )
                    )
                } else {
                    viewModel.addExpense(date, amount, category, desc, notes)
                }
                showAddExpenseDialog = false
                expenseToEdit = null
            }
        )
    }

    if (showAddCreditDialog) {
        AddCreditDialog(
            customers = customers,
            preselectedCustomerId = preselectedCreditCustId,
            onDismiss = { showAddCreditDialog = false },
            onConfirm = { custId, date, amount, desc ->
                viewModel.addCredit(custId, date, amount, desc)
            },
            onOpenAddCustomer = {
                showAddCustomerDialog = true
            }
        )
    }

    if (showRecordPaymentDialog) {
        RecordPaymentDialog(
            customers = customers,
            customerBalances = customerBalances,
            preselectedCustomerId = preselectedPaymentCustId,
            onDismiss = { showRecordPaymentDialog = false },
            onConfirm = { custId, date, amount, desc ->
                viewModel.recordPayment(custId, date, amount, desc)
            }
        )
    }

    if (showDailyClosingDialog) {
        DailyClosingDialog(
            summary = todaySummary,
            onDismiss = { showDailyClosingDialog = false },
            onConfirm = { date, openingCash, actualCash, notes ->
                viewModel.saveDailyClosing(date, openingCash, actualCash, notes)
            }
        )
    }

    if (showAddCustomerDialog) {
        AddCustomerDialog(
            onDismiss = { showAddCustomerDialog = false },
            onConfirm = { name, phone, notes, initialCredit ->
                viewModel.addCustomer(name, phone, notes, initialCredit)
            }
        )
    }
}

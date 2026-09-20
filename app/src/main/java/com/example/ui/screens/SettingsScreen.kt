package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.domain.FinancialCalculator
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

@Composable
fun SettingsScreen(
    viewModel: TrisaktiViewModel,
    modifier: Modifier = Modifier
) {
    var showClearDialog by remember { mutableStateOf(false) }
    var showReloadDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val todaySummary by viewModel.todaySummary.collectAsStateWithLifecycle()
    val allCustomers by viewModel.allCustomers.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TrisaktiBackground)
    ) {
        BackgroundWatermark()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("settings_screen"),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 100.dp)
        ) {
            item {
                TrisaktiTopBar(subtitle = "Settings & Shop Profile", showDemoBadge = false)
            }

            // SECTION 1: ACTIVE SESSION & ACCESS CONTROL
            item {
                SettingsSectionHeader("1. ACTIVE SESSION & ACCESS CONTROL")
                SettingsCard {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (currentUser?.avatarRes != null) {
                                Image(
                                    painter = painterResource(id = currentUser!!.avatarRes!!),
                                    contentDescription = currentUser?.name ?: "User",
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .border(1.5.dp, TrisaktiGold, CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(TrisaktiGold.copy(alpha = 0.2f))
                                        .border(1.5.dp, TrisaktiGold, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = TrisaktiGold,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = currentUser?.name ?: "Authorized User",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = TrisaktiTextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(TrisaktiEmerald.copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "AUTHENTICATED",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = TrisaktiEmerald
                                        )
                                    }
                                }
                                Text(
                                    text = currentUser?.role ?: "Authorized Business Member",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TrisaktiGold
                                )
                                Text(
                                    text = currentUser?.email ?: "",
                                    fontSize = 11.sp,
                                    color = TrisaktiTextSecondary
                                )
                            }
                        }

                        HorizontalDivider(color = TrisaktiCardBorder, thickness = 0.8.dp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            DetailItem(
                                label = "System Access",
                                value = "2 Accounts Only",
                                modifier = Modifier.weight(1f)
                            )
                            DetailItem(
                                label = "Ledger Mode",
                                value = "Shared Family Ledger",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showChangePasswordDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("btn_change_password"),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, TrisaktiGold.copy(alpha = 0.5f))
                            ) {
                                Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = TrisaktiGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("CHANGE PASSWORD", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TrisaktiGold)
                            }

                            Button(
                                onClick = { showLogoutDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("btn_logout"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = TrisaktiCoral.copy(alpha = 0.15f),
                                    contentColor = TrisaktiCoral
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, TrisaktiCoral.copy(alpha = 0.4f))
                            ) {
                                Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("LOG OUT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // SECTION 2: ACCOUNT & STORE PROFILE
            item {
                Spacer(modifier = Modifier.height(14.dp))
                SettingsSectionHeader("2. STORE PROFILE & REGISTRATION")
                SettingsCard {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(TrisaktiGold.copy(alpha = 0.15f))
                                    .border(1.dp, TrisaktiGold.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Store,
                                    contentDescription = null,
                                    tint = TrisaktiGold,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = "TRISAKTI TRADERS",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TrisaktiTextPrimary
                                )
                                Text(
                                    text = "Family Retail & Grocery Business",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TrisaktiGold
                                )
                                Text(
                                    text = "Kathmandu, Nepal • Est. 2026",
                                    fontSize = 11.sp,
                                    color = TrisaktiTextSecondary
                                )
                            }
                        }

                        HorizontalDivider(color = TrisaktiCardBorder, thickness = 0.8.dp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            DetailItem(label = "Currency", value = "NPR (Rs. / रू)", modifier = Modifier.weight(1f))
                            DetailItem(label = "Ledger Mode", value = "Dual Cash & Udhaar", modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // SECTION 3: BUSINESS PREFERENCES & FINANCIAL STANDARDS
            item {
                Spacer(modifier = Modifier.height(14.dp))
                SettingsSectionHeader("3. BUSINESS PREFERENCES")
                SettingsCard {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        InfoRow(
                            icon = Icons.Default.Calculate,
                            title = "Standard Net Profit Calculation",
                            subtitle = "Estimated Profit = Total Sales - Cost of Goods Sold (COGS) - Operating Expenses.",
                            color = TrisaktiEmerald
                        )

                        HorizontalDivider(color = TrisaktiCardBorder, thickness = 0.8.dp)

                        InfoRow(
                            icon = Icons.Default.Tune,
                            title = "Counter Sales & Udhaar Standard",
                            subtitle = "Default payment is Cash Counter. Credit customer ledgers maintain full transaction history.",
                            color = TrisaktiGold
                        )

                        HorizontalDivider(color = TrisaktiCardBorder, thickness = 0.8.dp)

                        InfoRow(
                            icon = Icons.Default.AccountBalance,
                            title = "Daily Cash Closing Reconciliation",
                            subtitle = "Compare drawer balance against expected cash: Opening + Cash Sales + Repayments - Expenses.",
                            color = TrisaktiSky
                        )
                    }
                }
            }

            // SECTION 4: PRIVATE ACCESS & RLS SECURITY
            item {
                Spacer(modifier = Modifier.height(14.dp))
                SettingsSectionHeader("4. PRIVATE ACCESS & RLS SECURITY")
                SettingsCard {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        InfoRow(
                            icon = Icons.Default.VerifiedUser,
                            title = "Strict 2-Account Authorization",
                            subtitle = "Exclusively authorized for Sandesh Bajgai and Arjun Prasad Bajgai. Public self-registration is permanently disabled. One-time initial passwords must be replaced.",
                            color = TrisaktiEmerald
                        )

                        HorizontalDivider(color = TrisaktiCardBorder, thickness = 0.8.dp)

                        InfoRow(
                            icon = Icons.Default.Security,
                            title = "Database Row Level Security (RLS)",
                            subtitle = "All tables enforce RLS policies. Anonymous users and arbitrary client user_ids have zero access to private records.",
                            color = TrisaktiGold
                        )

                        HorizontalDivider(color = TrisaktiCardBorder, thickness = 0.8.dp)

                        InfoRow(
                            icon = Icons.Default.Storage,
                            title = "Zero Secrets in APK & Local Sandboxing",
                            subtitle = "No database passwords or service-role keys are compiled into the application bundle. Private ledger state is wiped on logout.",
                            color = TrisaktiSky
                        )
                    }
                }
            }

            // SECTION 5: DATA MANAGEMENT & BACKUPS
            item {
                Spacer(modifier = Modifier.height(14.dp))
                SettingsSectionHeader("5. DATA & BACKUP MANAGEMENT")
                SettingsCard {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Demo & Testing Dataset",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TrisaktiTextPrimary
                                )
                                Text(
                                    text = "Preloaded retail sales, product costs, operating expenses, and customer credit ledger.",
                                    fontSize = 11.sp,
                                    color = TrisaktiTextSecondary
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(TrisaktiEmerald.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "ACTIVE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TrisaktiEmerald
                                )
                            }
                        }

                        // Export / Share Summary Action
                        OutlinedButton(
                            onClick = {
                                val exportText = """
*TRISAKTI TRADERS - Full Store Ledger Backup*
Export Date: ${FinancialCalculator.todayDateString()}
━━━━━━━━━━━━━━━━━━━━━━━━━━
Today's Financial Status:
• Total Sales: ${FinancialCalculator.formatNpr(todaySummary.totalSales)}
• Product Cost: ${FinancialCalculator.formatNpr(todaySummary.totalProductCost)}
• Total Expenses: ${FinancialCalculator.formatNpr(todaySummary.totalExpenses)}
• Estimated Net Profit: ${FinancialCalculator.formatNpr(todaySummary.estimatedProfit)}
• Credit Outstanding (Udhaar): ${FinancialCalculator.formatNpr(todaySummary.outstandingCredit)}
• Active Credit Customers: ${allCustomers.size} parties
━━━━━━━━━━━━━━━━━━━━━━━━━━
Secure local ledger backup from Trisakti Traders App.
                                """.trimIndent()

                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, exportText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Export Business Ledger"))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("btn_export_all_data"),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, TrisaktiSky.copy(alpha = 0.6f))
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = TrisaktiSky, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export / Share Ledger Summary", color = TrisaktiSky, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { showReloadDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("btn_reload_demo_data"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = TrisaktiGold, contentColor = Color.Black)
                            ) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Reload Demo", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = { showClearDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("btn_clear_all_data"),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, TrisaktiCoral.copy(alpha = 0.6f))
                            ) {
                                Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = null, tint = TrisaktiCoral, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Clear Data", color = TrisaktiCoral, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // SECTION 6: ABOUT TRISAKTI TRADERS & DEVELOPER ATTRIBUTION
            item {
                Spacer(modifier = Modifier.height(14.dp))
                SettingsSectionHeader("6. ABOUT TRISAKTI TRADERS")
                SettingsCard {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // Purpose Card
                        Column {
                            Text(
                                text = "About the Application",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = TrisaktiTextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "The application was designed and built to help manage daily business records with simplicity, speed, and clarity. Tailored for grocery and retail trading.",
                                fontSize = 12.sp,
                                color = TrisaktiTextSecondary,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                FocusPillar("Simple, uncluttered business management")
                                FocusPillar("Fast sales & expense tracking with COGS")
                                FocusPillar("Customer credit (Udhaar) ledger & repayments")
                                FocusPillar("Clear, understandable financial records & profit")
                            }
                        }

                        HorizontalDivider(color = TrisaktiCardBorder, thickness = 0.8.dp)

                        // Developer Profile Card: Built by Sandesh Bajgai
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Professional photo frame (64dp, circular mask, 1.5dp subtle border)
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(TrisaktiSurface)
                                    .border(1.5.dp, TrisaktiGold.copy(alpha = 0.7f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.sandesh_avatar),
                                    contentDescription = "Sandesh Bajgai - Designer & Developer",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = "Built by Sandesh Bajgai",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TrisaktiTextPrimary
                                )
                                Text(
                                    text = "Designer & Developer",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TrisaktiGold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Crafted for Trisakti Traders to bring clarity and effortless bookkeeping to family retail stores.",
                                    fontSize = 11.sp,
                                    color = TrisaktiTextSecondary,
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        HorizontalDivider(color = TrisaktiCardBorder, thickness = 0.8.dp)

                        // Technical Specs Summary
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("App Version", fontSize = 11.sp, color = TrisaktiTextMuted)
                                Text("1.0.0 (Production Stable)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TrisaktiTextSecondary)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Tech Stack", fontSize = 11.sp, color = TrisaktiTextMuted)
                                Text("Kotlin • Jetpack Compose • M3", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TrisaktiTextSecondary)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Database", fontSize = 11.sp, color = TrisaktiTextMuted)
                                Text("Android Room SQLite (ACID)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TrisaktiTextSecondary)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Data Security", fontSize = 11.sp, color = TrisaktiTextMuted)
                                Text("100% On-Device Private", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TrisaktiEmerald)
                            }
                        }
                    }
                }
            }
        }

        // Change Password Dialog
        if (showChangePasswordDialog) {
            var currentPwd by remember { mutableStateOf("") }
            var newPwd by remember { mutableStateOf("") }
            var confirmPwd by remember { mutableStateOf("") }
            var isCurrentVisible by remember { mutableStateOf(false) }
            var isNewVisible by remember { mutableStateOf(false) }
            var errorMsg by remember { mutableStateOf<String?>(null) }
            var isSubmitting by remember { mutableStateOf(false) }

            AlertDialog(
                onDismissRequest = { showChangePasswordDialog = false },
                containerColor = TrisaktiSurface,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = TrisaktiGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Change Security Password", color = TrisaktiTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            "Update access credentials for: ${currentUser?.name ?: ""}",
                            color = TrisaktiTextSecondary,
                            fontSize = 12.sp
                        )

                        OutlinedTextField(
                            value = currentPwd,
                            onValueChange = { currentPwd = it; errorMsg = null },
                            label = { Text("Current Password", fontSize = 12.sp) },
                            visualTransformation = if (isCurrentVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                IconButton(onClick = { isCurrentVisible = !isCurrentVisible }, modifier = Modifier.size(48.dp)) {
                                    Icon(
                                        imageVector = if (isCurrentVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = TrisaktiTextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_settings_current_pwd")
                        )

                        OutlinedTextField(
                            value = newPwd,
                            onValueChange = { newPwd = it; errorMsg = null },
                            label = { Text("New Password (min 4 chars)", fontSize = 12.sp) },
                            visualTransformation = if (isNewVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                IconButton(onClick = { isNewVisible = !isNewVisible }, modifier = Modifier.size(48.dp)) {
                                    Icon(
                                        imageVector = if (isNewVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = TrisaktiTextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_settings_new_pwd")
                        )

                        OutlinedTextField(
                            value = confirmPwd,
                            onValueChange = { confirmPwd = it; errorMsg = null },
                            label = { Text("Confirm New Password", fontSize = 12.sp) },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_settings_confirm_pwd")
                        )

                        if (errorMsg != null) {
                            Text(errorMsg!!, color = TrisaktiCoral, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (currentPwd.isBlank()) {
                                errorMsg = "Please enter your current password."
                                return@Button
                            }
                            if (newPwd.length < 4) {
                                errorMsg = "New password must be at least 4 characters/digits."
                                return@Button
                            }
                            if (newPwd != confirmPwd) {
                                errorMsg = "New passwords do not match."
                                return@Button
                            }
                            if (newPwd == currentPwd) {
                                errorMsg = "New password cannot be identical to current password."
                                return@Button
                            }

                            isSubmitting = true
                            val result = viewModel.changePassword(currentPwd, newPwd)
                            isSubmitting = false
                            if (result.isSuccess) {
                                showChangePasswordDialog = false
                            } else {
                                errorMsg = result.exceptionOrNull()?.message ?: "Failed to update password."
                            }
                        },
                        enabled = !isSubmitting,
                        colors = ButtonDefaults.buttonColors(containerColor = TrisaktiGold, contentColor = Color.Black)
                    ) {
                        Text(if (isSubmitting) "Updating..." else "Save Password", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showChangePasswordDialog = false }) {
                        Text("Cancel", color = TrisaktiTextPrimary)
                    }
                }
            )
        }

        // Logout Confirmation Dialog
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                containerColor = TrisaktiSurface,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = TrisaktiCoral)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Log Out of Business Session?", color = TrisaktiTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                },
                text = {
                    Text(
                        "You will be securely signed out of Trisakti Traders. All active application session state will be cleared. You must enter your authorized security passkey to re-open the ledger.",
                        color = TrisaktiTextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.logout()
                            showLogoutDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TrisaktiCoral, contentColor = Color.White)
                    ) {
                        Text("Log Out", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("Cancel", color = TrisaktiTextPrimary)
                    }
                }
            )
        }

        // Clear Confirmation Dialog (Destructive action protection)
        if (showClearDialog) {
            AlertDialog(
                onDismissRequest = { showClearDialog = false },
                containerColor = TrisaktiSurface,
                title = { Text("Clear All Records?", color = TrisaktiTextPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "This will wipe all sales, expenses, customers, and daily closings so you can start with a clean slate. You can reload demo data at any time from this screen.",
                        color = TrisaktiTextSecondary
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.clearAllData()
                            showClearDialog = false
                        }
                    ) {
                        Text("Clear All Data", color = TrisaktiCoral, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDialog = false }) {
                        Text("Cancel", color = TrisaktiTextPrimary)
                    }
                }
            )
        }

        // Reload Demo Data Confirmation Dialog
        if (showReloadDialog) {
            AlertDialog(
                onDismissRequest = { showReloadDialog = false },
                containerColor = TrisaktiSurface,
                title = { Text("Reload Demo Data?", color = TrisaktiTextPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "This will reset all records and reload the complete realistic demo dataset for Trisakti Traders (Ram, Shyam, Hari credit ledgers, daily sales, and operating expenses).",
                        color = TrisaktiTextSecondary
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.reloadDemoData()
                            showReloadDialog = false
                        }
                    ) {
                        Text("Reload Demo", color = TrisaktiGold, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showReloadDialog = false }) {
                        Text("Cancel", color = TrisaktiTextPrimary)
                    }
                }
            )
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = label, fontSize = 11.sp, color = TrisaktiTextMuted)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TrisaktiTextPrimary)
    }
}

@Composable
private fun FocusPillar(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(TrisaktiEmerald)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontSize = 12.sp, color = TrisaktiTextPrimary)
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Black,
        color = TrisaktiTextSecondary,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(TrisaktiCardBg)
            .border(1.dp, TrisaktiCardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color
) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(34.dp)
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
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TrisaktiTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TrisaktiTextSecondary,
                lineHeight = 16.sp
            )
        }
    }
}


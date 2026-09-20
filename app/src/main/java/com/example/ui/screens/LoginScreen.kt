package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.auth.SecurityManager
import com.example.domain.AuthUser
import com.example.ui.components.BackgroundWatermark
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

/**
 * High-security private business login screen.
 * Strictly limited to 2 authorized family accounts:
 * 1. Sandesh Bajgai (Developer & Business Partner)
 * 2. Father (Store Owner & Proprietor)
 *
 * Public registration is explicitly disabled.
 */
@Composable
fun LoginScreen(
    viewModel: TrisaktiViewModel,
    modifier: Modifier = Modifier
) {
    var selectedUser by remember { mutableStateOf<AuthUser>(SecurityManager.SANDESH_USER) }
    var passkey by remember { mutableStateOf("") }
    var isPasskeyVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    var showPolicyDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TrisaktiBackground)
    ) {
        BackgroundWatermark()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Brand Header & Shield
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(TrisaktiGold.copy(alpha = 0.15f))
                    .border(1.5.dp, TrisaktiGold.copy(alpha = 0.5f), RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = TrisaktiGold,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "TRISAKTI TRADERS",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = TrisaktiTextPrimary
            )

            Text(
                text = "Private Family Business Management Portal",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TrisaktiGold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Security Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(TrisaktiEmerald.copy(alpha = 0.12f))
                    .border(1.dp, TrisaktiEmerald.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = TrisaktiEmerald,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "RESTRICTED ACCESS • 2 AUTHORIZED ACCOUNTS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp,
                        color = TrisaktiEmerald
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Login Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_login_portal"),
                colors = CardDefaults.cardColors(containerColor = TrisaktiCardBg),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, TrisaktiCardBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "SELECT AUTHORIZED ACCOUNT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TrisaktiTextMuted,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Authorized Account Selector: Sandesh & Arjun
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AccountSelectTile(
                            user = SecurityManager.SANDESH_USER,
                            isSelected = selectedUser.id == SecurityManager.SANDESH_USER.id,
                            modifier = Modifier.weight(1f),
                            onSelect = {
                                selectedUser = SecurityManager.SANDESH_USER
                                errorMessage = null
                                passkey = ""
                            }
                        )

                        AccountSelectTile(
                            user = SecurityManager.ARJUN_USER,
                            isSelected = selectedUser.id == SecurityManager.ARJUN_USER.id,
                            modifier = Modifier.weight(1f),
                            onSelect = {
                                selectedUser = SecurityManager.ARJUN_USER
                                errorMessage = null
                                passkey = ""
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = TrisaktiCardBorder, thickness = 0.8.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Selected Account Badge
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(TrisaktiSurface, RoundedCornerShape(12.dp))
                            .border(1.dp, TrisaktiCardBorder, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (selectedUser.avatarRes != null) {
                            Image(
                                painter = painterResource(id = selectedUser.avatarRes!!),
                                contentDescription = selectedUser.name,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, TrisaktiGold, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(TrisaktiGold.copy(alpha = 0.2f))
                                    .border(1.dp, TrisaktiGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Store,
                                    contentDescription = null,
                                    tint = TrisaktiGold,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = selectedUser.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = TrisaktiTextPrimary
                            )
                            Text(
                                text = selectedUser.role,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TrisaktiGold
                            )
                            Text(
                                text = selectedUser.email,
                                fontSize = 11.sp,
                                color = TrisaktiTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Passkey Input Field
                    Text(
                        text = "SECURITY PASSKEY / PIN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TrisaktiTextMuted,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = passkey,
                        onValueChange = {
                            passkey = it
                            errorMessage = null
                        },
                        placeholder = {
                            Text(
                                "Enter your private passkey or PIN",
                                color = TrisaktiTextMuted,
                                fontSize = 13.sp
                            )
                        },
                        visualTransformation = if (isPasskeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (passkey.isNotBlank()) {
                                    val result = viewModel.login(selectedUser.id, passkey)
                                    if (result.isFailure) {
                                        errorMessage = "Invalid credentials or unauthorized account. Access restricted."
                                    }
                                }
                            }
                        ),
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = TrisaktiGold, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { isPasskeyVisible = !isPasskeyVisible },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = if (isPasskeyVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (isPasskeyVisible) "Hide passkey" else "Show passkey",
                                    tint = TrisaktiTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_login_passkey")
                    )

                    // Error Message
                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(TrisaktiCoral.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                                .border(1.dp, TrisaktiCoral.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = errorMessage!!,
                                color = TrisaktiCoral,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Login Action Button
                    Button(
                        onClick = {
                            if (isSubmitting) return@Button
                            if (passkey.isBlank()) {
                                errorMessage = "Please enter your security passkey."
                                return@Button
                            }
                            isSubmitting = true
                            val result = viewModel.login(selectedUser.id, passkey)
                            isSubmitting = false
                            if (result.isFailure) {
                                errorMessage = "Invalid credentials or unauthorized account. Access restricted to authorized business accounts."
                            }
                        },
                        enabled = !isSubmitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_submit_login"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TrisaktiGold,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isSubmitting) "VERIFYING..." else "LOGIN TO BUSINESS LEDGER",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Registration Disabled Notice Card (Mandatory security policy)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(TrisaktiSurface)
                    .border(1.dp, TrisaktiCardBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = TrisaktiSky,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Public Registration Disabled",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TrisaktiTextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Trisakti Traders is private proprietary software. Uninvited sign-ups and public accounts are prohibited. All database queries outside these 2 accounts are rejected by server RLS.",
                        fontSize = 11.sp,
                        color = TrisaktiTextSecondary,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    TextButton(
                        onClick = { showPolicyDialog = true },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = "View Security & Privacy Policy",
                            fontSize = 11.sp,
                            color = TrisaktiSky,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Trisakti Traders • Mobile First Enterprise 2026",
                fontSize = 11.sp,
                color = TrisaktiTextMuted
            )
        }

        // Security & Privacy Policy Dialog
        if (showPolicyDialog) {
            AlertDialog(
                onDismissRequest = { showPolicyDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = TrisaktiGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Private Business Access Policy", color = TrisaktiTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            "This system manages private financial data for Trisakti Traders (sales, product costs, operating expenses, cash drawer closing, customer credit ledgers).",
                            color = TrisaktiTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                        HorizontalDivider(color = TrisaktiCardBorder)
                        Text(
                            "1. Two Authorized Accounts Only:\n• Sandesh Bajgai (Co-Owner & System Developer)\n• Arjun Prasad Bajgai (Store Proprietor & Owner)",
                            color = TrisaktiTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "2. Zero Public Registration:\nSelf-registration is blocked at both client and database levels. Anonymous requests are blocked with 0 privileges.",
                            color = TrisaktiTextSecondary,
                            fontSize = 11.sp
                        )
                        Text(
                            "3. Database Level Row Security (RLS):\nDirect SQL and REST API requests are constrained strictly to the two authorized identities. Changing client-side user_id does not grant access.",
                            color = TrisaktiTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showPolicyDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = TrisaktiGold, contentColor = Color.Black)
                    ) {
                        Text("Understood", fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = TrisaktiCardBg,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
private fun AccountSelectTile(
    user: AuthUser,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit
) {
    val borderColor = if (isSelected) TrisaktiGold else TrisaktiCardBorder
    val bgColor = if (isSelected) TrisaktiGold.copy(alpha = 0.12f) else TrisaktiSurface

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(if (isSelected) 1.5.dp else 1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable { onSelect() }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (user.avatarRes != null) {
                Image(
                    painter = painterResource(id = user.avatarRes),
                    contentDescription = user.name,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.dp, if (isSelected) TrisaktiGold else Color.Transparent, CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) TrisaktiGold.copy(alpha = 0.3f) else TrisaktiCardBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = if (isSelected) TrisaktiGold else TrisaktiTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = user.name,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                color = if (isSelected) TrisaktiTextPrimary else TrisaktiTextSecondary,
                textAlign = TextAlign.Center
            )

            Text(
                text = if (user.id == SecurityManager.SANDESH_USER.id) "Co-Owner" else "Proprietor",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) TrisaktiGold else TrisaktiTextMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}

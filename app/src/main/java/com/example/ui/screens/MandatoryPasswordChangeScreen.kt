package com.example.ui.screens

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.AuthUser
import com.example.ui.components.BackgroundWatermark
import com.example.ui.theme.TrisaktiBackground
import com.example.ui.theme.TrisaktiCardBg
import com.example.ui.theme.TrisaktiCardBorder
import com.example.ui.theme.TrisaktiCoral
import com.example.ui.theme.TrisaktiGold
import com.example.ui.theme.TrisaktiSurface
import com.example.ui.theme.TrisaktiTextMuted
import com.example.ui.theme.TrisaktiTextPrimary
import com.example.ui.theme.TrisaktiTextSecondary
import com.example.ui.viewmodel.TrisaktiViewModel

/**
 * Mandatory first-time password setup screen.
 * Triggered automatically upon first successful login using the temporary initial passkey.
 * Enforces that initial temporary passwords (2009 / 1234) are permanently replaced before
 * granting access to confidential store records.
 */
@Composable
fun MandatoryPasswordChangeScreen(
    viewModel: TrisaktiViewModel,
    user: AuthUser,
    modifier: Modifier = Modifier
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isCurrentVisible by remember { mutableStateOf(false) }
    var isNewVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

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
            // Security Icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(TrisaktiGold.copy(alpha = 0.15f))
                    .border(1.5.dp, TrisaktiGold.copy(alpha = 0.5f), RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = TrisaktiGold,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "SECURITY REQUIREMENT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = TrisaktiGold
            )

            Text(
                text = "Change Initial Password",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = TrisaktiTextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Welcome ${user.name}. You are logged in with your initial temporary password. To protect Trisakti Traders business records, you must set a new private password before continuing.",
                fontSize = 12.sp,
                color = TrisaktiTextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_mandatory_password_change"),
                colors = CardDefaults.cardColors(containerColor = TrisaktiCardBg),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, TrisaktiCardBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Account context banner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(TrisaktiSurface, RoundedCornerShape(10.dp))
                            .border(1.dp, TrisaktiCardBorder, RoundedCornerShape(10.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = user.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TrisaktiTextPrimary
                            )
                            Text(
                                text = user.role,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TrisaktiGold
                            )
                            Text(
                                text = user.email,
                                fontSize = 11.sp,
                                color = TrisaktiTextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = TrisaktiCardBorder, thickness = 0.8.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Field 1: Current initial password
                    Text(
                        text = "CURRENT INITIAL PASSWORD",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TrisaktiTextMuted,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = {
                            currentPassword = it
                            errorMessage = null
                        },
                        placeholder = { Text("Enter current initial password", color = TrisaktiTextMuted, fontSize = 13.sp) },
                        visualTransformation = if (isCurrentVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = TrisaktiGold, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { isCurrentVisible = !isCurrentVisible },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = if (isCurrentVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = TrisaktiTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_current_password")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Field 2: New password
                    Text(
                        text = "NEW PRIVATE PASSWORD / PIN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TrisaktiTextMuted,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = {
                            newPassword = it
                            errorMessage = null
                        },
                        placeholder = { Text("Enter new strong password (min 4 chars)", color = TrisaktiTextMuted, fontSize = 13.sp) },
                        visualTransformation = if (isNewVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        leadingIcon = {
                            Icon(Icons.Default.Security, contentDescription = null, tint = TrisaktiGold, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { isNewVisible = !isNewVisible },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = if (isNewVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = TrisaktiTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_new_password")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Field 3: Confirm new password
                    Text(
                        text = "CONFIRM NEW PASSWORD",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TrisaktiTextMuted,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            errorMessage = null
                        },
                        placeholder = { Text("Re-enter new password", color = TrisaktiTextMuted, fontSize = 13.sp) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = TrisaktiGold, modifier = Modifier.size(18.dp))
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_confirm_password")
                    )

                    // Error warning
                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(TrisaktiCoral.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                .border(1.dp, TrisaktiCoral.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = TrisaktiCoral, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = errorMessage!!, color = TrisaktiCoral, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            if (currentPassword.isBlank()) {
                                errorMessage = "Please enter your current initial password."
                                return@Button
                            }
                            if (newPassword.isBlank() || newPassword.length < 4) {
                                errorMessage = "New password must be at least 4 characters/digits."
                                return@Button
                            }
                            if (newPassword != confirmPassword) {
                                errorMessage = "New passwords do not match."
                                return@Button
                            }
                            if (newPassword == currentPassword) {
                                errorMessage = "New password cannot be identical to initial password."
                                return@Button
                            }

                            isSubmitting = true
                            val result = viewModel.changePassword(currentPassword, newPassword)
                            isSubmitting = false
                            if (result.isFailure) {
                                errorMessage = result.exceptionOrNull()?.message ?: "Failed to update password."
                            }
                        },
                        enabled = !isSubmitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_save_new_password"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TrisaktiGold,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (isSubmitting) "Updating Password..." else "Save Password & Unlock App",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Log out / cancel
                    OutlinedButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_cancel_and_logout"),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TrisaktiCardBorder)
                    ) {
                        Text("Log Out of Session", color = TrisaktiTextSecondary, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

package com.dev.lokabudaya.pages.Profile.Menu

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dev.lokabudaya.R
import com.dev.lokabudaya.ScreenRoute
import com.dev.lokabudaya.pages.Auth.AuthState
import com.dev.lokabudaya.pages.Auth.AuthViewModel
import com.dev.lokabudaya.ui.theme.bigTextColor
import com.dev.lokabudaya.ui.theme.interBold
import com.dev.lokabudaya.ui.theme.poppinsSemiBold
import com.dev.lokabudaya.ui.theme.selectedCategoryColor
import com.dev.lokabudaya.ui.theme.smallTextColor

@Composable
fun PrivacyPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when(authState.value) {
            is AuthState.PasswordChangedWithSignOut -> {
                Toast.makeText(context, "Password changed successfully! Please login with your new password.", Toast.LENGTH_LONG).show()
                showChangePasswordDialog = false
                authViewModel.signout(context)
                navController.navigate("LoginPage") {
                    popUpTo("PrivacyPage") { inclusive = true }
                }
            }
            is AuthState.Error -> {
                Toast.makeText(context, (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }
    }

    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onConfirm = { currentPassword, newPassword, confirmPassword ->
                authViewModel.changePassword(
                    currentPassword = currentPassword,
                    newPassword = newPassword,
                    onSuccess = {
                    },
                    onError = { errorMessage ->
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        showChangePasswordDialog = false
                    }
                )
            },
            onDismiss = {
                showChangePasswordDialog = false
            },
            authViewModel = authViewModel
        )
    }

    Column(modifier = modifier.padding(16.dp)) {
        HeaderPrivacySection(navController)
        Spacer(modifier = Modifier.height(32.dp))
        PrivacySection(
            onChangePasswordClick = {
                showChangePasswordDialog = true
            }
        )
    }
}

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun HeaderPrivacySection(navController: NavController) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Back Icon",
                tint = bigTextColor,
                modifier = Modifier
                    .size(24.dp)
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null
                    ) {
                        navController.navigate(ScreenRoute.Profile.route)
                    }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Privacy & Security",
                fontSize = 24.sp,
                fontFamily = interBold,
                color = bigTextColor
            )
        }
    }
}

@Composable
fun PrivacySection(
    onChangePasswordClick: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onChangePasswordClick() }
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_privacy),
                contentDescription = "Privacy Icon",
                tint = smallTextColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Change Password",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = bigTextColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Change your password to secure your account if needed",
                    fontSize = 12.sp,
                    color = bigTextColor,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
            }
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_forward),
            contentDescription = "Forward Icon",
            tint = bigTextColor,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun ChangePasswordDialog(
    onConfirm: (String, String, String) -> Unit,
    onDismiss: () -> Unit,
    authViewModel: AuthViewModel
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isCurrentPasswordVisible by remember { mutableStateOf(false) }
    var isNewPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Change Password",
                fontFamily = interBold,
                fontSize = 18.sp
            )
        },
        text = {
            Column {
                Text(
                    text = "Enter your current password and new password. You will be signed out after the change.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Current Password") },
                    visualTransformation = if (isCurrentPasswordVisible)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isCurrentPasswordVisible = !isCurrentPasswordVisible }) {
                            Icon(
                                painter = painterResource(
                                    if (isCurrentPasswordVisible) R.drawable.ic_eyeclosed
                                    else R.drawable.ic_eye
                                ),
                                contentDescription = if (isCurrentPasswordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isProcessing,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = selectedCategoryColor,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = selectedCategoryColor,
                        unfocusedLabelColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    visualTransformation = if (isNewPasswordVisible)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isNewPasswordVisible = !isNewPasswordVisible }) {
                            Icon(
                                painter = painterResource(
                                    if (isNewPasswordVisible) R.drawable.ic_eyeclosed
                                    else R.drawable.ic_eye
                                ),
                                contentDescription = if (isNewPasswordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isProcessing,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = selectedCategoryColor,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = selectedCategoryColor,
                        unfocusedLabelColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm New Password") },
                    visualTransformation = if (isConfirmPasswordVisible)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                            Icon(
                                painter = painterResource(
                                    if (isConfirmPasswordVisible) R.drawable.ic_eyeclosed
                                    else R.drawable.ic_eye
                                ),
                                contentDescription = if (isConfirmPasswordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isProcessing,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = selectedCategoryColor,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = selectedCategoryColor,
                        unfocusedLabelColor = Color.Gray
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    when {
                        currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty() -> {
                            Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                        }
                        newPassword != confirmPassword -> {
                            Toast.makeText(context, "New passwords don't match", Toast.LENGTH_SHORT).show()
                        }
                        newPassword.length < 6 -> {
                            Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            isProcessing = true
                            onConfirm(currentPassword, newPassword, confirmPassword)
                        }
                    }
                },
                enabled = !isProcessing
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = selectedCategoryColor
                    )
                } else {
                    Text(color = selectedCategoryColor, text = "Confirm")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isProcessing
            ) {
                Text(color = Color.Gray, text = "Cancel")
            }
        }
    )
}
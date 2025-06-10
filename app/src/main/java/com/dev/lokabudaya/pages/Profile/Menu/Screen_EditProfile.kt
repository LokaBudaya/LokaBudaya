package com.dev.lokabudaya.pages.Profile.Menu

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TransformedText
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

@Composable
fun EditProfilePage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    val authState = authViewModel.authState.observeAsState()
    val userData = authViewModel.userData.observeAsState()
    val context = LocalContext.current

    var displayName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var currentPassword by remember { mutableStateOf("") }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var pendingUpdate by remember { mutableStateOf<(() -> Unit)?>(null) }

    LaunchedEffect(userData.value) {
        userData.value?.let { user ->
            displayName = user.displayName
            username = user.username
            email = user.email
            val profile = user.profile
            phoneNumber = profile["phonenumber"] as? String ?: ""
        }
    }

    LaunchedEffect(authState.value) {
        when(authState.value) {
            is AuthState.Authenticated -> {
                if (isLoading) {
                    val emailChanged = email != (userData.value?.email ?: "")
                    if (!emailChanged) {
                        isLoading = false
                    }
                }
            }
            is AuthState.EmailVerificationSentWithSignOut -> {
                Toast.makeText(context, "Email verification sent! Please verify your new email and login again.", Toast.LENGTH_LONG).show()
                isLoading = false
                showPasswordDialog = false

                authViewModel.signout(context)

                navController.navigate("LoginPage") {
                    popUpTo("EditProfilePage") { inclusive = true }
                }
            }
            is AuthState.EmailVerificationSent -> {
                isLoading = false
            }
            is AuthState.Error -> {
                Toast.makeText(context, (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
                isLoading = false
                showPasswordDialog = false
            }
            else -> Unit
        }
    }

    if (showPasswordDialog) {
        PasswordConfirmationDialog(
            onConfirm = { password ->
                currentPassword = password
                showPasswordDialog = false
                pendingUpdate?.invoke()
            },
            onDismiss = {
                showPasswordDialog = false
                isLoading = false
            },
            navController = navController,
            authViewModel = authViewModel
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        HeaderEditProfileSection(navController)

        Spacer(modifier = Modifier.height(32.dp))

        ProfilePictureSection()

        Spacer(modifier = Modifier.height(32.dp))

        EditProfileTextField(
            value = displayName,
            onValueChange = { displayName = it },
            label = "Display name",
            placeholder = "Enter your display name"
        )

        Spacer(modifier = Modifier.height(16.dp))

        EditProfileTextField(
            value = username,
            onValueChange = { username = it },
            label = "Username",
            placeholder = "Enter your username"
        )

        Spacer(modifier = Modifier.height(16.dp))

        EditProfileTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            placeholder = "Enter your email",
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(16.dp))

        PhoneNumberField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = "Phone number"
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val emailChanged = email != (userData.value?.email ?: "")
                val displayNameChanged = displayName != (userData.value?.displayName ?: "")
                val usernameChanged = username != (userData.value?.username ?: "")
                val phoneChanged = phoneNumber != ((userData.value?.profile?.get("phonenumber") as? String) ?: "")

                val otherFieldsChanged = displayNameChanged || usernameChanged || phoneChanged

                if (emailChanged) {
                    pendingUpdate = {
                        authViewModel.updateProfile(
                            displayName = displayName,
                            username = username,
                            email = email,
                            phoneNumber = phoneNumber,
                            currentEmail = userData.value?.email ?: "",
                            currentPassword = currentPassword
                        )
                    }
                    showPasswordDialog = true
                    isLoading = true
                } else if (otherFieldsChanged) {
                    isLoading = true
                    authViewModel.updateProfile(
                        displayName = displayName,
                        username = username,
                        email = email,
                        phoneNumber = phoneNumber,
                        currentEmail = userData.value?.email ?: "",
                        currentPassword = ""
                    )

                    Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    navController.navigate(ScreenRoute.Profile.route) {
                        popUpTo("EditProfilePage") { inclusive = true }
                    }
                } else {
                    Toast.makeText(context, "No changes detected", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = selectedCategoryColor
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = "Save",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun PasswordConfirmationDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Confirm Password",
                fontFamily = interBold
            )
        },
        text = {
            Column {
                Text(
                    text = "To change your email, please enter your current password.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Current Password") },
                    visualTransformation = if (isPasswordVisible)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                painter = painterResource(
                                    if (isPasswordVisible) R.drawable.ic_eyeclosed
                                    else R.drawable.ic_eye
                                ),
                                contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
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
                    isProcessing = true
                    onConfirm(password)
                },
                enabled = password.isNotEmpty() && !isProcessing
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

@Composable
fun ProfilePictureSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box {
            Image(
                painter = painterResource(id = R.drawable.img_banner),
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(selectedCategoryColor, CircleShape)
                    .align(Alignment.BottomEnd)
                    .clickable {
                        // TODO
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_camera),
                    contentDescription = "Change Picture",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Change profile picture",
            color = selectedCategoryColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable {
                // TODO
            }
        )
    }
}

@Composable
fun EditProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.LightGray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = selectedCategoryColor,
                unfocusedBorderColor = Color.LightGray,
                focusedLabelColor = selectedCategoryColor,
                unfocusedLabelColor = Color.Gray
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true
        )
    }
}

@Composable
fun PhoneNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    val numericRegex = Regex("[^0-9]")

    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = { input ->
                val stripped = numericRegex.replace(input, "")
                val limited = if (stripped.length >= 12) {
                    stripped.substring(0..11)
                } else {
                    stripped
                }
                onValueChange(limited)
            },
            placeholder = { Text("Enter your phone number", color = Color.LightGray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = selectedCategoryColor,
                unfocusedBorderColor = Color.LightGray,
                focusedLabelColor = selectedCategoryColor,
                unfocusedLabelColor = Color.Gray
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            visualTransformation = IndonesianPhoneVisualTransformation(),
            leadingIcon = {
                Text(
                    text = "+62 ",
                    color = Color.Black,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 12.dp)
                )
            },
            singleLine = true
        )
    }
}

class IndonesianPhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 12) text.text.substring(0..11) else text.text
        var out = ""

        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 2 || i == 6) out += "-"
        }

        return TransformedText(AnnotatedString(out), phoneNumberOffsetTranslator)
    }

    private val phoneNumberOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            return when {
                offset <= 3 -> offset
                offset <= 7 -> offset + 1
                else -> offset + 2
            }
        }

        override fun transformedToOriginal(offset: Int): Int {
            return when {
                offset <= 3 -> offset
                offset <= 8 -> offset - 1
                else -> offset - 2
            }
        }
    }
}

// Header Edit Profile Section
@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun HeaderEditProfileSection(navController: NavController) {
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
                text = "Edit Profile",
                fontSize = 24.sp,
                fontFamily = interBold,
                color = bigTextColor
            )
        }
    }
}
package com.example.fakestore.auth.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.fakestore.utils.FakeStoreTextField
import com.example.fakestore.utils.LoginOption
import com.example.fakestore.R
import com.example.fakestore.utils.SocialButtons
import com.example.fakestore.auth.AuthResult
import com.example.fakestore.ui.theme.Orange


@Composable
fun LogInScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val uiState by viewModel.loginState.collectAsState()

    // Handle side effects like showing Toast or navigation
    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthResult.Success -> {
                Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                navController.navigate("all_products") {
                    popUpTo("log_in") { inclusive = true }
                }
            }

            is AuthResult.Error -> {
                Toast.makeText(context, (uiState as AuthResult.Error).message, Toast.LENGTH_SHORT)
                    .show()
            }

            else -> Unit // No-op for other states
        }
    }

    Box {
        // Background Image
        Image(
            painter = painterResource(R.drawable.ic_auth_bg),
            contentDescription = null,
            modifier = modifier.fillMaxSize()
        )

        // Back Button
        IconButton(
            onClick = {
                navController.navigate("auth") {
                    launchSingleTop = true
                    popUpTo("auth") { inclusive = true }
                }
            },
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
            )
        }

        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f)) // Spacer to push content down

            Text(text = "Log In", fontSize = 45.sp)

            Spacer(Modifier.size(16.dp))

            // Email Field
            FakeStoreTextField(
                modifier = Modifier.fillMaxWidth(),
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") }
            )

            Spacer(Modifier.size(16.dp))

            // Password Field
            FakeStoreTextField(
                modifier = Modifier.fillMaxWidth(),
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = if (!isPasswordVisible) {
                    PasswordVisualTransformation()
                } else VisualTransformation.None,
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            painter = painterResource(
                                if (isPasswordVisible) R.drawable.ic_eye else R.drawable.eye_password_hide_svgrepo_com
                            ),
                            contentDescription = "Toggle Password Visibility"
                        )
                    }
                },
                singleLine = true
            )

            Spacer(Modifier.size(16.dp))

            // Forgot Password Button
            TextButton(onClick = { /* Navigate to Forgot Password Screen */ }) {
                Text(text = "Forgot password?", color = Orange)
            }

            Spacer(Modifier.size(16.dp))

            // Login Button
            Button(
                enabled = email.isNotEmpty() && password.isNotEmpty(),
                onClick = {
                    viewModel.loginUser(email, password)
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(200.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Orange)
            ) {
                when (uiState) {
                    is AuthResult.Loading -> CircularProgressIndicator(color = Color.White)
                    else -> Text(text = "Log In")
                }
            }

            Spacer(Modifier.size(16.dp))

            // Sign Up Option
            LoginOption(
                onClick = { navController.navigate("sign_up") },
                name = R.string.sign_up,
                text = R.string.do_not_have_account,
                colorName = Orange,
                colorText = Color.Black
            )

            Spacer(Modifier.size(16.dp))

            // Social Login Buttons
            SocialButtons(
                onClickGoogle = { /* Handle Google Login */ },
                onClickFacebook = { /* Handle Facebook Login */ },
                color = Color.Black
            )

            Spacer(Modifier.weight(1f)) // Spacer to push content up
        }
    }
}


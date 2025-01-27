package com.example.fakestore.auth.signup

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
fun SignUpScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewmodel: SignUpViewModel = hiltViewModel()
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val uiState by viewmodel.signUpState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        when (uiState) {
          is  AuthResult.Error -> {
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
            }
            is AuthResult.Success -> {
                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                navController.navigate("all_products") {
                    popUpTo(route = "sign_up"){
                        saveState=true
                    }
                    launchSingleTop=true
                }
            }
            else -> {}
        }
    }

    Box {
        Image(
            painter = painterResource(R.drawable.ic_auth_bg),
            contentDescription = null,
            modifier = modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(Modifier.weight(1f))
            Text(text = "Sign Up", fontSize = 45.sp)
            FakeStoreTextField(
                modifier = Modifier.fillMaxWidth(),
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") })
            Spacer(Modifier.size(16.dp))
            FakeStoreTextField(
                modifier = Modifier.fillMaxWidth(),
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") })
            Spacer(Modifier.size(16.dp))
            FakeStoreTextField(
                modifier = Modifier.fillMaxWidth(),
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = if (!isPasswordVisible) {
                    PasswordVisualTransformation()
                } else VisualTransformation.None,
                trailingIcon = {
                    IconButton(onClick = {
                        isPasswordVisible = !isPasswordVisible
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_eye),
                            contentDescription = null
                        )
                    }
                },
                singleLine = true
            )
            Spacer(Modifier.size(16.dp))

            Button(
                enabled = (fullName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()),
                onClick = {
                    viewmodel.signUpUser(fullName = fullName, email = email, password = password)
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(200.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Orange,
                )
            ) {
                when (uiState) {
                    is AuthResult.Loading -> {
                        CircularProgressIndicator()
                    }
                    else -> {
                        Text(text = "Sign Up")
                    }
                }
            }
            LoginOption(
                onClick = {
                    navController.navigate("log_in")
                },
                name = R.string.log_in,
                text = R.string.already_have_account,
                colorName = Orange,
                colorText = Color.Black
            )
            SocialButtons(onClickGoogle = {}, onClickFacebook = {}, color = Color.Black)
        }
    }
}


package com.thusith.recipebook

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.thusith.recipebook.data.local.UserDao
import com.thusith.recipebook.data.remote.AuthRepository
import com.thusith.recipebook.data.remote.RetrofitInstance
import com.thusith.recipebook.model.LoginRequest
import com.thusith.recipebook.utils.Resource
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Composable
fun LoginScreen(navController: NavHostController, userDao: UserDao) {
    //var email by remember { mutableStateOf(TextFieldValue()) }
    var email by remember { mutableStateOf("") }
    //var password by remember { mutableStateOf(TextFieldValue()) }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val apiService = remember { RetrofitInstance.api }
    val authRepository = remember { AuthRepository(apiService, userDao) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top

    ) {
        Text(text = "Sign In",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = colorResource(R.color.orange),
            modifier = Modifier.padding(40.dp).align(Alignment.CenterHorizontally)
        )

        TextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Email") },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        var passwordVisibility by remember{ mutableStateOf(false) }

        val icon = if (passwordVisibility)
            painterResource(id = R.drawable.visibility_on)
        else
            painterResource(id = R.drawable.visibility_off)

        TextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Password") },
            trailingIcon = {
                IconButton(onClick = {
                    passwordVisibility = !passwordVisibility
                }) {
                    Icon(painter = icon,
                        contentDescription = "visibility icon")
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
            ),
            visualTransformation = if (passwordVisibility) VisualTransformation.None
            else PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(), // Make the row fill the width
            horizontalArrangement = Arrangement.End // Align elements to the end
        ) {
            TextButton(
                onClick = {
                    navController.popBackStack()
                    navController.navigate("RegisterScreen")
                },
                modifier = Modifier
            ) {
                Text(
                    text = "Forgot password?",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.End,
                    color = colorResource(R.color.orange)
                )
            }
        }

        Button(onClick = {
            if (email.isBlank() || password.isBlank()) {
                errorMessage = "Please enter both email and password"
                return@Button
            }

            coroutineScope.launch {
                isLoading = true
                errorMessage = null

                val result = authRepository.login(LoginRequest(email, password))
                isLoading = false

                when (result) {
                    is Resource.Success<*> -> {
                        // Save user data if needed
                        navController.navigate("HomeScreen") {
                            popUpTo("LoginScreen") { inclusive = true }
                        }
                    }
                    is Resource.Error<*> -> {
                        errorMessage = result.message ?: "Login failed"
                    }
                }
            }
        },

            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 10.dp,
                pressedElevation = 6.dp
            ),

            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(30.dp)) {
            Text(text = "Sign In",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                textAlign = TextAlign.End
            )
        }

        Row(
            modifier = Modifier.padding(top = 2.dp, bottom = 52.dp).align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.Center
        ) {
            ClickableRegisterText(navController, onTextSelected = {})

        }

    }
}

@Composable
fun ClickableRegisterText(navController: NavHostController, onTextSelected: (String) -> Unit){
    val initialText = "Don't have an account? "
    val registerText = " Sign Up"

    val annotatedString = buildAnnotatedString {
        append(initialText)
        withStyle(style = SpanStyle(color = colorResource(R.color.orange))) {
            pushStringAnnotation(tag = registerText, annotation = registerText)
            append(registerText)
        }
    }

    ClickableText(text = annotatedString, onClick = {
        // navigation logic
        navController.popBackStack()
        navController.navigate("RegisterScreen")
    })
}
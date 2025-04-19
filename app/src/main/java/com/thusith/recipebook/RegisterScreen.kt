package com.thusith.recipebook

import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.thusith.recipebook.data.local.UserDao
import com.thusith.recipebook.data.remote.AuthRepository
import com.thusith.recipebook.data.remote.RetrofitInstance
import com.thusith.recipebook.model.RegisterRequest
import com.thusith.recipebook.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

@Composable
fun RegisterScreen(navController: NavHostController, userDao: UserDao) {
    var name by remember { mutableStateOf("") }
    // var email by remember { mutableStateOf(TextFieldValue()) }
    var email by remember { mutableStateOf("") }
    //var password by remember { mutableStateOf(TextFieldValue()) }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val context = LocalContext.current

    fun register(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        navController: NavHostController,
        authRepository: AuthRepository,
        coroutineScope: CoroutineScope
    ) {
        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank() || name.isBlank()) {
            Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        val request = RegisterRequest(email, password)

        coroutineScope.launch {
            when (val result = authRepository.register(
                name,
                email,
                RegisterRequest(email, password)

            )) {
                is Resource.Success -> {
                    Toast.makeText(context, "Registered successfully!", Toast.LENGTH_SHORT).show()
                    navController.navigate("LoginScreen")
                }

                is Resource.Error -> {
                    Log.e("REG_ERROR", result.message ?: "Unknown error")
                    Toast.makeText(
                        context,
                        "Failed: ${result.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                else -> Unit
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top

    ) {
        Text(
            text = "Sign Up",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = colorResource(R.color.orange),
            modifier = Modifier
                .padding(40.dp)
                .align(Alignment.CenterHorizontally)
        )

        // Enter Name
        TextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Name") },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

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

        var passwordVisibility by remember { mutableStateOf(false) }

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
                    Icon(
                        painter = icon,
                        contentDescription = "visibility icon"
                    )
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

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Confirm Password") },
            trailingIcon = {
                IconButton(onClick = {
                    passwordVisibility = !passwordVisibility
                }) {
                    Icon(
                        painter = icon,
                        contentDescription = "visibility icon"
                    )
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

        Spacer(modifier = Modifier.height(16.dp))

        val coroutineScope = rememberCoroutineScope()
        val apiService = remember { RetrofitInstance.api }
        val authRepository = remember { AuthRepository(apiService, userDao) }

        Button(
            onClick = {

                if (email.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                // name.text
                // email.text
                register(
                    name,
                    email,
                    password,
                    confirmPassword,
                    navController,
                    authRepository,
                    coroutineScope
                )
            },
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 10.dp,
                pressedElevation = 6.dp
            ),

            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(30.dp)
        ) {
            Text(
                text = "Submit",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                textAlign = TextAlign.End
            )
        }

        Row(
            modifier = Modifier
                .padding(top = 2.dp, bottom = 52.dp)
                .align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.Center
        ) {
            ClickableLoginText(navController, onTextSelected = {})
        }
    }

}

@Composable
fun ClickableLoginText(navController: NavHostController, onTextSelected: (String) -> Unit){
    val initialText = "Already have an account? "
    val registerText = " Sign In"

    val annotatedString = buildAnnotatedString {
        append(initialText)
        withStyle(style = SpanStyle(color = colorResource(R.color.orange))) {
            pushStringAnnotation(tag = registerText, annotation = registerText)
            append(registerText)
        }
    }

    ClickableText(text = annotatedString, onClick = {
        // navigate to login
        navController.popBackStack()
        navController.navigate("LoginScreen")
    })
}

//email = "eve.holt@reqres.in"
//password = "pistol"
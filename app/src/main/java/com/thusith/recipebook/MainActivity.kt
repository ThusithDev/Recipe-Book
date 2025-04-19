package com.thusith.recipebook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.thusith.recipebook.ui.theme.RecipeBookTheme
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.core.view.WindowCompat
import androidx.room.Room
import com.thusith.recipebook.data.local.AppDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Room database
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "recipe-db"
        ).build()

        val userDao = db.userDao()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        window.statusBarColor = Color.parseColor("#FC5835")
        window.navigationBarColor = Color.parseColor("#FC5835")
        enableEdgeToEdge()
        setContent {
            RecipeBookTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "Splash") {
                        composable("Splash") {
                            SplashScreen(navController = navController)
                        }
                        composable("HomeScreen"){
                            HomeScreen(navController = navController)
                        }
                        composable("LoginScreen"){
                            LoginScreen(navController = navController, userDao = userDao)
                        }
                        composable("RegisterScreen"){
                            RegisterScreen(navController = navController, userDao = userDao)
                        }
                    }
                }
            }
        }
    }

}

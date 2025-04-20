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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.thusith.recipebook.data.local.AppDatabase
import com.thusith.recipebook.viewModel.RecipeViewModel

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

        // enableEdgeToEdge()
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
                        composable("RecipeDetail/{recipeId}") { backStackEntry ->
                            val recipeViewModel: RecipeViewModel = viewModel() // <-- local ViewModel instance
                            val recipeId = backStackEntry.arguments?.getString("recipeId")
                            val recipe = recipeViewModel.recipes.value.find { it.id == recipeId }

                            recipe?.let {
                                RecipeDetailScreen(recipe = it, navController = navController)
                            }
                        }

                    }
                }
            }
        }
    }

}

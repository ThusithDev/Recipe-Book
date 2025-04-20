package com.thusith.recipebook

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.thusith.recipebook.viewModel.RecipeViewModel

@Composable
fun CategoryRecipesScreen(
    category: String,
    navController: NavHostController,
    recipeViewModel: RecipeViewModel = viewModel()
) {
    val recipes = recipeViewModel.recipes.value.filter {
        it.category.equals(category, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C0905))
            .padding(16.dp)
    ) {
        Text(
            text = "$category Recipes",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (recipes.isEmpty()) {
            Text(
                text = "No recipes found in $category category.",
                color = Color.LightGray,
                fontSize = 16.sp
            )
        } else {
            LazyColumn {
                items(recipes) { recipe ->
                    RecipeCard(recipe = recipe) {
                        navController.navigate("RecipeDetail/${recipe.id}")
                    }
                }
            }
        }
    }
}

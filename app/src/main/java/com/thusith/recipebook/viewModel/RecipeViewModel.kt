package com.thusith.recipebook.viewModel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thusith.recipebook.data.remote.ApiClient
import com.thusith.recipebook.model.Recipe
import kotlinx.coroutines.launch

class RecipeViewModel : ViewModel() {
    private val recipeApi = ApiClient.recipeApi

    private val _recipes = mutableStateOf<List<Recipe>>(emptyList())
    val recipes: State<List<Recipe>> = _recipes

    init {
        fetchRecipes()
    }

    fun fetchRecipes() {
        viewModelScope.launch {
            try {
                _recipes.value = recipeApi.getRecipes()
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "Error fetching recipes: ${e.message}")
            }
        }
    }

    fun addRecipe(recipe: Recipe, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                recipeApi.addRecipe(recipe)
                fetchRecipes()
                onSuccess()
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "Failed to add recipe: ${e.message}")
                onError(e.message ?: "Unknown error")
            }
        }
    }

    fun deleteRecipe(id: String) {
        viewModelScope.launch {
            recipeApi.deleteRecipe(id)
            fetchRecipes()
        }
    }
}

package com.thusith.recipebook.viewModel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thusith.recipebook.data.local.AppDatabase
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

    // View food recipes
    fun fetchRecipes() {
        viewModelScope.launch {
            try {
                _recipes.value = recipeApi.getRecipes()
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "Error fetching recipes: ${e.message}")
            }
        }
    }

    // Create food recipes
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

    suspend fun getLoggedInEmail(context: Context): String {
        val userDao = AppDatabase.getDatabase(context).userDao()
        return userDao.getUser()?.email ?: ""
    }

    // Update food recipes
    fun updateRecipe(recipe: Recipe, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                recipeApi.updateRecipe(recipe.id!!, recipe)
                fetchRecipes()
                onSuccess()
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "Failed to update recipe: ${e.message}")
                onError(e.message ?: "Unknown error")
            }
        }
    }

    // Enhanced delete with callback
    fun deleteRecipe(id: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                recipeApi.deleteRecipe(id)
                fetchRecipes()
                onSuccess()
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "Failed to delete recipe: ${e.message}")
                onError(e.message ?: "Unknown error")
            }
        }
    }
}

package com.thusith.recipebook.data.remote

import com.thusith.recipebook.model.Recipe
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

import retrofit2.Response
import retrofit2.http.*

interface RecipeApi {

    @GET("recipes")
    suspend fun getRecipes(): List<Recipe>

    @GET("recipes/{id}")
    suspend fun getRecipe(@Path("id") id: String): Recipe

    @POST("recipes")
    suspend fun addRecipe(@Body recipe: Recipe): Recipe

    @PUT("recipes/{id}")
    suspend fun updateRecipe(@Path("id") id: String, @Body recipe: Recipe): Recipe

    @DELETE("recipes/{id}")
    suspend fun deleteRecipe(@Path("id") id: String): Response<Unit>
}


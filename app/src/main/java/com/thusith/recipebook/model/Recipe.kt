package com.thusith.recipebook.model

data class Recipe(
    val id: String? = null,
    val title: String,
    val ingredients: String,
    val instructions: String,
    val imageUrl: String,
    val category: String
)


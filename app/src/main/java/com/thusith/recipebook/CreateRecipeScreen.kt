package com.thusith.recipebook

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.thusith.recipebook.data.local.AppDatabase
import com.thusith.recipebook.model.Recipe
import com.thusith.recipebook.viewModel.RecipeViewModel
import kotlinx.coroutines.runBlocking

@Composable
fun CreateRecipeScreen(
    navController: NavHostController,
    recipeViewModel: RecipeViewModel = viewModel()
) {
    var title by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    val context = LocalContext.current

    val items = listOf(
        BottomNavigationItem("HomeScreen", R.drawable.home_icon, R.drawable.home_icon, false),
        BottomNavigationItem("Create", R.drawable.create_icon, R.drawable.create_icon, false),
        BottomNavigationItem("Categories", R.drawable.category_icon, R.drawable.category_icon, false),
        BottomNavigationItem("Profile", R.drawable.profile_icon, R.drawable.profile_icon, false)
    )

    var selectedItemIndex by rememberSaveable { mutableIntStateOf(1) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                items = items,
                selectedIndex = selectedItemIndex,
                onItemSelected = { index ->
                    selectedItemIndex = index
                    navController.navigate(items[index].title)
                },
                height = 46.dp
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Create Recipe", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
            OutlinedTextField(value = ingredients, onValueChange = { ingredients = it }, label = { Text("Ingredients") })
            OutlinedTextField(value = instructions, onValueChange = { instructions = it }, label = { Text("Instructions") })
            OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Image URL") })
            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") })
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Your Email") })

            Button(onClick = {
                if (title.isNotBlank() && ingredients.isNotBlank() && email.isNotBlank()) {

                    val userDao = AppDatabase.getDatabase(context).userDao()
                    val loggedEmail = runBlocking { userDao.getUser()?.email ?: "" }

                    val newRecipe = Recipe(
                        title = title,
                        ingredients = ingredients,
                        instructions = instructions,
                        imageUrl = imageUrl,
                        category = category,
                        email = email
                    )

                    recipeViewModel.addRecipe(
                        recipe = newRecipe,
                        onSuccess = {
                            Toast.makeText(context, "Recipe added successfully!", Toast.LENGTH_SHORT).show()
                            navController.navigate("HomeScreen")
                        },
                        onError = {
                            Toast.makeText(context, "Failed to add recipe: $it", Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Add Recipe")
            }
        }
    }
}

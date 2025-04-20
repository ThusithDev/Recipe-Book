package com.thusith.recipebook

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.thusith.recipebook.data.local.AppDatabase
import com.thusith.recipebook.data.local.UserEntity
import com.thusith.recipebook.model.Recipe
import com.thusith.recipebook.viewModel.RecipeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ProfileScreen(navController: NavController, context: Context, recipeViewModel: RecipeViewModel = viewModel()) {
    val scope = rememberCoroutineScope()
    var user by remember { mutableStateOf<UserEntity?>(null) }
    // var myRecipes by remember { mutableStateOf<List<Recipe>>(emptyList()) }
    var showEditDialog by remember { mutableStateOf(false) }
    var currentEditRecipe by remember { mutableStateOf<Recipe?>(null) }

    // Function to handle recipe deletion
    fun handleDelete(recipeId: String) {
        recipeViewModel.deleteRecipe(
            id = recipeId,
            onSuccess = {
                // Show toast message when Success
                Toast.makeText(context, "Recipe deleted", Toast.LENGTH_SHORT).show()
            },
            onError = { error ->
                Toast.makeText(context, "Delete failed: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Edit Dialog
    if (showEditDialog && currentEditRecipe != null) {
        RecipeEditDialog(
            recipe = currentEditRecipe!!,
            onDismiss = { showEditDialog = false },
            onSave = { updatedRecipe ->
                recipeViewModel.updateRecipe(
                    recipe = updatedRecipe,
                    onSuccess = {
                        showEditDialog = false
                        Toast.makeText(context, "Recipe updated", Toast.LENGTH_SHORT).show()
                    },
                    onError = { error ->
                        Toast.makeText(context, "Update failed: $error", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        )
    }

    val contextTwo = LocalContext.current
    val allRecipes by recipeViewModel.recipes

    // Filter recipes based on logged-in user's email
    val myRecipes by remember(user, allRecipes) {
        derivedStateOf {
            user?.let { currentUser ->
                allRecipes.filter { it.email == currentUser.email }
            } ?: emptyList()
        }
    }

    LaunchedEffect(Unit) {
        scope.launch {
            // Get user from Room DB
            val userDao = AppDatabase.getDatabase(context).userDao()
            val currentUser = withContext(Dispatchers.IO) { userDao.getUser() }
            user = currentUser

            // Get recipes from view model and filter by email
            /**
            currentUser?.let {
                val allRecipes = recipeViewModel.recipes.value
                myRecipes = allRecipes.filter { it.email == currentUser.email }
            }
            **/
        }
    }

    val items = listOf(
        BottomNavigationItem("HomeScreen", R.drawable.home_icon, R.drawable.home_icon, false),
        BottomNavigationItem("Create", R.drawable.create_icon, R.drawable.create_icon, false),
        BottomNavigationItem("Categories", R.drawable.category_icon, R.drawable.category_icon, false),
        BottomNavigationItem("Profile", R.drawable.profile_icon, R.drawable.profile_icon, false)
    )

    var selectedItemIndex by rememberSaveable { mutableIntStateOf(3) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                items = items,
                selectedIndex = selectedItemIndex,
                onItemSelected = { index ->
                    selectedItemIndex = index
                    // Handle navigation
                    navController.navigate(items[index].title)
                },
                height = 46.dp // Custom height
                // modifier = Modifier.navigationBarsPadding()
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .background(color = Color(0xFF1C0905))
            .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Profile",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White)

            Spacer(modifier = Modifier.height(16.dp))

            user?.let {
                Text("Name: ${it.name}", color = Color.White)
                Text("Email: ${it.email}", color = Color.White)
            } ?: Text("Loading user...", color = Color.White)

            Spacer(modifier = Modifier.height(24.dp))

            Text("My Recipes", color = Color.White, style = MaterialTheme.typography.headlineSmall)

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(myRecipes) { recipe ->
                    RecipeItem(
                        recipe = recipe,
                        onEdit = {
                            currentEditRecipe = recipe
                            showEditDialog = true
                        },
                        onDelete = { handleDelete(recipe.id!!) }
                    )
                }
            }
        }
    }
}

@Composable
fun RecipeItem(
    recipe: Recipe,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )

                // Action buttons
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Recipe"
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Recipe",
                            tint = Color.Red
                        )
                    }
                }
            }

            Text(text = "Category: ${recipe.category}")
            Spacer(modifier = Modifier.height(8.dp))

            recipe.imageUrl?.let { imageUrl ->
                Image(
                    painter = rememberAsyncImagePainter(model = imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .width(120.dp)
                        .height(120.dp)
                )
            }

            // Show ingredients and instructions when expanded
            var expanded by remember { mutableStateOf(false) }
            TextButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(if (expanded) "Show Less" else "Show More")
            }

            if (expanded) {
                Text("Ingredients:", fontWeight = FontWeight.Bold)
                Text(recipe.ingredients.replace(", ", "\n"))

                Spacer(modifier = Modifier.height(8.dp))

                Text("Instructions:", fontWeight = FontWeight.Bold)
                Text(recipe.instructions)
            }
        }
    }
}

@Composable
fun RecipeEditDialog(
    recipe: Recipe,
    onDismiss: () -> Unit,
    onSave: (Recipe) -> Unit
) {
    val titleState = rememberSaveable { mutableStateOf(recipe.title) }
    val ingredientsState = rememberSaveable { mutableStateOf(recipe.ingredients) }
    val instructionsState = rememberSaveable { mutableStateOf(recipe.instructions) }
    val categoryState = rememberSaveable { mutableStateOf(recipe.category) }
    val imageUrlState = rememberSaveable { mutableStateOf(recipe.imageUrl ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Recipe") },
        text = {
            Column {
                OutlinedTextField(
                    value = titleState.value,
                    onValueChange = { titleState.value = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = ingredientsState.value,
                    onValueChange = { ingredientsState.value = it },
                    label = { Text("Ingredients (comma separated)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = instructionsState.value,
                    onValueChange = { instructionsState.value = it },
                    label = { Text("Instructions") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 4
                )

                OutlinedTextField(
                    value = categoryState.value,
                    onValueChange = { categoryState.value = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = imageUrlState.value,
                    onValueChange = { imageUrlState.value = it },
                    label = { Text("Image URL") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    imageUrlState.value.ifEmpty { null }?.let {
                        recipe.copy(
                            title = titleState.value,
                            ingredients = ingredientsState.value,
                            instructions = instructionsState.value,
                            category = categoryState.value,
                            imageUrl = it
                        )
                    }?.let {
                        onSave(
                            it
                        )
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}



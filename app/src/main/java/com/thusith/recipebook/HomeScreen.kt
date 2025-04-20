package com.thusith.recipebook

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.thusith.recipebook.model.Recipe
import com.thusith.recipebook.viewModel.RecipeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
  Author -: Thusith Wickramasinghe
* */

@Composable
fun HomeScreen(navController: NavHostController, recipeViewModel: RecipeViewModel = viewModel()) {

    val activity = LocalActivity.current as? Activity

    DoubleBackToExitApp {
        activity?.finish()
    }

    // var searchQuery by rememberSaveable { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }

    val filteredRecipes = if (searchQuery.isBlank()) {
        recipeViewModel.recipes.value
    } else {
        recipeViewModel.recipes.value.filter {
            it.title.contains(searchQuery, ignoreCase = true)
        }
    }

    val items = listOf(
        BottomNavigationItem(
            title = "HomeScreen", //HomePage/$profileImageUrl
            selectedIcon = R.drawable.home_icon,
            unselectedIcon = R.drawable.home_icon,
            hasNews = false
        ),
        BottomNavigationItem(
            title = "Create",
            selectedIcon = R.drawable.create_icon,
            unselectedIcon = R.drawable.create_icon,
            hasNews = false
        ),
        BottomNavigationItem(
            title = "Categories",
            selectedIcon = R.drawable.category_icon,
            unselectedIcon = R.drawable.category_icon,
            hasNews = false
        ),
        BottomNavigationItem(
            title = "Profile",
            selectedIcon = R.drawable.profile_icon,
            unselectedIcon = R.drawable.profile_icon,
            hasNews = false
        )
    )
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                items = items,
                selectedIndex = selectedItemIndex,
                onItemSelected = { index ->
                    selectedItemIndex = index

                    navController.navigate(items[index].title)
                },
                height = 46.dp // Custom height
                // modifier = Modifier.navigationBarsPadding()
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(color = Color(0xFF1C0905)),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 8.dp, start = 70.dp, end = 4.dp)
                    .fillMaxWidth()
            ) {

                Text(
                    text = "Recipe Book",
                    color = Color(0xFFFFFFFF),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(30.dp)
                )

                Image(
                    painter = painterResource(id = R.drawable.pizza), // replace with your actual drawable name
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(68.dp)
                        .padding(start = 8.dp)
                )
            }

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search recipes...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = Color.White,
                    textColor = Color.Black,
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(color = Color(0xFF1C0905)),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                items(filteredRecipes) { recipe ->
                    RecipeCard(recipe = recipe) {
                        navController.navigate("RecipeDetail/${recipe.id}")
                    }
                }
                /**
                items(recipeViewModel.recipes.value) { recipe ->
                    RecipeCard(recipe = recipe) {
                        navController.navigate("RecipeDetail/${recipe.id}")
                    }
                }
                **/
            }
        }

    }
}

@Composable
fun BottomNavigationBar(
    items: List<BottomNavigationItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    backgroundColor: Color = Color(0xFFFC5835),
    height: Dp = 46.dp
) {
    Surface(
        modifier = Modifier
            .background(Color(0xFF1C0905))
            .padding(bottom = 10.dp)
            .height(height)
            .clip(RoundedCornerShape(16.dp)),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    icon = {
                        if (item.badgeCount != null && item.badgeCount > 0) {
                            BadgedBox(badge = { Badge { Text("${item.badgeCount}") } }) {
                                Icon(
                                    painterResource(id = if (selectedIndex == index) item.selectedIcon else item.unselectedIcon),
                                    contentDescription = item.title,
                                    tint = if (selectedIndex == index) Color.White else Color.Black,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        } else {
                            Icon(
                                painterResource(id = if (selectedIndex == index) item.selectedIcon else item.unselectedIcon),
                                contentDescription = item.title,
                                tint = if (selectedIndex == index) Color.White else Color.Black,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    },
                    selected = selectedIndex == index,
                    onClick = { onItemSelected(index) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        unselectedIconColor = Color.Black,
                        selectedTextColor = Color.White,
                        unselectedTextColor = Color.Black,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: Int,
    val unselectedIcon: Int,
    val hasNews: Boolean,
    val badgeCount: Int? = null,
    val parameterizedRoute: String? = null
)

@Composable
fun RecipeCard(recipe: Recipe, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF3E2723)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = recipe.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Category: ${recipe.category}",
                color = Color.LightGray,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Ingredients: ${recipe.ingredients}",
                color = Color.LightGray,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun DoubleBackToExitApp(onExit: () -> Unit) {
    var backPressedOnce by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    BackHandler {
        if (backPressedOnce) {
            onExit()
        } else {
            backPressedOnce = true
            Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()

            // Reset the flag after 2 seconds
            scope.launch {
                delay(2000)
                backPressedOnce = false
            }
        }
    }
}

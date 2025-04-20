package com.thusith.recipebook

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun CategoryScreen(navController: NavHostController) {
    val categories = listOf(
        CategoryItem("Chinese", R.drawable.chinese_foods),
        CategoryItem("Italian", R.drawable.italian_foods),
        CategoryItem("Sri Lankan", R.drawable.sri_lankan_food),
        CategoryItem("Thai", R.drawable.thai_foods),
        CategoryItem("Snacks", R.drawable.snacks),
        CategoryItem("Other", R.drawable.chinese_foods)
    )

    val items = listOf(
        BottomNavigationItem("HomeScreen", R.drawable.home_icon, R.drawable.home_icon, false),
        BottomNavigationItem("Create", R.drawable.create_icon, R.drawable.create_icon, false),
        BottomNavigationItem("Categories", R.drawable.category_icon, R.drawable.category_icon, false),
        BottomNavigationItem("Profile", R.drawable.profile_icon, R.drawable.profile_icon, false)
    )

    var selectedItemIndex by rememberSaveable { mutableIntStateOf(2) }

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
                .verticalScroll(rememberScrollState())
                .background(color = Color(0xFF1C0905)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Categories",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
            )

            // Arrange items in 3 rows of 2 columns
            for (i in categories.indices step 2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CategoryBox(category = categories[i], navController = navController)
                    if (i + 1 < categories.size) {
                        CategoryBox(category = categories[i + 1], navController = navController)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

}

@Composable
fun CategoryBox(category: CategoryItem, navController: NavHostController) {
    Column(
        modifier = Modifier
            .size(150.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFEEEEEE))
            .clickable {
                // Handle navigation to category-specific screen
                navController.navigate("CategoryRecipes/${category.name}")
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = category.imageRes),
            contentDescription = category.name,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = category.name,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

data class CategoryItem(
    val name: String,
    val imageRes: Int
)

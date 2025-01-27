package com.example.fakestore.home

import android.media.Image
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bottombar.AnimatedBottomBar
import com.example.bottombar.components.BottomBarItem
import com.example.bottombar.model.IndicatorStyle

@Composable
fun BottomBar(icons: List<NavigationItem>,navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var selectedItem by remember { mutableIntStateOf(0) }
    AnimatedBottomBar(
        selectedItem = selectedItem,
        itemSize = icons.take(3).size,
        containerColor = Color.LightGray,
        indicatorStyle = IndicatorStyle.LINE
    ) {
        icons.forEachIndexed { index, navigationItem ->
            BottomBarItem(
                selected = currentRoute == navigationItem.route,
                onClick = {
                    if (currentRoute != navigationItem.route) {
                        selectedItem = index
                        navController.navigate(navigationItem.route) {
                            // Prevent multiple copies of the same destination
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }

                    }
                },
                imageVector = navigationItem.icon,
                label = navigationItem.title,
                containerColor = Color.Transparent

            )

        }

    }


}

data class NavigationItem(val route: String, val icon: ImageVector, val title: String)
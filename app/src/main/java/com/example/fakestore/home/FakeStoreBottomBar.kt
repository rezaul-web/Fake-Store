package com.example.fakestore.home

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.bottombar.AnimatedBottomBar
import com.example.bottombar.components.BottomBarItem
import com.example.bottombar.model.IndicatorStyle

@Composable
fun FakeStoreBottomBar(icons: List<NavigationItem>, navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var selectedItem by remember { mutableIntStateOf(0) }
    AnimatedBottomBar(
        selectedItem = selectedItem,
        itemSize = icons.take(3).size,
        containerColor = Color.Cyan.copy(alpha = .2f),
        indicatorStyle = IndicatorStyle.NONE
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
                containerColor = Color.Cyan.copy(alpha = .2f)

            )

        }

    }
}

data class NavigationItem(val route: String, val icon: ImageVector, val title: String)
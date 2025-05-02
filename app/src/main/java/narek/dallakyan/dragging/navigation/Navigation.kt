package narek.dallakyan.dragging.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import narek.dallakyan.dragging.screens.FixedScreen
import narek.dallakyan.dragging.screens.InitialScreen

@Composable
fun Navigation(
    modifier: Modifier,
    navController: NavHostController,
) {
    NavHost(navController, startDestination = NavigationItem.Initial.route, modifier = modifier) {
        composable(NavigationItem.Initial.route) {
            InitialScreen(
                onFixedClick = { navController.navigate(NavigationItem.Fixed.route) },
            )
        }
        composable(NavigationItem.Fixed.route) {
            FixedScreen()
        }
    }
}

sealed class NavigationItem(var route: String) {
    data object Initial : NavigationItem("initial")
    data object Fixed : NavigationItem("fixed")
}
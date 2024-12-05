package mp.verif_ai.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import mp.verif_ai.presentation.screens.Screen

@Composable
fun AppBottomNavigation(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        val items = listOf(
            Screen.MainNav.Home to "home",
            Screen.MainNav.Inbox.Main to "inbox",
            Screen.MainNav.Settings.Main to "setting"
        )

        items.forEach { (screen, label) ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = when (screen) {
                            is Screen.MainNav.Home -> Icons.Filled.Home
                            is Screen.MainNav.Inbox.Main -> Icons.Filled.Mail
                            is Screen.MainNav.Settings.Main -> Icons.Filled.Settings
                            else -> Icons.Filled.Home
                        },
                        contentDescription = label
                    )
                },
                label = { Text(label) },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

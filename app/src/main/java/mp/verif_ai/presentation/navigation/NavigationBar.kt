package mp.verif_ai.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import mp.verif_ai.presentation.screens.Screen

data object NavColors {
    val NavigationBackground = Color(0xFFEAF4FF)
    val SelectedIndicator = Color(0xFFD9E5FF)
}

@Composable
fun AppBottomNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = modifier
            .padding(horizontal = 8.dp),
        containerColor = NavColors.NavigationBackground,
        tonalElevation = 8.dp
    ) {
        bottomNavItems.forEach { item ->
            val selected = isCurrentRoute(currentRoute, item.route)
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = NavColors.SelectedIndicator
                ),
                alwaysShowLabel = true
            )
        }
    }
}

private fun isCurrentRoute(currentRoute: String?, itemRoute: String): Boolean {
    return when {
        currentRoute == null -> false
        itemRoute.contains("home_screen") -> currentRoute.startsWith("main/home")
        itemRoute.contains("explore_screen") -> currentRoute.startsWith("main/explore")
        itemRoute.contains("inbox_screen") -> currentRoute.startsWith("main/inbox")
        itemRoute.contains("settings_screen") -> currentRoute.startsWith("main/settings")
        else -> false
    }
}

private val bottomNavItems = listOf(
    BottomNavItem(
        route = Screen.MainNav.Home.HomeScreen.route,  // "main/home_screen"
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home,
        label = "Home"
    ),
    BottomNavItem(
        route = Screen.MainNav.Explore.ExploreScreen.route,  // "main/explore_screen"
        icon = Icons.Outlined.Explore,
        selectedIcon = Icons.Filled.Explore,
        label = "Explore"
    ),
    BottomNavItem(
        route = Screen.MainNav.Inbox.InboxScreen.route,  // "main/inbox_screen"
        icon = Icons.Outlined.Inbox,
        selectedIcon = Icons.Filled.Inbox,
        label = "Inbox"
    ),
    BottomNavItem(
        route = Screen.MainNav.Settings.SettingsScreen.route,  // "main/settings_screen"
        icon = Icons.Outlined.Settings,
        selectedIcon = Icons.Filled.Settings,
        label = "Settings"
    )
)

private data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val label: String
)
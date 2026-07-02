package edu.metrostate.ics342.mediatracker.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Feed
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.Feed
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import edu.metrostate.ics342.mediatracker.R

data class BottomNavItem(
    val route: String,
    val labelRes: Int,
    val selectedIcon: @Composable () -> Unit,
    val unselectedIcon: @Composable () -> Unit,
)

val bottomNavItems = listOf(
    BottomNavItem(
        Routes.ACTIVITY_FEED,
        R.string.nav_feed,
        selectedIcon = { Icon(Icons.AutoMirrored.Filled.Feed, contentDescription = stringResource(R.string.nav_feed)) },
        unselectedIcon = { Icon(Icons.AutoMirrored.Outlined.Feed, contentDescription = stringResource(R.string.nav_feed)) }
    ),
    BottomNavItem(
        Routes.SEARCH,
        R.string.nav_search,
        selectedIcon = { Icon(Icons.Filled.Search, contentDescription = stringResource(R.string.nav_search)) },
        unselectedIcon = { Icon(Icons.Outlined.Search, contentDescription = stringResource(R.string.nav_search)) }
    ),
    BottomNavItem(
        Routes.LIBRARY,
        R.string.nav_library,
        selectedIcon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = stringResource(R.string.nav_library)) },
        unselectedIcon = { Icon(Icons.AutoMirrored.Outlined.MenuBook, contentDescription = stringResource(R.string.nav_library)) }
    ),
    BottomNavItem(
        Routes.CONNECTIONS,
        R.string.nav_people,
        selectedIcon = { Icon(Icons.Filled.Group, contentDescription = stringResource(R.string.nav_people)) },
        unselectedIcon = { Icon(Icons.Outlined.Group, contentDescription = stringResource(R.string.nav_people)) }
    ),
    BottomNavItem(
        Routes.MY_PROFILE,
        R.string.nav_profile,
        selectedIcon = { Icon(Icons.Filled.Person, contentDescription = stringResource(R.string.nav_profile)) },
        unselectedIcon = { Icon(Icons.Outlined.Person, contentDescription = stringResource(R.string.nav_profile)) }
    ),
)

@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = NavigationBarDefaults.Elevation
    ) {
        bottomNavItems.forEach { item ->
            val isSelected =
                currentDestination?.hierarchy?.any { it.route == item.route } == true

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    if (isSelected) item.selectedIcon() else item.unselectedIcon()
                },
                label = {
                    Text(
                        text = stringResource(item.labelRes),
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
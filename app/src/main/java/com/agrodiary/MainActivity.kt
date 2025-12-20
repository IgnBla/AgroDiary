package com.agrodiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.agrodiary.ui.navigation.AgroDiaryBottomNavBar
import com.agrodiary.ui.navigation.NavGraph
import com.agrodiary.ui.navigation.Screen
import com.agrodiary.ui.theme.AgroDiaryTheme
import dagger.hilt.android.AndroidEntryPoint

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import com.agrodiary.data.local.PreferenceManager
import com.agrodiary.data.repository.AuthRepository
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferenceManager: PreferenceManager

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by preferenceManager.themeMode.collectAsState()
            val darkTheme = when (themeMode) {
                "light" -> false
                "dark" -> true
                else -> isSystemInDarkTheme()
            }

            AgroDiaryTheme(darkTheme = darkTheme) {
                AgroDiaryAppContent(authRepository = authRepository)
            }
        }
    }
}

@Composable
fun AgroDiaryAppContent(authRepository: AuthRepository) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // Determine if bottom bar should be shown
    val showBottomBar = currentRoute != null &&
        currentRoute != Screen.Login.route &&
        currentRoute != Screen.Register.route

    // Determine start destination
    var isInitialized by remember { mutableStateOf(false) }
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        authRepository.initializeSession()
        startDestination = if (authRepository.isLoggedIn.value) {
            Screen.Home.route
        } else {
            Screen.Login.route
        }
        isInitialized = true
    }

    if (!isInitialized || startDestination == null) {
        // Show loading while determining auth state
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (showBottomBar) {
                    AgroDiaryBottomNavBar(navController = navController)
                }
            }
        ) { innerPadding ->
            NavGraph(
                navController = navController,
                startDestination = startDestination!!,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

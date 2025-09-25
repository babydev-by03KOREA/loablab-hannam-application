package com.loab.hannam.ui.screen.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.loab.hannam.ui.SurveyViewModel
import com.loab.hannam.ui.screen.consultation.NameScreen
import com.loab.hannam.ui.screen.intro.IntroScreen
import com.loab.hannam.ui.screen.start.StartScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    vm: SurveyViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Start.route,
        modifier = modifier
    ) {
        composable(Screen.Intro.route) {
            IntroScreen(vm = vm, navController = navController)
        }
        composable(Screen.Start.route) {
            StartScreen(vm = vm, navController = navController)
        }
        composable(Screen.Start.route) {
            NameScreen(vm = vm, navController = navController)
        }
    }
}
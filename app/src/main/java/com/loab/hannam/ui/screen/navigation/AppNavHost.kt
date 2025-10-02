package com.loab.hannam.ui.screen.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.loab.hannam.ui.SurveyViewModel
import com.loab.hannam.ui.screen.consultation.FifthScreen
import com.loab.hannam.ui.screen.consultation.FirstScreen
import com.loab.hannam.ui.screen.consultation.FourthScreen
import com.loab.hannam.ui.screen.consultation.NameScreen
import com.loab.hannam.ui.screen.consultation.SecondScreen
import com.loab.hannam.ui.screen.consultation.ThankYouScreen
import com.loab.hannam.ui.screen.consultation.ThirdScreen
import com.loab.hannam.ui.screen.intro.IntroScreen
import com.loab.hannam.ui.screen.result.ResultScreen
import com.loab.hannam.ui.screen.start.StartScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    vm: SurveyViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Intro.route,
        modifier = modifier
    ) {
        composable(Screen.Intro.route) {
            IntroScreen(vm = vm, navController = navController)
        }
        composable(Screen.Start.route) {
            StartScreen(vm = vm, navController = navController)
        }
        composable(Screen.HairConsult.route) {
            NameScreen(vm = vm, navController = navController)
        }
        composable(Screen.First.route) {
            FirstScreen(vm = vm, navController = navController)
        }
        composable(Screen.Second.route) {
            SecondScreen(vm = vm, navController = navController)
        }
        composable(Screen.Third.route) {
            ThirdScreen(vm = vm, navController = navController)
        }
        composable(Screen.Fourth.route) {
            FourthScreen(vm = vm, navController = navController)
        }
        composable(Screen.Fifth.route) {
            FifthScreen(vm = vm, navController = navController)
        }
        composable(Screen.Result.route) {
            ResultScreen(vm = vm, navController = navController)
        }
        composable(Screen.ThankYou.route) {
            ThankYouScreen(vm = vm, navController = navController)
        }
    }
}
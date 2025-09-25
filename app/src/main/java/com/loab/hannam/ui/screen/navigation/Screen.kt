package com.loab.hannam.ui.screen.navigation

sealed class Screen(val route: String) {
    object Start : Screen("start")
    object Intro : Screen("intro")
    object Concern : Screen("concern")
    object Preference : Screen("preference")
    object History : Screen("history")
    object Result : Screen("result")
}
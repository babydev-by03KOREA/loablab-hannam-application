package com.loab.hannam.ui.screen.navigation

sealed class Screen(val route: String) {
    object Start : Screen("start")
    object Intro : Screen("intro")
    object History : Screen("history")
    object Result : Screen("result")
}
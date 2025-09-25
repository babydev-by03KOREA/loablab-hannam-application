package com.loab.hannam.ui.screen.navigation

sealed class Screen(val route: String) {
    object Start : Screen("start")
    object Intro : Screen("intro")
    object HairConsult : Screen("hair_consult")
    object First : Screen("first")
    object Second : Screen("second")
    object Third : Screen("third")
    object Fourth : Screen("fourth")
    object Fifth : Screen("fifth")
    object Result : Screen("result")
}

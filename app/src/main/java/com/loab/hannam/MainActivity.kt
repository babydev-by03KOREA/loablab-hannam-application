package com.loab.hannam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.loab.hannam.data.repository.SurveyRepositoryImpl
import com.loab.hannam.data.store.SurveyLocalStore
import com.loab.hannam.ui.SurveyViewModel
import com.loab.hannam.ui.theme.LOABLABHannamApplicationTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.loab.hannam.ui.screen.navigation.AppNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LOABLABHannamApplicationTheme {
                val navController = rememberNavController()

                val repo = SurveyRepositoryImpl(SurveyLocalStore(applicationContext))
                val vm: SurveyViewModel = viewModel(factory = SurveyViewModelFactory(repo))

                AppNavHost(navController = navController, vm = vm)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LOABLABHannamApplicationTheme {

    }
}
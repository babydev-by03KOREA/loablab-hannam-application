package com.loab.hannam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.loab.hannam.data.repository.SurveyRepositoryImpl
import com.loab.hannam.data.store.SurveyLocalStore
import com.loab.hannam.ui.SurveyViewModel
import com.loab.hannam.ui.screen.result.ResultActions
import com.loab.hannam.ui.theme.LOABLABHannamApplicationTheme
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Repo 준비 (Context 기반)
        val repo = SurveyRepositoryImpl(SurveyLocalStore(applicationContext))
        val factory = SurveyViewModelFactory(repo)


        setContent {
            LOABLABHannamApplicationTheme {
                val vm: SurveyViewModel = viewModel(factory = factory)
                ResultActions(vm = vm)
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
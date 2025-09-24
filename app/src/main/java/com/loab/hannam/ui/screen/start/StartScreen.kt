package com.loab.hannam.ui.screen.start

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.loab.hannam.ui.SurveyViewModel
import com.loab.hannam.ui.theme.LOABLABHannamApplicationTheme

@Composable
fun StartScreenContent(

) {

}

@Composable
fun StartScreen(
    vm: SurveyViewModel,
    navController: NavController
) {
    val state by vm.uiState.collectAsStateWithLifecycle()


}


@Preview(showBackground = true)
@Composable
fun StartScreenPreview() {
    LOABLABHannamApplicationTheme {

    }
}
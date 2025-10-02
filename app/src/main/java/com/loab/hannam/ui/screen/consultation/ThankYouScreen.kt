package com.loab.hannam.ui.screen.consultation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.loab.hannam.R
import com.loab.hannam.ui.SurveyViewModel
import com.loab.hannam.ui.screen.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun ThankYouScreen(
    vm: SurveyViewModel,
    navController: NavController
) {
    // 화면 들어오면 1.5초 후 초기화 + 스택 클리어하고 Intro로
    LaunchedEffect(Unit) {
        delay(3000)
        vm.resetSurvey() // SurveyState() 로 초기화하는 함수
        navController.navigate(Screen.Intro.route) {
            popUpTo(0) { inclusive = true } // 전체 스택 제거
            launchSingleTop = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.thankyou_for_your_time),
            style = MaterialTheme.typography.titleLarge
        )
    }
}

package com.loab.hannam.ui.screen.intro

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.loab.hannam.R
import com.loab.hannam.ui.SurveyViewModel
import com.loab.hannam.ui.components.LangBar
import com.loab.hannam.ui.screen.navigation.Screen
import com.loab.hannam.ui.theme.LOABLABHannamApplicationTheme

@Composable
fun IntroScreenContent(
    currentLang: String,
    onSelectLang: (String) -> Unit,
    onStart: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {

        // 1) 배경 이미지 (뒤에 꽉 채우기)
        Image(
            painter = painterResource(R.drawable.lohub_intro),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        // 2) 상단 언어 선택 (안전영역 + 중앙 정렬)
        LangBar(
            currentLang = currentLang,
            onSelectLang = onSelectLang
        )

        // 3) 하단 START 버튼 (가운데 정렬)
        Button(
            onClick = onStart,
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 32.dp)
                .height(56.dp)
                .width(220.dp)
        ) {
            Text(
                text = stringResource(R.string.start).uppercase(),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun IntroScreen(
    vm: SurveyViewModel,
    navController: NavController
) {
    val state by vm.uiState.collectAsStateWithLifecycle()

    IntroScreenContent(
        currentLang = state.customer.localeTag,          // "ko" / "zh" / "en" / "ja"
        onSelectLang = { lang -> vm.setLocale(lang) },   // ViewModel에 언어 변경 위임
        onStart = {                                      // 다음 화면으로 이동
            navController.navigate(Screen.Start.route) {
                launchSingleTop = true
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun StartScreenPreview() {
    LOABLABHannamApplicationTheme {
        IntroScreenContent(
            currentLang = "ko",
            onSelectLang = {},
            onStart = {}
        )
    }
}

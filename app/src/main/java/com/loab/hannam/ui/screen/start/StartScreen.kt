package com.loab.hannam.ui.screen.start

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.loab.hannam.R
import com.loab.hannam.data.model.HairChecklist
import com.loab.hannam.data.model.SurveyState
import com.loab.hannam.ui.SurveyViewModel
import com.loab.hannam.ui.components.LangBar
import com.loab.hannam.ui.components.PrevNextBar
import com.loab.hannam.ui.preview.FakeSurveyRepository
import com.loab.hannam.ui.screen.navigation.Screen
import com.loab.hannam.ui.theme.LOABLABHannamApplicationTheme

@Composable
fun StartScreen(
    vm: SurveyViewModel,
    navController: NavController
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    StartScreenContent(
        state = state,
        vm = vm,
        navController = navController,
        currentLang = state.customer.localeTag,
        onSelectLang = { lang -> vm.setLocale(lang) }
    )
}

@Composable
fun StartScreenContent(
    state: SurveyState,
    vm: SurveyViewModel,
    navController: NavController,
    currentLang: String,
    onSelectLang: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // 아주 연한 워터마크
        Text(
            text = "LOAB",
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer {
                    rotationZ = 90f
                }
                .alpha(0.06f),
            fontSize = 150.sp,
            fontWeight = FontWeight.Normal
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 상단 언어 선택
            LangBar(
                currentLang = currentLang,
                onSelectLang = onSelectLang
            )

            // 가운데 본문(센터 정렬, 줄간 간격 여유)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.thank_you_message),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 28.sp
                    )
                )
            }

            // 하단 Prev / Next 바
            PrevNextBar(
                navController = navController,
                prevRoute = Screen.Intro.route,
                nextRoute = Screen.Concern.route,
                nextEnabled = listOf(
                    state.hair.lastCut,
                    state.hair.lastPerm,
                    state.hair.lastColor,
                    state.hair.lastBleach
                ).any { it != null },  // 네 개 중 하나라도 선택되면 활성화
                onBeforeNavigateNext = {
                    vm.persistStep()
                    true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }
    }
}


@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun StartScreenPreview() {
    LOABLABHannamApplicationTheme {
        val initialState = SurveyState(
            hair = HairChecklist(
                lastCut = true,
                lastPerm = null,
                lastColor = null,
                lastBleach = null
            )
        )
        val vm = remember {
            SurveyViewModel(
                repo = FakeSurveyRepository(initialState)
            )
        }
        val navController = rememberNavController()
        val state by vm.uiState.collectAsStateWithLifecycle()

        StartScreenContent(
            state = state,
            vm = vm,
            navController = navController,
            currentLang = state.customer.localeTag,
            onSelectLang = { lang -> vm.setLocale(lang) }
        )
    }
}

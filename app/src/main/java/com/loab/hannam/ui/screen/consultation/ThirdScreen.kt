package com.loab.hannam.ui.screen.consultation

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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


enum class UsualStyle(@StringRes val labelRes: Int) {
    STANDARD(R.string.style_standard),        // 스탠다드
    SOFT(R.string.style_soft),                // 부드러운
    BOYISH(R.string.style_boyish),            // 보이쉬한
    LIGHT(R.string.style_light),              // 가벼운
    CASUAL(R.string.style_casual),            // 캐주얼
    CUTE(R.string.style_cute),                // 귀여운
    PROFESSIONAL(R.string.style_pro),       // 프로페셔널한
    GLAM(R.string.style_flashy),                // 화려한
    TRENDY(R.string.style_trendy),            // 트렌디
    NEAT(R.string.style_clean),                // 깔끔한
    UNIQUE(R.string.style_unique),            // 유니크
    SIMPLE(R.string.style_simple),            // 심플
    MODERN(R.string.style_modern),            // 모던
    CHIC(R.string.style_chic),                // 시크한
    VINTAGE(R.string.style_vintage),          // 빈티지한
    EFFORTLESS(R.string.style_noeffort)     // 꾸안꾸
}

@OptIn(ExperimentalLayoutApi::class)
@SuppressLint("MutableCollectionMutableState")
@Composable
fun ThirdScreen(
    vm: SurveyViewModel,
    navController: NavController
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    val scroll = rememberScrollState()

    // 기존 저장값을 enum으로 역변환
    val initiallySelected = remember(state.hair.usualImage) {
        state.hair.usualImage.mapNotNull { saved ->
            runCatching { UsualStyle.valueOf(saved) }.getOrNull()
        }.toMutableSet()
    }
    var selected by remember { mutableStateOf(initiallySelected) }
    val maxSelect = 3

    Scaffold(
        bottomBar = {
            PrevNextBar(
                navController = navController,
                prevRoute = Screen.Second.route,         // 이전 단계
                nextRoute = Screen.Fourth.route,      // 다음 단계
                nextEnabled = selected.isNotEmpty(),      // 최소 1개 이상 선택 시 활성화
                onBeforeNavigateNext = {
                    // 저장: enum 이름 리스트로 보관 (이미 모델이 String 리스트이므로)
                    vm.updateHair { hair ->
                        hair.copy(usualImage = selected.map { it.name })
                    }
                    vm.persistStep()
                    true
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scroll),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 상단 언어 선택
                LangBar(
                    currentLang = state.customer.localeTag,
                    onSelectLang = { vm.setLocale(it) }
                )

                // 타이틀
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.hair_condition_check_list),
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.hair_treatment_consultation),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                // 평소 스타일 이미지
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.choose_usual_style_image),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(24.dp))
                }

                // 체크박스 그리드
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    UsualStyle.entries.forEach { option ->
                        StyleCheckItem(
                            checked = option in selected,
                            label = stringResource(option.labelRes),
                            onToggle = {
                                selected = if (option in selected) {
                                    (selected - option).toMutableSet()
                                } else {
                                    if (selected.size >= maxSelect) selected // 3개 초과는 무시
                                    else (selected + option).toMutableSet()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StyleCheckItem(
    checked: Boolean,
    label: String,
    onToggle: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = checked, onCheckedChange = { onToggle() })
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PreferenceThirdPagePreview() {
    LOABLABHannamApplicationTheme {
        // 초기 상태 세팅
        val initialState = SurveyState(
            hair = HairChecklist(
                usualImage = listOf("스탠다드", "귀여운", "꾸안꾸")
            )
        )

        // Fake Repository + VM
        val vm = remember { SurveyViewModel(FakeSurveyRepository(initialState)) }
        val navController = rememberNavController()

        ThirdScreen(
            vm = vm,
            navController = navController
        )
    }
}
package com.loab.hannam.ui.screen.consultation

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.loab.hannam.R
import com.loab.hannam.ui.SurveyViewModel
import com.loab.hannam.ui.components.PrevNextBar
import com.loab.hannam.ui.components.LangBar
import com.loab.hannam.ui.preview.FakeSurveyRepository
import com.loab.hannam.ui.screen.navigation.Screen
import com.loab.hannam.ui.theme.LOABLABHannamApplicationTheme

/** Y/N 두 개 선택 가능한 칩 Row */
@Composable
private fun YesNoRow(
    label: String,
    value: Boolean?,
    onChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = value == true,
                onClick = { onChange(true) },
                label = { Text("Y") }
            )
            FilterChip(
                selected = value == false,
                onClick = { onChange(false) },
                label = { Text("N") }
            )
        }
    }
}

/** 텍스트 입력 필드(라벨 위, 박스형) */
@Composable
private fun LabeledTextField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    placeholder: String = "",
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            placeholder = { if (placeholder.isNotBlank()) Text(placeholder) },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray),
            minLines = 3
        )
    }
}

/** 페이지 본문 */
@Composable
fun FirstScreen(
    vm: SurveyViewModel,
    navController: NavController
) {
    val state by vm.uiState.collectAsStateWithLifecycle()

    // ----- 화면 로컬 상태(저장은 vm.persistStep() 때 repo로 싱크) -----
    var lastCut by remember { mutableStateOf<Boolean?>(null) }
    var lastPerm by remember { mutableStateOf<Boolean?>(null) }
    var lastColor by remember { mutableStateOf<Boolean?>(null) }
    var lastBleach by remember { mutableStateOf<Boolean?>(null) }

    var uncomfortable by remember { mutableStateOf("") }
    var concerns by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = {
            // 하단 Prev / Next 바
            PrevNextBar(
                navController = navController,
                prevRoute = Screen.Intro.route,
                nextRoute = Screen.Second.route,
                nextEnabled = listOf(lastCut, lastPerm, lastColor, lastBleach).any { it != null },
                onBeforeNavigateNext = {
                    vm.updateHair { hair ->
                        hair.copy(
                            lastCut = lastCut,
                            lastPerm = lastPerm,
                            lastColor = lastColor,
                            lastBleach = lastBleach
                        )
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
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
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

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = stringResource(R.string.last_treatment_details))
                }

                // ---- 컷/펌/컬러/탈색: 개별 Y/N ----
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    YesNoRow(
                        label = "컷 (Y/N)",
                        value = lastCut,
                        onChange = { lastCut = it }
                    )
                    YesNoRow(
                        label = "펌 (Y/N)",
                        value = lastPerm,
                        onChange = { lastPerm = it }
                    )
                    YesNoRow(
                        label = "컬러 (Y/N)",
                        value = lastColor,
                        onChange = { lastColor = it }
                    )
                    YesNoRow(
                        label = "탈색 (Y/N)",
                        value = lastBleach,
                        onChange = { lastBleach = it }
                    )
                }

                Spacer(Modifier.height(8.dp))

                // ---- 자유 입력 3개 ----
                LabeledTextField(
                    label = stringResource(R.string.last_treatment_uncomfortable),
                    value = uncomfortable,
                    onChange = { uncomfortable = it },
                    modifier = Modifier.fillMaxWidth()
                )

                LabeledTextField(
                    label = stringResource(R.string.last_treatment_concerned),
                    value = concerns,
                    onChange = { concerns = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun HairConsultPagePreview() {
    LOABLABHannamApplicationTheme {
        // ✅ 프리뷰용 FakeRepository + ViewModel
        val fakeVm = remember {
            SurveyViewModel(
                repo = FakeSurveyRepository()   // 이미 만드셨던 FakeSurveyRepository
            )
        }
        val navController = rememberNavController()

        FirstScreen(
            vm = fakeVm,
            navController = navController
        )
    }
}

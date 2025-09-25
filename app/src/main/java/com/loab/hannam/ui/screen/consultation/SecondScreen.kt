package com.loab.hannam.ui.screen.consultation

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
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

// --- 중요 요소(다국어 대응 위해 @StringRes 사용) ---
enum class ImportantTag(@StringRes val label: Int) {
    NATURAL(R.string.most_important_hairstyle_option_natural),
    BLOW(R.string.most_important_hairstyle_option_dry),
    ROLL(R.string.most_important_hairstyle_option_rolldry);
}

enum class StyleLevel(@StringRes val label: Int) {
    DEVICE(R.string.styling_level_option_use_device),
    EVERYDAY(R.string.styling_level_option_use_everyday),
    WEEK(R.string.styling_level_option_use_week)   // 주 n회(숫자 필요)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SecondScreen(
    vm: SurveyViewModel,
    navController: NavController
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    val scroll = rememberScrollState()

    // ----- 화면 상태 -----
    var precautions by remember { mutableStateOf(state.hair.precautions) }

    // 중요포인트 다중선택 (최대 3개)
    var selectedImportant by remember {
        mutableStateOf(
            state.hair.importantInStyle.mapNotNull { str ->
                ImportantTag.entries.find { it.name == str }
            }.toSet()
        )
    }

    // 스타일링 레벨
    var selectedLevels by remember {
        mutableStateOf(
            state.hair.stylingLevel.mapNotNull { s ->
                when {
                    s.startsWith("WEEK:") -> StyleLevel.WEEK
                    s == StyleLevel.DEVICE.name -> StyleLevel.DEVICE
                    s == StyleLevel.EVERYDAY.name -> StyleLevel.EVERYDAY
                    else -> null
                }
            }.toSet()
        )
    }
    var weekTimesText by remember {
        val week = state.hair.stylingLevel.firstOrNull { it.startsWith("WEEK:") }
        mutableStateOf(week?.removePrefix("WEEK:") ?: "")
    }

    Scaffold(
        bottomBar = {
            // 하단 Prev / Next
            PrevNextBar(
                navController = navController,
                prevRoute = Screen.First.route,   // 이전/다음 라우트는 플로우에 맞게 변경
                nextRoute = Screen.Third.route,
                nextEnabled = true, // 필요 시 검증 로직 추가
                onBeforeNavigateNext = {
                    // 저장: 모델 구조를 바꾸지 않기 위해 문자열 리스트로 인코딩
                    val importantSaved: List<String> = selectedImportant.map { it.name }

                    val stylingSaved = buildList {
                        if (StyleLevel.DEVICE in selectedLevels) add(StyleLevel.DEVICE.name)
                        if (StyleLevel.EVERYDAY in selectedLevels) add(StyleLevel.EVERYDAY.name)
                        if (StyleLevel.WEEK in selectedLevels && weekTimesText.isNotBlank())
                            add("WEEK:${weekTimesText}")   // 예: WEEK:3
                    }

                    vm.updateHair { hair ->
                        hair.copy(
                            precautions = precautions,
                            importantInStyle = importantSaved,
                            stylingLevel = stylingSaved
                        )
                    }
                    vm.persistStep()
                    true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
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

                // 1) 시술시 요청/주의
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.last_treatment_requested),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(24.dp))
                    OutlinedTextField(
                        value = precautions,
                        onValueChange = { precautions = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray),
                        minLines = 3
                    )
                }

                // 2) 헤어스타일에서 가장 중요시 여기는 부분
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // 2) 헤어스타일에서 가장 중요시 여기는 부분
                    Text(
                        text = stringResource(R.string.most_important_hairstyle),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ImportantTag.entries.forEach { tag ->
                            val checked = selectedImportant.contains(tag)

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Checkbox(
                                    checked = checked,
                                    onCheckedChange = { isChecked ->
                                        val next = when {
                                            isChecked && selectedImportant.size < 3 -> selectedImportant + tag
                                            !isChecked -> selectedImportant - tag
                                            else -> selectedImportant
                                        }
                                        selectedImportant = next
                                        // 저장은 키로(= enum name)
                                        vm.updateHair { hair ->
                                            hair.copy(importantInStyle = next.map { it.name })
                                        }
                                    }
                                )
                                Text(
                                    text = stringResource(tag.label),
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                    }
                }

                // 3) 스타일링 레벨
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.styling_level),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    // 기기사용
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = StyleLevel.DEVICE in selectedLevels,
                            onCheckedChange = { checked ->
                                selectedLevels = if (checked) selectedLevels + StyleLevel.DEVICE
                                else selectedLevels - StyleLevel.DEVICE
                            }
                        )
                        Text(text = stringResource(StyleLevel.DEVICE.label))
                    }
                    // 매일사용
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = StyleLevel.EVERYDAY in selectedLevels,
                            onCheckedChange = { checked ->
                                selectedLevels = if (checked) selectedLevels + StyleLevel.EVERYDAY
                                else selectedLevels - StyleLevel.EVERYDAY
                            }
                        )
                        Text(text = stringResource(StyleLevel.EVERYDAY.label))
                    }
                    // 주 n회
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = StyleLevel.WEEK in selectedLevels,
                            onCheckedChange = { checked ->
                                selectedLevels = if (checked) selectedLevels + StyleLevel.WEEK
                                else {
                                    weekTimesText = ""
                                    selectedLevels - StyleLevel.WEEK
                                }
                            }
                        )
                        Text(text = stringResource(StyleLevel.WEEK.label))
                        Spacer(Modifier.width(8.dp))
                        OutlinedTextField(
                            value = weekTimesText,
                            onValueChange = { new ->
                                // 숫자만 허용
                                if (new.all { it.isDigit() } && new.length <= 2) {
                                    weekTimesText = new
                                }
                            },
                            enabled = StyleLevel.WEEK in selectedLevels,
                            modifier = Modifier
                                .width(30.dp)
                                .height(30.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Text(text = stringResource(R.string.styling_level_option_use_times))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PreferenceSecondPagePreview() {
    LOABLABHannamApplicationTheme {
        // 초기 상태 세팅
        val initialState = SurveyState(
            hair = HairChecklist(
                precautions = "",
                importantInStyle = listOf("자연건조"),
                stylingLevel = listOf("매일사용")
            )
        )

        // Fake Repository + VM
        val vm = remember { SurveyViewModel(FakeSurveyRepository(initialState)) }
        val navController = rememberNavController()

        SecondScreen(
            vm = vm,
            navController = navController
        )
    }
}

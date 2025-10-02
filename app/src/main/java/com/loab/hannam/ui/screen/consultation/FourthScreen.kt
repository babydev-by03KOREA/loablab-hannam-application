package com.loab.hannam.ui.screen.consultation

import androidx.annotation.StringRes
import androidx.compose.foundation.background
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

enum class ManyOrLessTag(@StringRes val label: Int) {
    MANY(R.string.many),
    LESS(R.string.less),
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FourthScreen(
    vm: SurveyViewModel,
    navController: NavController
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    val scroll = rememberScrollState()

    // 기존 저장값을 enum으로 역변환
    var selectedLayer by remember(state.hair.layerLevel) {
        mutableStateOf(
            state.hair.layerLevel
                .let { saved -> ManyOrLessTag.entries.find { it.name == saved } }
        )
    }
    var selectedThinning by remember(state.hair.thinningLevel) {
        mutableStateOf(
            state.hair.thinningLevel
                .let { saved -> ManyOrLessTag.entries.find { it.name == saved } }
        )
    }

    var todayDesign by remember { mutableStateOf(state.hair.todayDesign) }

    Scaffold(
        bottomBar = {
            PrevNextBar(
                navController = navController,
                prevRoute = Screen.Third.route,
                nextRoute = Screen.Fifth.route,
                nextEnabled = (selectedLayer != null && selectedThinning != null),
                onBeforeNavigateNext = {
                    vm.updateHair { hair ->
                        hair.copy(
                            layerLevel = selectedLayer?.name ?: "",
                            thinningLevel = selectedThinning?.name ?: ""
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

                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.layer),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ManyOrLessTag.entries.forEach { tag ->
                            val checked = selectedLayer == tag

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Checkbox(
                                    checked = checked,
                                    onCheckedChange = {
                                        selectedLayer = if (checked) null else tag
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

                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.thick_layer),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ManyOrLessTag.entries.forEach { tag ->
                            val checked = selectedThinning == tag

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Checkbox(
                                    checked = checked,
                                    onCheckedChange = {
                                        selectedThinning = if (checked) null else tag
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

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.today_want_today),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(24.dp))
                    OutlinedTextField(
                        value = todayDesign,
                        onValueChange = { todayDesign = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray),
                        minLines = 3
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PreferenceFourthPagePreview() {
    LOABLABHannamApplicationTheme {
        // 초기 상태 세팅
        val initialState = SurveyState(
            hair = HairChecklist(
                layerLevel = "",
                thinningLevel = "",
                todayDesign = ""
            )
        )

        // Fake Repository + VM
        val vm = remember { SurveyViewModel(FakeSurveyRepository(initialState)) }
        val navController = rememberNavController()

        FourthScreen(
            vm = vm,
            navController = navController
        )
    }
}
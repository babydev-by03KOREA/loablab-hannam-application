package com.loab.hannam.ui.screen.consultation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.loab.hannam.ui.preview.FakeSurveyRepository
import com.loab.hannam.ui.theme.LOABLABHannamApplicationTheme

@Composable
fun FourthScreen(
    vm: SurveyViewModel,
    navController: NavController
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    val scroll = rememberScrollState()

    Scaffold (
        bottomBar = {

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
package com.loab.hannam.ui.screen.result

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.loab.hannam.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.loab.hannam.data.model.HairChecklist
import com.loab.hannam.data.model.SurveyState
import com.loab.hannam.ui.ReportUiState
import com.loab.hannam.ui.SurveyViewModel
import com.loab.hannam.ui.components.SaveButton
import com.loab.hannam.ui.preview.FakeSurveyRepository
import com.loab.hannam.ui.screen.navigation.Screen
import com.loab.hannam.ui.theme.LOABLABHannamApplicationTheme
import kotlinx.coroutines.launch

@Composable
fun ResultScreen(
    vm: SurveyViewModel,
    navController: NavController
) {
    val report by vm.report.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    var saving by remember { mutableStateOf(false) }

    // 화면 진입 시 한 번 생성
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        vm.generateReport(context)
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 본문: 상태별 UI
            when (val s = report) {
                is ReportUiState.Generating, ReportUiState.Idle -> {
                    // 로딩
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(Modifier.height(16.dp))
                            Text(stringResource(R.string.generating_report)) // "시술상담지 생성중…"
                        }
                    }
                }

                is ReportUiState.Ready -> {
                    // 완성된 보고서 미리보기
                    val bmp = s.bitmap
                    val ratio = remember(bmp) { bmp.width.toFloat() / bmp.height.toFloat() }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = "generated report",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(ratio)
                        )
                    }

                    SaveButton(
                        onSave = {
                            scope.launch {
                                if (saving) return@launch
                                saving = true
                                val ok = vm.renderAndSave(context) // suspend 함수라고 가정
                                saving = false

                                Toast
                                    .makeText(
                                        context,
                                        if (ok) context.getString(R.string.saved_success)
                                        else context.getString(R.string.saved_fail),
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()

                                if (ok) {
                                    // 감사 화면으로 이동
                                    navController.navigate(Screen.ThankYou.route)
                                }
                            }
                        },
                        enabled = !saving // 저장 중엔 비활성화
                    )

                }

                is ReportUiState.Error -> {
                    // 에러 + 재시도
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = stringResource(R.string.generate_failed),
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = { vm.regenerateReport(context) }) {
                                Text(stringResource(R.string.retry))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val initialState = SurveyState(
        hair = HairChecklist(
            layerLevel = "",
            thinningLevel = "",
            todayDesign = ""
        )
    )

    val vm = remember { SurveyViewModel(FakeSurveyRepository(initialState)) }
    val navController = rememberNavController()

    LOABLABHannamApplicationTheme {
        ResultScreen(
            vm = vm,
            navController = navController
        )
    }
}
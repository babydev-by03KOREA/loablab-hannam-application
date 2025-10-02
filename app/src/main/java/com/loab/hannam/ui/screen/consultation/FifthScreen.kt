package com.loab.hannam.ui.screen.consultation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
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

@Composable
fun FifthScreen(
    vm: SurveyViewModel,
    navController: NavController
) {
    val state by vm.uiState.collectAsStateWithLifecycle()

    // 저장된 값 있으면 초기 선택으로
    var faceShapeIndex by remember(state.hair.faceShapeIndex) {
        mutableStateOf(state.hair.faceShapeIndex)
    }

    // drawable 리소스(얼굴형 6개) 배열
    val faces = remember {
        listOf(
            R.drawable.face_oval,     // 0
            R.drawable.face_square,   // 1
            R.drawable.face_rectangle,// 2
            R.drawable.face_diamond,  // 3
            R.drawable.face_heart,    // 4
            R.drawable.face_round     // 5
        )
    }

    Scaffold(
        bottomBar = {
            PrevNextBar(
                navController = navController,
                prevRoute = Screen.Fourth.route,
                nextRoute = Screen.Result.route,
                nextEnabled = (faceShapeIndex != null),
                onBeforeNavigateNext = {
                    vm.updateHair { hair ->
                        hair.copy(
                            faceShapeIndex = faceShapeIndex,
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
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
                    .padding(paddingValues),
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

                // 3x2 그리드
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 240.dp), // 원하는 만큼
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    userScrollEnabled = false // 외부 Column 스크롤 사용 시
                ) {
                    items(faces.size) { index ->
                        FaceChoiceItem(
                            drawableId = faces[index],
                            selected = faceShapeIndex == index,
                            onClick = { faceShapeIndex = index }
                        )
                    }
                }
            }
        }
    }
}

/** 개별 얼굴 셀: 이미지 + 선택 오버레이(빨간 체크/테두리) */
@Composable
private fun FaceChoiceItem(
    @DrawableRes drawableId: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (selected) Color.Red else Color.Transparent
    val borderWidth = if (selected) 2.dp else 0.dp

    Box(
        modifier = Modifier
            .aspectRatio(1f) // 정사각형 셀
            .clip(RoundedCornerShape(8.dp))
            .border(borderWidth, borderColor, RoundedCornerShape(8.dp))
            .background(Color.White)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(drawableId),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentScale = ContentScale.Fit
        )

        if (selected) {
            // 우상단 빨간 체크 표시
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PreferenceFifthPagePreview() {
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

        FifthScreen(
            vm = vm,
            navController = navController
        )
    }
}
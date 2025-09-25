package com.loab.hannam.ui.screen.consultation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.loab.hannam.R
import com.loab.hannam.ui.SurveyViewModel
import com.loab.hannam.ui.components.LangBar
import com.loab.hannam.ui.components.PrevNextBar
import com.loab.hannam.ui.preview.FakeSurveyRepository
import com.loab.hannam.ui.screen.navigation.Screen
import com.loab.hannam.ui.theme.LOABLABHannamApplicationTheme

@Composable
fun NameScreenContent(
    vm: SurveyViewModel,
    navController: NavController,
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    var name by remember { mutableStateOf(state.customer.name) }

    // 하단 Prev / Next 바
    Scaffold(
        bottomBar = {
            PrevNextBar(
                navController = navController,
                prevRoute = Screen.Start.route,
                nextRoute = Screen.First.route,
                nextEnabled = name.isNotBlank(),
                onBeforeNavigateNext = {
                    vm.setName(name)
                    vm.persistStep()
                    true
                }
            )
        }
    ) { padding ->
        LangBar(
            currentLang = state.customer.localeTag,
            onSelectLang = { vm.setLocale(it) }
        )

        // 본문
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.customer_info),
                fontSize = 40.sp,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(50.dp))

            Text(
                text = stringResource(R.string.customer_name),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.LightGray,
                    focusedContainerColor = Color.LightGray,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun NameScreen(
    vm: SurveyViewModel,
    navController: NavController
) {
    NameScreenContent(
        vm = vm,
        navController = navController
    )
}


@Preview(showBackground = true)
@Composable
fun StartScreenPreview() {
    LOABLABHannamApplicationTheme {
        val fakeVm = remember {
            SurveyViewModel(
                repo = FakeSurveyRepository()   // 이미 만드셨던 FakeSurveyRepository
            )
        }
        val navController = rememberNavController()

        NameScreenContent(
            vm = fakeVm,
            navController = navController
        )
    }
}

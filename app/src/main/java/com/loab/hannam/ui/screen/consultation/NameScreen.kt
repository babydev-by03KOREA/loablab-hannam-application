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
import com.loab.hannam.R
import com.loab.hannam.ui.SurveyViewModel
import com.loab.hannam.ui.screen.navigation.Screen
import com.loab.hannam.ui.theme.LOABLABHannamApplicationTheme

@Composable
fun NameScreenContent(
    name: String,
    onNameChange: (String) -> Unit,
    onNextClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.customer_info),
            fontSize = 40.sp,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(100.dp))

        Text(
            text = stringResource(R.string.customer_name),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.LightGray, // 비포커스 배경 색상
                focusedContainerColor = Color.LightGray,   // 포커스 배경 색상
                unfocusedIndicatorColor = Color.Transparent, // 비포커스 테두리 제거
                focusedIndicatorColor = Color.Transparent    // 포커스 테두리 제거
            )
        )

        Spacer(Modifier.height(24.dp))


    }
}

@Composable
fun NameScreen(
    vm: SurveyViewModel,
    navController: NavController
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    var name by remember { mutableStateOf(state.customer.name) }

    NameScreenContent(
        name = name,
        onNameChange = { name = it },
        onNextClick = {
            if (name.isNotBlank()) {
                vm.setName(name)
                navController.navigate(Screen.Intro.route)
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun StartScreenPreview() {
    LOABLABHannamApplicationTheme {
        NameScreenContent(
            name = "홍길동",
            onNameChange = {},
            onNextClick = {}
        )
    }
}

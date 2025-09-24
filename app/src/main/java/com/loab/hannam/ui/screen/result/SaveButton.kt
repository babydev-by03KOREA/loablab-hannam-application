package com.loab.hannam.ui.screen.result

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.loab.hannam.R
import com.loab.hannam.data.model.SurveyState

@Composable
fun SaveButton(
    state: SurveyState,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(onClick = onSave, modifier = modifier) {
        Text(text = stringResource(R.string.save_image))
    }
}
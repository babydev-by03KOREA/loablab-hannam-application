package com.loab.hannam.ui.screen.result

import android.widget.Toast
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.loab.hannam.R
import com.loab.hannam.report.ReportRenderer
import com.loab.hannam.report.ReportSaver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.loab.hannam.data.model.CustomerInfo
import com.loab.hannam.data.model.SurveyState
import com.loab.hannam.ui.SurveyViewModel
import com.loab.hannam.ui.theme.LOABLABHannamApplicationTheme

@Composable
fun ResultActions(
    vm: SurveyViewModel
) {
    val context = LocalContext.current
    val state by vm.uiState.collectAsStateWithLifecycle()

    SaveButton(
        state = state,
        onSave = {
            val uri = ReportSaver.savePng(
                context, ReportRenderer.render(state),
                "Loab_${state.customer.name}_${System.currentTimeMillis()}"
            )
            Toast.makeText(
                context,
                if (uri != null) context.getString(R.string.saved_success) else context.getString(R.string.saved_fail),
                Toast.LENGTH_SHORT
            ).show()
        }
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val fake = SurveyState(customer = CustomerInfo(name = "홍길동"))
    LOABLABHannamApplicationTheme {
        SaveButton(
            state = fake,
            onSave = {}
        )
    }
}
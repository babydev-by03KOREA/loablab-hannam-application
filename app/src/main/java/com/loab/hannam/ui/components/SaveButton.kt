package com.loab.hannam.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.loab.hannam.R
import com.loab.hannam.data.model.SurveyState

@Composable
fun SaveButton(
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onSave,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black,
        ),
    ) {
        Row {
            Icon(
                Icons.Default.SaveAlt,
                contentDescription = "SaveAlt",
                tint = Color.White
            )
            Spacer(Modifier.width(10.dp))
            Text(text = stringResource(R.string.save_image))
        }
    }
}
package com.loab.hannam.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LangBar(
    currentLang: String,
    onSelectLang: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = 16.dp)
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LangChip(text = "한국어", selected = currentLang == "ko") { onSelectLang("ko") }
        Spacer(Modifier.width(12.dp))
        LangChip(text = "中文", selected = currentLang == "zh") { onSelectLang("zh") }
        Spacer(Modifier.width(12.dp))
        LangChip(text = "EN", selected = currentLang == "en") { onSelectLang("en") }
        Spacer(Modifier.width(12.dp))
        LangChip(text = "日本語", selected = currentLang == "ja") { onSelectLang("ja") }
    }
}


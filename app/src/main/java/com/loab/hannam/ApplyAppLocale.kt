package com.loab.hannam

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

@Composable
fun ApplyAppLocale(localeTag: String, content: @Composable () -> Unit) {
    val context = LocalContext.current
    val configuration = context.resources.configuration
    val locale = Locale(localeTag)
    configuration.setLocale(locale)

    val localizedContext = context.createConfigurationContext(configuration)

    CompositionLocalProvider(
        LocalContext provides localizedContext
    ) {
        content()
    }
}

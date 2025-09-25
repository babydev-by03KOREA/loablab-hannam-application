package com.loab.hannam.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun PrevNextBar(
    navController: NavController,
    prevRoute: String?,
    nextRoute: String?,
    modifier: Modifier = Modifier,
    nextEnabled: Boolean = true,
    onBeforeNavigateNext: (() -> Boolean)? = null,
    ) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        if (prevRoute != null) {
            TextButton (
                onClick = { navController.navigate(prevRoute) { launchSingleTop = true } }
            ) { Text("Previous") }
        } else {
            Spacer(Modifier.width(8.dp))
        }

        if (nextRoute != null) {
            TextButton(
                enabled = nextEnabled,
                onClick = {
                    val go = onBeforeNavigateNext?.invoke() ?: true
                    if (go) navController.navigate(nextRoute) { launchSingleTop = true }
                }
            ) { Text("Next") }
        }
    }
}
package com.loab.hannam.ui

sealed interface ReportUiState {
    data object Idle : ReportUiState
    data object Generating : ReportUiState
    data class Ready(val bitmap: android.graphics.Bitmap) : ReportUiState
    data class Error(val message: String) : ReportUiState
}
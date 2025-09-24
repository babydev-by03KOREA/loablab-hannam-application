package com.loab.hannam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.loab.hannam.data.repository.SurveyRepository
import com.loab.hannam.ui.SurveyViewModel

class SurveyViewModelFactory(
    private val repo: SurveyRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SurveyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SurveyViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown VM class")
    }
}
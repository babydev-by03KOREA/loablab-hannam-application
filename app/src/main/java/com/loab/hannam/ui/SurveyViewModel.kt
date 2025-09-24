package com.loab.hannam.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loab.hannam.data.model.HairChecklist
import com.loab.hannam.data.model.SurveyState
import com.loab.hannam.data.repository.SurveyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * @desc
 * 화면 상태(State)와 로직을 총괄
 * UI 로직(입력/갱신/저장) 담당
 * */
class SurveyViewModel(
    private val repo: SurveyRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SurveyState())
    val uiState: StateFlow<SurveyState> = _uiState

    init {
        viewModelScope.launch {
            repo.state.collect { _uiState.value = it }
        }
    }

    fun setName(name: String) = mutate { it.copy(customer = it.customer.copy(name = name)) }
    fun setLocale(tag: String) = mutate { it.copy(customer = it.customer.copy(localeTag = tag)) }

    fun updateHair(block: (HairChecklist) -> HairChecklist) = mutate {
        it.copy(hair = block(it.hair))
    }

    fun markFinished() = mutate { it.copy(finished = true) }

    fun persist() {
        viewModelScope.launch { repo.save(_uiState.value) }
    }

    private fun mutate(change: (SurveyState) -> SurveyState) {
        _uiState.update(change)

        // 필요 시 즉시 저장(자동 저장)
        // viewModelScope.launch { repo.save(_uiState.value) }
    }
}
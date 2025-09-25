package com.loab.hannam.ui.preview

import com.loab.hannam.data.model.SurveyState
import com.loab.hannam.data.repository.SurveyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeSurveyRepository(
    initial: SurveyState = SurveyState()
) : SurveyRepository {

    private val _state = MutableStateFlow(initial)
    override val state: StateFlow<SurveyState> get() = _state

    // 저장하면 그냥 최신 상태로 덮어쓰기
    override suspend fun save(state: SurveyState) {
        _state.value = state
    }

    // 현재 상태에 block을 적용해 갱신
    override suspend fun update(block: (SurveyState) -> SurveyState) {
        _state.value = block(_state.value)
    }
}
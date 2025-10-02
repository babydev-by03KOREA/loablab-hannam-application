package com.loab.hannam.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loab.hannam.data.model.HairChecklist
import com.loab.hannam.data.model.SurveyState
import com.loab.hannam.data.repository.SurveyRepository
import com.loab.hannam.report.ReportRenderer
import com.loab.hannam.report.ReportSaver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    /** 단계 진행 전에 현재 상태를 저장 (선택) */
    fun persistStep() {
        viewModelScope.launch {
            repo.save(_uiState.value)
        }
    }

    /** Concern(고민) 단계 유효성 검사 예시 */
//    fun validateConcern(): Boolean =
//        _uiState.value.hair.currentConcerns.isNotBlank()

    /** Concern 단계에서 Next 버튼 활성화 여부 */
//    fun canProceedFromConcern(): Boolean = validateConcern()

    private fun mutate(change: (SurveyState) -> SurveyState) {
        _uiState.update(change)

        // 필요 시 즉시 저장(자동 저장)
        // viewModelScope.launch { repo.save(_uiState.value) }
    }

    private val _report = MutableStateFlow<ReportUiState>(ReportUiState.Idle)
    val report: StateFlow<ReportUiState> = _report

    /** 결과 이미지 생성 트리거 */
    fun generateReport() {
        // 중복 호출 방지
        if (_report.value is ReportUiState.Generating) return

        _report.value = ReportUiState.Generating
        val snapshot = _uiState.value

        viewModelScope.launch(Dispatchers.Default) {
            try {
                // 무거운 비트맵 렌더링은 Default(백그라운드)에서
                val bmp = ReportRenderer.render(snapshot)  // 이미 만들어 둔 ReportRenderer 사용
                withContext(Dispatchers.Main) {
                    _report.value = ReportUiState.Ready(bmp)
                }
            } catch (t: Throwable) {
                withContext(Dispatchers.Main) {
                    _report.value = ReportUiState.Error(t.message ?: "unknown error")
                }
            }
        }
    }

    /** 다시 만들기(에러/재시도) */
    fun regenerateReport() {
        _report.value = ReportUiState.Idle
        generateReport()
    }

    fun resetSurvey() {
        // UI 메모리 상태 리셋
        _uiState.value = SurveyState()
        // 결과 상태도 리셋(이미지/로딩)
        _report.value = ReportUiState.Idle
        // 디스크에도 초기 상태 저장
        viewModelScope.launch {
            repo.overwrite(SurveyState())
        }
    }

    /** 결과 비트맵 렌더 + 저장 (백그라운드 스레드) */
    suspend fun renderAndSave(context: Context): Boolean = withContext(Dispatchers.Default) {
        return@withContext try {
            val bmp = ReportRenderer.render(_uiState.value, context)
            val fileName = "Loab_${_uiState.value.customer.name}_${System.currentTimeMillis()}"
            val uri = ReportSaver.savePng(context, bmp, fileName)
            uri != null
        } catch (_: Throwable) {
            false
        }
    }
}
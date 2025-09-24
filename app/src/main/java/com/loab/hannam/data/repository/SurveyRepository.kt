package com.loab.hannam.data.repository

import com.loab.hannam.data.model.SurveyState
import kotlinx.coroutines.flow.Flow

interface SurveyRepository {
    val state: Flow<SurveyState>
    suspend fun save(partial: SurveyState)
    suspend fun update(block: (SurveyState) -> SurveyState)
}
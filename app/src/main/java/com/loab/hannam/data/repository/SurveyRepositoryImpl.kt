package com.loab.hannam.data.repository

import com.loab.hannam.data.model.SurveyState
import com.loab.hannam.data.store.SurveyLocalStore
import kotlinx.coroutines.flow.Flow

/**
 * @desc
 * ViewModel이 직접 DataStore를 다루지 않게 중간 다리 역할
 * */
class SurveyRepositoryImpl(
    private val store: SurveyLocalStore
) : SurveyRepository {
    override val state: Flow<SurveyState> = store.stateFlow
    override suspend fun save(partial: SurveyState) = store.overwrite(partial)
    override suspend fun update(block: (SurveyState) -> SurveyState) = store.update(block)
}
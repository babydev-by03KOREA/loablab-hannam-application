package com.loab.hannam.data.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.loab.hannam.data.model.SurveyState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

/**
 * @desc
 * 구조화된 데이터 로컬 저장을 위한 kotlinx.serialization + DataStore
 * DataStore를 이용해서 설문 데이터를 JSON 형태로 로컬에 저장/불러오기
 * */
object SurveyStateSerializer : Serializer<SurveyState> {
    override val defaultValue: SurveyState = SurveyState()
    override suspend fun readFrom(input: InputStream): SurveyState = try {
        Json.decodeFromString<SurveyState>(input.readBytes().decodeToString())
    } catch (_: SerializationException) {
        defaultValue
    }

    override suspend fun writeTo(t: SurveyState, output: OutputStream) {
        output.write(Json.encodeToString(t).encodeToByteArray())
    }
}

private val Context.surveyDataStore: DataStore<SurveyState> by dataStore(
    fileName = "survey_state.json",
    serializer = SurveyStateSerializer
)

class SurveyLocalStore(private val context: Context) {
    val stateFlow: Flow<SurveyState> = context.surveyDataStore.data
        .catch { emit(SurveyState()) }

    suspend fun update(transform: (SurveyState) -> SurveyState) {
        context.surveyDataStore.updateData { old -> transform(old) }
    }

    suspend fun overwrite(new: SurveyState) {
        context.surveyDataStore.updateData { new }
    }
}
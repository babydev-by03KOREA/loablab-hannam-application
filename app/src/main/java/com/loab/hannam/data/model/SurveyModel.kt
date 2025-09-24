package com.loab.hannam.data.model

import kotlinx.serialization.Serializable

/**
 * @desc
 * 단일 ViewModel로 전 단계 상태 관리
 * 화면에서 입력한 설문 데이터를 구조화해서 담는 클래스
 * */
@Serializable
data class CustomerInfo(
    val name: String = "",
    val dateMillis: Long = System.currentTimeMillis(),
    val localeTag: String = "ko"            // "ko", "zh", "en", "ja"
)

@Serializable
data class HairChecklist(
    val lastServices: String = "",          // "컷(Y/M) 펌(Y/M) …"
    val previousIssues: String = "",
    val currentConcerns: String = "",
    val precautions: String = "",           // 알러지/두피상태 등
    val importantInStyle: List<String> = emptyList(),  // 자연건조/드라이…
    val stylingLevel: List<String> = emptyList(),      // 기기사용/매일사용…
    val usualImage: List<String> = emptyList(),        // 스탠다드/부드러운/…
    val preferredImage: List<String> = emptyList(),
    val layerLevel: String = "",            // "많이"/"적게"
    val thinningLevel: String = "",         // "많이"/"적게"
    val todayDesign: String = "",
    val faceShapeIndex: Int? = null         // 0..5 (선택 이미지)
)

@Serializable
data class SurveyState(
    val customer: CustomerInfo = CustomerInfo(),
    val hair: HairChecklist = HairChecklist(),
    val finished: Boolean = false
)
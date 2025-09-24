package com.loab.hannam.report

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import com.loab.hannam.data.model.SurveyState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.graphics.createBitmap

/**
 * @desc
 * Canvas로 비트맵/PNG 생성
 * 설문 내용을 이미지로 변환하는 “렌더러”
 * */
object ReportRenderer {
    fun render(state: SurveyState, width: Int = 1080, height: Int = 1528): Bitmap {
        val bmp = createBitmap(width, height)
        val canvas = Canvas(bmp)
        canvas.drawColor(Color.WHITE)

        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK; textSize = 48f; typeface =
            Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK; textSize = 32f
        }
        val gray = Paint().apply { color = Color.LTGRAY }

        var y = 120f
        canvas.drawText("HAIR CONDITION CHECK LIST", 64f, y, titlePaint)
        y += 64

        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            .format(Date(state.customer.dateMillis))
        drawRow(canvas, "DATE", dateStr, 64f, y, textPaint); y += 64
        drawRow(canvas, "NAME", state.customer.name, 64f, y, textPaint); y += 64

        // 간단히 주요 항목만 예시
        fun bullet(label: String, value: String) {
            y += 24; drawRow(canvas, label, value, 64f, y, textPaint); y += 56
        }
        bullet("Last Services", state.hair.lastServices)
        bullet("Previous Issues", state.hair.previousIssues)
        bullet("Concerns", state.hair.currentConcerns)
        bullet("Precautions", state.hair.precautions)
        bullet("Important", state.hair.importantInStyle.joinToString(", "))
        bullet("Styling Level", state.hair.stylingLevel.joinToString(", "))
        bullet("Usual Image", state.hair.usualImage.joinToString(", "))
        bullet("Preferred Image", state.hair.preferredImage.joinToString(", "))
        bullet("Layer", state.hair.layerLevel)
        bullet("Thinning", state.hair.thinningLevel)
        bullet("Today Design", state.hair.todayDesign)

        // 간단한 워터마크
        canvas.drawRect(Rect(0, (height * 0.75f).toInt(), width, height), gray.apply { alpha = 30 })
        return bmp
    }

    private fun drawRow(
        canvas: Canvas, label: String, value: String, x: Float, y: Float, paint: Paint
    ) {
        canvas.drawText("$label : $value", x, y, paint)
    }
}
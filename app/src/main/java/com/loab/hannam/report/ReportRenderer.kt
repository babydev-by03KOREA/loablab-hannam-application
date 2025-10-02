package com.loab.hannam.report

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.util.TypedValue
import com.loab.hannam.data.model.SurveyState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.graphics.createBitmap
import com.loab.hannam.R

/**
 * @desc
 * Canvas로 비트맵/PNG 생성
 * 설문 내용을 이미지로 변환하는 “렌더러”
 * */
object ReportRenderer {

    // ===== 공통 유틸 =====
    private fun Float.dp() = this * Resources.getSystem().displayMetrics.density
    private fun spToPx(sp: Float) =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp,
            Resources.getSystem().displayMetrics
        )

    private fun newPaint(
        color: Int = Color.BLACK,
        sizeSp: Float = 14f,
        bold: Boolean = false,
        align: Paint.Align = Paint.Align.LEFT
    ) = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color
        textSize = spToPx(sizeSp)
        textAlign = align
        typeface =
            Typeface.create(Typeface.SANS_SERIF, if (bold) Typeface.BOLD else Typeface.NORMAL)
    }

    private fun checkbox(canvas: Canvas, cx: Float, cy: Float, size: Float, checked: Boolean) {
        val half = size / 2f
        val r = RectF(cx - half, cy - half, cx + half, cy + half)
        val box = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        canvas.drawRoundRect(r, 4f, 4f, box)
        if (checked) {
            val mark = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.BLACK
                style = Paint.Style.STROKE
                strokeWidth = 4f
                strokeCap = Paint.Cap.ROUND
            }
            // 간단한 체크 모양
            canvas.drawLine(r.left + size * 0.20f, cy, cx, r.bottom - size * 0.20f, mark)
            canvas.drawLine(
                cx,
                r.bottom - size * 0.20f,
                r.right - size * 0.18f,
                r.top + size * 0.22f,
                mark
            )
        }
    }

    private fun drawLabelValueRow(
        canvas: Canvas,
        y: Float,
        label: String,
        value: String,
        leftX: Float,
        rightX: Float,
        labelPaint: Paint,
        valuePaint: Paint
    ) {
        canvas.drawText(label, leftX, y, labelPaint)
        canvas.drawText(value, rightX, y, valuePaint)
    }

    private fun drawDivider(canvas: Canvas, startX: Float, endX: Float, y: Float) {
        val p = Paint().apply {
            color = 0xFFDDDDDD.toInt()
            strokeWidth = 2f
        }
        canvas.drawLine(startX, y, endX, y, p)
    }

    // ===== 실제 렌더 =====
    fun render(
        state: SurveyState,
        context: Context? = null,
        width: Int = 1240,     // A4 비율에 가까운 캔버스 (px)
        height: Int = 1754
    ): Bitmap {
        val bmp = createBitmap(width, height)
        val canvas = Canvas(bmp)
        canvas.drawColor(Color.WHITE)

        // 레이아웃 가이드
        val marginH = 48f
        val leftColX = marginH
        val rightColX = width * 0.40f          // 우측 컬럼 시작
        val contentRight = width - marginH

        var y = 72f

        // 제목 (영문/한글)
        val h1 = newPaint(sizeSp = 28f, bold = true)
        val h2 = newPaint(sizeSp = 16f, bold = true)
        canvas.drawText("HAIR CONDITION CHECK LIST", leftColX, y, h1); y += 28f + 10f
        canvas.drawText("헤어 시술 상담", leftColX, y, h2); y += 24f

        // DATE / NAME 헤더라인
        drawDivider(canvas, leftColX, contentRight, y + 14f)

        val body = newPaint(sizeSp = 14f, bold = true)
        val bodyValue = newPaint(sizeSp = 14f)
        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            .format(Date(state.customer.dateMillis))

        y += 42f
        drawLabelValueRow(canvas, y, "DATE", dateStr, leftColX, rightColX, body, bodyValue)

        y += 36f
        drawLabelValueRow(
            canvas,
            y,
            "NAME",
            state.customer.name,
            leftColX,
            rightColX,
            body,
            bodyValue
        )

        y += 28f
        drawDivider(canvas, leftColX, contentRight, y)

        // 번호 스타일
        val numPaint = newPaint(sizeSp = 15f, bold = true)
        val qPaint = newPaint(sizeSp = 15f)

        fun question(num: Int, text: String) {
            y += 34f
            canvas.drawText("$num.", leftColX, y, numPaint)
            canvas.drawText(text, leftColX + 24f, y, qPaint)
        }

        // 1. 마지막 시술내역… (컷/펌/컬러/탈색) (Y/M)
        question(1, "마지막 시술내역을 모두 작성해주세요.")
        // 라벨
        val labelSmall = newPaint(sizeSp = 14f)
        val ymPaint = newPaint(sizeSp = 14f)
        val checkSize = 18f
        var lineY = y + 28f

        fun ymRow(label: String, value: Boolean?) {
            // "컷 ("  "Y/M"  ")"
            val startX = rightColX
            canvas.drawText(label, startX, lineY, labelSmall)

            val boxY = lineY - 12f
            val boxYCenter = boxY

            // Y
            val yX = startX + 90f
            checkbox(canvas, yX, boxYCenter, checkSize, value == true)
            canvas.drawText("Y", yX + 16f, lineY, ymPaint)

            // N
            val nX = yX + 80f
            checkbox(canvas, nX, boxYCenter, checkSize, value == false)
            canvas.drawText("N", nX + 16f, lineY, ymPaint)

            lineY += 28f
        }

        ymRow("컷", state.hair.lastCut)
        ymRow("펌", state.hair.lastPerm)
        ymRow("컬러", state.hair.lastColor)
        ymRow("탈색", state.hair.lastBleach)
        y = lineY

        // 2~3. 자유기입형 (우측에 괄호 영역)
        fun freeRow(num: Int, text: String, value: String) {
            question(num, text)
            // 대괄호 영역
            val bracketPaint = newPaint(sizeSp = 16f)
            val top = y - 18f
            val h = 36f
            canvas.drawText("[", rightColX, y, bracketPaint)
            canvas.drawText("]", contentRight - 8f, y, bracketPaint)
            // 값
            val v = value.ifBlank { " " }
            canvas.drawText(v, rightColX + 16f, y, bodyValue)
        }

        freeRow(2, "이전 시술에서 불편했던 점이 있다면 적어주세요.", state.hair.lastTreatmentUncomfortable)
        freeRow(3, "현재 스타일중 고민되는 점을 적어주세요.", state.hair.currentConcerns)

        // 4. 시술시 요청사항과 주의 해야할 부분이 있나요?
        freeRow(4, "시술시 요청사항과 주의 해야할 부분이 있나요?", state.hair.precautions)

        // 5. 헤어스타일에서 가장 중요시 여기는 부분은?
        question(5, "헤어스타일에서 가장 중요시 여기는 부분은?")
        // 예시 4개: 자연건조, 드라이건조, 롤드라이, (기기사용/매일사용/주N회)
        fun chipRow(yText: Float, label: String, xStart: Float) =
            canvas.drawText("□ $label", xStart, yText, bodyValue)

        val imp = state.hair.importantInStyle // ["자연건조", ...]라면 체크표시로 표시하고 싶으면 아래 로직 확장
        val rowY1 = y + 28f
        chipRow(rowY1, "자연건조", rightColX)
        chipRow(rowY1, "드라이건조", rightColX + 200f)
        chipRow(rowY1, "롤드라이", rightColX + 400f)

        val rowY2 = rowY1 + 28f
        canvas.drawText("□ 기기사용(고데기, 에어랩)", rightColX, rowY2, bodyValue)
        canvas.drawText("□ 매일사용", rightColX + 320f, rowY2, bodyValue)
        canvas.drawText("□ 주(   )회사용", rightColX + 480f, rowY2, bodyValue)

        y = rowY2

        // 6. 스타일링 레벨은 어떻게 되시나요?
        question(6, "스타일링 레벨은 어떻게 되시나요?")
        val rowY3 = y + 28f
        canvas.drawText("□ 자연건조", rightColX, rowY3, bodyValue)
        canvas.drawText("□ 드라이건조", rightColX + 200f, rowY3, bodyValue)
        canvas.drawText("□ 롤드라이", rightColX + 400f, rowY3, bodyValue)
        y = rowY3

        // 7. 평소 내 스타일과 이미지를 선택해주세요. (최대 3개)
        question(7, "평소 내 스타일과 이미지를 선택해주세요. (최대 3개까지 선택)")
        y = drawTagGrid(canvas, rightColX, y + 16f, bodyValue)

        // 9. 레이어 정도  /  10. 숱 정도
        fun manyLessRow(num: Int, title: String, value: String) {
            question(num, title)
            val row = y + 28f
            val manyChecked = value.equals("MANY", true) || value == "많이"
            val lessChecked = value.equals("LESS", true) || value == "적게"
            checkbox(canvas, rightColX, row - 10f, 18f, manyChecked)
            canvas.drawText("많이", rightColX + 22f, row, bodyValue)
            checkbox(canvas, rightColX + 100f, row - 10f, 18f, lessChecked)
            canvas.drawText("적게", rightColX + 122f, row, bodyValue)
            y = row
        }
        manyLessRow(8, "레이어 정도", state.hair.layerLevel)
        manyLessRow(9, "숱 정도", state.hair.thinningLevel)

        // 11. 오늘하고 싶은 디자인  (자유기입)
        question(10, "오늘하고싶은 디자인")
        val row11 = y + 28f
        canvas.drawText("[", rightColX, row11, bodyValue)
        canvas.drawText("]", contentRight - 8f, row11, bodyValue)
        canvas.drawText(state.hair.todayDesign.ifBlank { " " }, rightColX + 16f, row11, bodyValue)
        y = row11

        // 12. 얼굴형 (6개 아이콘)
        question(11, "자신이 생각하는 얼굴형")
        val gapX = 24f
        val gapY = 24f
        val cellW = 120f
        val cellH = 140f
        val startX = rightColX
        var cx = startX
        var cy = y + 16f

        val faces = listOf(
            R.drawable.face_oval,
            R.drawable.face_square,
            R.drawable.face_rectangle,
            R.drawable.face_diamond,
            R.drawable.face_heart,
            R.drawable.face_round
        )

        val selected = state.hair.faceShapeIndex

        val red = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }

        faces.forEachIndexed { idx, resId ->
            val bm = BitmapFactory.decodeResource(context?.resources, resId)
            val dst = RectF(cx, cy, cx + cellW, cy + cellH)
            // contain fit
            val src = Rect(0, 0, bm.width, bm.height)
            canvas.drawBitmap(bm, src, dst, null)
            if (selected == idx) {
                canvas.drawRoundRect(dst, 10f, 10f, red)
            }
            cx += cellW + gapX
            if ((idx + 1) % 3 == 0) { // 3개씩 줄 바꿈
                cx = startX
                cy += cellH + gapY
            }
        }

        // 워터마크(연한 회색 큰 로고 느낌)
        val mark = Paint().apply { color = 0x10_000000 } // 아주 연하게
        canvas.drawCircle(width * 0.5f, height * 0.55f, width * 0.45f, mark)

        return bmp
    }

    /** 스타일/이미지 체크 라인(3열×N줄) */
    private fun drawTagGrid(
        canvas: Canvas,
        startX: Float,
        startY: Float,
        paint: Paint
    ): Float {
        val tags = listOf(
            "스탠다드", "부드러운", "보이쉬한", "가벼운",
            "캐주얼", "귀여운", "프로페셔널한", "화려한",
            "트렌디", "깔끔한", "유니크", "심플", "모던",
            "시크한", "빈티지한", "꾸안꾸"
        )
        val colW = 200f
        val rowH = 28f
        var x = startX
        var y = startY
        tags.forEachIndexed { i, t ->
            canvas.drawText("□ $t", x, y, paint)
            x += colW
            if ((i + 1) % 4 == 0) {
                x = startX
                y += rowH
            }
        }
        return y
    }
}

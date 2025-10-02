package com.loab.hannam.report

import android.content.res.Resources
import android.graphics.*
import android.util.TypedValue
import androidx.core.graphics.createBitmap
import com.loab.hannam.R
import com.loab.hannam.data.model.SurveyState
import java.text.SimpleDateFormat
import java.util.*

/**
 * 설문 결과를 A4 비율의 비트맵으로 렌더링.
 * 좌측(질문) / 우측(답변) 2-컬럼 레이아웃.
 */
object ReportRenderer {

    // ---------- Typography / Paint helpers ----------
    private fun spToPx(sp: Float): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, Resources.getSystem().displayMetrics)

    private fun newPaint(
        color: Int = Color.BLACK,
        sizeSp: Float = 14f,
        bold: Boolean = false,
        align: Paint.Align = Paint.Align.LEFT
    ) = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color
        textSize = spToPx(sizeSp)
        textAlign = align
        typeface = Typeface.create(Typeface.SANS_SERIF, if (bold) Typeface.BOLD else Typeface.NORMAL)
    }

    /** 폭을 넘지 않도록 텍스트를 여러 줄로 그리며 마지막 y(다음 줄 시작 baseline)를 반환 */
    private fun drawParagraph(
        canvas: Canvas,
        text: String,
        x: Float,
        startY: Float,
        maxWidth: Float,
        paint: Paint,
        lineSpacing: Float = 6f
    ): Float {
        val content = text.ifBlank { " " }
        var y = startY
        var start = 0
        val len = content.length
        while (start < len) {
            val count = paint.breakText(content, start, len, true, maxWidth, null)
            canvas.drawText(content, start, start + count, x, y, paint)
            y += paint.textSize + lineSpacing
            start += count
        }
        return y
    }

    private fun checkbox(canvas: Canvas, cx: Float, cy: Float, size: Float, checked: Boolean) {
        val half = size / 2f
        val r = RectF(cx - half, cy - half, cx + half, cy + half)
        val box = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK; style = Paint.Style.STROKE; strokeWidth = 2f
        }
        canvas.drawRoundRect(r, 4f, 4f, box)
        if (checked) {
            val mark = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.BLACK; style = Paint.Style.STROKE; strokeWidth = 4f; strokeCap = Paint.Cap.ROUND
            }
            canvas.drawLine(r.left + size * 0.20f, cy, cx, r.bottom - size * 0.20f, mark)
            canvas.drawLine(cx, r.bottom - size * 0.20f, r.right - size * 0.18f, r.top + size * 0.22f, mark)
        }
    }

    private fun drawDivider(canvas: Canvas, startX: Float, endX: Float, y: Float) {
        val p = Paint().apply { color = 0xFFDDDDDD.toInt(); strokeWidth = 2f }
        canvas.drawLine(startX, y, endX, y, p)
    }

    /** 체크박스 + 라벨 한 항목 */
    private fun drawCheck(
        canvas: Canvas,
        x: Float,
        yBaseline: Float,
        label: String,
        checked: Boolean,
        paint: Paint
    ) {
        val size = 18f
        val cy = yBaseline - 10f
        checkbox(canvas, x, cy, size, checked)
        canvas.drawText(label, x + 22f, yBaseline, paint)
    }

    /** 저장된 문자열을 대소문자 무시집합으로 */
    private fun toSetIgnoreCase(values: List<String>): Set<String> =
        values.map { it.trim().uppercase(Locale.ROOT) }.toSet()

    // ---------- Public render ----------
    fun render(
        state: SurveyState,
        res: Resources,
        width: Int = 1240,     // A4 비율에 가까운 픽셀 캔버스
        height: Int = 1754
    ): Bitmap {
        val bmp = createBitmap(width, height)
        val canvas = Canvas(bmp)
        canvas.drawColor(Color.WHITE)

        // 레이아웃 가이드 (2-컬럼)
        val margin = 48f
        val gutter = 24f
        val leftColX = margin
        val leftColW = 420f                  // 질문 컬럼 폭
        val rightColX = leftColX + leftColW + gutter
        val rightColW = (width - margin) - rightColX

        // 타이포
        val title = newPaint(sizeSp = 28f, bold = true)
        val subtitle = newPaint(sizeSp = 16f, bold = true)
        val label = newPaint(sizeSp = 14f, bold = true)
        val value = newPaint(sizeSp = 14f)
        val numPaint = newPaint(sizeSp = 13f, bold = true)
        val qPaint = newPaint(sizeSp = 13f)
        val ansPaint = newPaint(sizeSp = 13f)

        var y = 72f

        // 헤더
        canvas.drawText("HAIR CONDITION CHECK LIST", leftColX, y, title); y += 38f
        canvas.drawText("헤어 시술 상담", leftColX, y, subtitle); y += 24f
        drawDivider(canvas, leftColX, width - margin, y + 12f)

        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            .format(Date(state.customer.dateMillis))

        y += 42f
        canvas.drawText("DATE", leftColX, y, label)
        canvas.drawText(dateStr, rightColX, y, value)

        y += 36f
        canvas.drawText("NAME", leftColX, y, label)
        canvas.drawText(state.customer.name, rightColX, y, value)

        y += 28f
        drawDivider(canvas, leftColX, width - margin, y)

        // 공통 질문/답변 헬퍼
        val lineGap = 30f
        fun drawQuestionLine(n: Int, text: String, yBaseline: Float) {
            canvas.drawText("$n.", leftColX, yBaseline, numPaint)
            val qStart = leftColX + 22f
            drawParagraph(canvas, text, qStart, yBaseline, leftColW - 22f, qPaint)
        }

        fun drawFreeAnswer(y0: Float, txt: String): Float {
            canvas.drawText("[", rightColX, y0, ansPaint)
            canvas.drawText("]", rightColX + rightColW - 8f, y0, ansPaint)
            return drawParagraph(canvas, txt, rightColX + 16f, y0, rightColW - 24f, ansPaint)
        }

        fun drawChecksGrid(
            items: List<Pair<String, Boolean>>,
            yStart: Float,
            colW: Float = 200f,
            rowH: Float = 24f,
            cols: Int = 4
        ): Float {
            var x = rightColX
            var yGrid = yStart
            items.forEachIndexed { i, (labelText, checked) ->
                drawCheck(canvas, x, yGrid, labelText, checked, ansPaint)
                x += colW
                if ((i + 1) % cols == 0) { x = rightColX; yGrid += rowH }
            }
            return yGrid
        }

        fun ynLabels(v: Boolean?) = listOf("Y", "N") to listOf(v == true, v == false)
        fun drawYNRow(labels: List<String>, checks: List<Boolean>, yBase: Float, startX: Float) {
            var x = startX
            labels.zip(checks).forEach { (lb, ck) ->
                drawCheck(canvas, x, yBase, lb, ck, ansPaint)
                x += 100f
            }
        }

        // 1) 마지막 시술내역 (컷/펌/컬러/탈색 Y/N)
        y += lineGap
        drawQuestionLine(1, "마지막 시술내역을 모두 작성해주세요.", y)
        var row = y + 22f
        fun ynRow(titleText: String, v: Boolean?) {
            canvas.drawText(titleText, rightColX, row, value)
            drawYNRow(ynLabels(v).first, ynLabels(v).second, row, rightColX + 90f)
            row += 24f
        }
        ynRow("컷", state.hair.lastCut)
        ynRow("펌", state.hair.lastPerm)
        ynRow("컬러", state.hair.lastColor)
        ynRow("탈색", state.hair.lastBleach)
        y = row

        // 2) 이전 시술 불편사항
        y += lineGap
        drawQuestionLine(2, "이전 시술에서 불편했던 점이 있다면 적어주세요.", y)
        y = drawFreeAnswer(y, state.hair.lastTreatmentUncomfortable)

        // 3) 현재 고민
        y += lineGap
        drawQuestionLine(3, "현재 스타일중 고민되는 점을 적어주세요.", y)
        y = drawFreeAnswer(y, state.hair.currentConcerns)

        // 4) 요청/주의
        y += lineGap
        drawQuestionLine(4, "시술시 요청사항과 주의 해야할 부분이 있나요?", y)
        y = drawFreeAnswer(y, state.hair.precautions)

        // 5) 가장 중요시 여기는 부분
        y += lineGap
        drawQuestionLine(5, "헤어스타일에서 가장 중요시 여기는 부분은?", y)
        val importantSet = toSetIgnoreCase(state.hair.importantInStyle)
        val impItems = listOf(
            "자연건조" to ("NATURAL" in importantSet || "자연건조" in importantSet),
            "드라이건조" to ("BLOW" in importantSet || "드라이건조" in importantSet),
            "롤드라이" to ("ROLL" in importantSet || "롤드라이" in importantSet)
        )
        y = drawChecksGrid(impItems, y + 22f, cols = 3)

        // 6) 스타일링 레벨
        y += lineGap
        drawQuestionLine(6, "스타일링 레벨은 어떻게 되시나요?", y)
        val levelSet = toSetIgnoreCase(state.hair.stylingLevel)
        val weekRaw = state.hair.stylingLevel.firstOrNull {
            it.uppercase(Locale.ROOT).startsWith("WEEK:") || it.startsWith("주:")
        }
        val weekNum = weekRaw?.substringAfter(':')?.trim().orEmpty()
        val levelItems = listOf(
            "기기사용(고데기, 에어랩)" to ("DEVICE" in levelSet || "기기사용" in levelSet),
            "매일사용" to ("EVERYDAY" in levelSet || "매일사용" in levelSet),
            "주(${weekNum})회사용" to (levelSet.any { it.startsWith("WEEK:") } || levelSet.contains("주"))
        )
        y = drawChecksGrid(levelItems, y + 22f, cols = 3, colW = 220f)

        // 7) 평소 스타일/이미지
        y += lineGap
        drawQuestionLine(7, "평소 내 스타일과 이미지를 선택해주세요. (최대 3개까지 선택)", y)
        val usualSet = toSetIgnoreCase(state.hair.usualImage)
        val usual = listOf(
            "STANDARD" to "스탠다드", "SOFT" to "부드러운", "BOYISH" to "보이쉬한", "LIGHT" to "가벼운",
            "CASUAL" to "캐주얼", "CUTE" to "귀여운", "PROFESSIONAL" to "프로페셔널한", "GLAM" to "화려한",
            "TRENDY" to "트렌디", "NEAT" to "깔끔한", "UNIQUE" to "유니크", "SIMPLE" to "심플",
            "MODERN" to "모던", "CHIC" to "시크한", "VINTAGE" to "빈티지한", "EFFORTLESS" to "꾸안꾸"
        ).map { (key, labelKo) ->
            labelKo to (key in usualSet || labelKo.uppercase(Locale.ROOT) in usualSet)
        }
        y = drawChecksGrid(usual, y + 22f, colW = 190f, cols = 4)

        // 8) (선호 이미지가 필요하면 동일 패턴으로 추가 가능)

        // 9) 레이어 정도
        y += lineGap
        drawQuestionLine(8, "레이어 정도", y)
        val row9 = y + 22f
        val manyLayer = state.hair.layerLevel.equals("MANY", true) || state.hair.layerLevel == "많이"
        val lessLayer = state.hair.layerLevel.equals("LESS", true) || state.hair.layerLevel == "적게"
        drawCheck(canvas, rightColX + 0f, row9, "많이", manyLayer, ansPaint)
        drawCheck(canvas, rightColX + 100f, row9, "적게", lessLayer, ansPaint)
        y = row9

        // 10) 숱 정도
        y += lineGap
        drawQuestionLine(9, "숱 정도", y)
        val row10 = y + 22f
        val manyThin = state.hair.thinningLevel.equals("MANY", true) || state.hair.thinningLevel == "많이"
        val lessThin = state.hair.thinningLevel.equals("LESS", true) || state.hair.thinningLevel == "적게"
        drawCheck(canvas, rightColX + 0f, row10, "많이", manyThin, ansPaint)
        drawCheck(canvas, rightColX + 100f, row10, "적게", lessThin, ansPaint)
        y = row10

        // 11) 오늘 하고 싶은 디자인 (서술형)
        y += lineGap
        drawQuestionLine(10, "오늘하고싶은 디자인", y)
        y = drawFreeAnswer(y, state.hair.todayDesign)

        // 12) 얼굴형 아이콘 3x2
        y += lineGap
        drawQuestionLine(11, "자신이 생각하는 얼굴형", y)

        val faces = listOf(
            R.drawable.face_oval, R.drawable.face_square, R.drawable.face_rectangle,
            R.drawable.face_diamond, R.drawable.face_heart, R.drawable.face_round
        )
        val selected = state.hair.faceShapeIndex
        val cellW = 120f; val cellH = 140f; val gapX = 24f; val gapY = 24f
        var fx = rightColX; var fy = y + 16f
        val red = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.RED; style = Paint.Style.STROKE; strokeWidth = 3f }

        faces.forEachIndexed { i, resId ->
            val bm = BitmapFactory.decodeResource(res, resId)
            val dst = RectF(fx, fy, fx + cellW, fy + cellH)
            val src = Rect(0, 0, bm.width, bm.height)
            canvas.drawBitmap(bm, src, dst, null)
            if (selected == i) canvas.drawRoundRect(dst, 10f, 10f, red)
            fx += cellW + gapX
            if ((i + 1) % 3 == 0) { fx = rightColX; fy += cellH + gapY }
        }

        return bmp
    }
}

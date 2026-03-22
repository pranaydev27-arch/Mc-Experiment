package com.example.myapplication

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var drawPath: Path = Path()
    private var drawPaint: Paint = Paint()
    private var canvasPaint: Paint = Paint(Paint.DITHER_FLAG)
    private var drawCanvas: Canvas? = null
    private var canvasBitmap: Bitmap? = null
    
    private var currentShape = ShapeType.CURVE
    private var startX = 0f
    private var startY = 0f

    init {
        setupDrawing()
    }

    private fun setupDrawing() {
        drawPaint.color = Color.BLACK
        drawPaint.isAntiAlias = true
        drawPaint.strokeWidth = 5f
        drawPaint.style = Paint.Style.STROKE
        drawPaint.strokeJoin = Paint.Join.ROUND
        drawPaint.strokeCap = Paint.Cap.ROUND
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(canvasBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(canvasBitmap!!, 0f, 0f, canvasPaint)
        canvas.drawPath(drawPath, drawPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = touchX
                startY = touchY
                if (currentShape == ShapeType.CURVE) {
                    drawPath.moveTo(touchX, touchY)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (currentShape == ShapeType.CURVE) {
                    drawPath.lineTo(touchX, touchY)
                } else {
                    drawPath.reset()
                    drawPath.moveTo(startX, startY)
                    when (currentShape) {
                        ShapeType.CIRCLE -> {
                            val radius = Math.sqrt(Math.pow((touchX - startX).toDouble(), 2.0) + Math.pow((touchY - startY).toDouble(), 2.0)).toFloat()
                            drawPath.addCircle(startX, startY, radius, Path.Direction.CW)
                        }
                        ShapeType.RECTANGLE -> {
                            drawPath.addRect(startX, startY, touchX, touchY, Path.Direction.CW)
                        }
                        ShapeType.LINE -> {
                            drawPath.lineTo(touchX, touchY)
                        }
                        else -> {}
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                drawCanvas?.drawPath(drawPath, drawPaint)
                drawPath.reset()
            }
            else -> return false
        }
        invalidate()
        return true
    }

    fun setCurrentShape(shape: ShapeType) {
        currentShape = shape
    }

    fun clear() {
        drawCanvas?.drawColor(0, PorterDuff.Mode.CLEAR)
        invalidate()
    }
}

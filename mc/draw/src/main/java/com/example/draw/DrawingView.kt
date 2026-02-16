package com.example.draw

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val paint = Paint().apply {
        color = Color.BLUE
        strokeWidth = 8f
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
    }

    private sealed class Shape {
        data class Circle(val rect: RectF) : Shape()
        data class Rect(val rect: RectF) : Shape()
        data class Line(val startX: Float, val startY: Float, val endX: Float, val endY: Float) : Shape()
        data class Curve(val path: Path) : Shape()
    }

    private val shapes = mutableListOf<Shape>()
    private var currentShape = "curve"
    private var startX = 0f
    private var startY = 0f
    private var currentPath: Path? = null
    private var isDrawing = false

    fun setShape(shape: String) {
        currentShape = shape
    }

    fun clear() {
        shapes.clear()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (shape in shapes) {
            when (shape) {
                is Shape.Circle -> canvas.drawCircle(shape.rect.centerX(), shape.rect.centerY(), shape.rect.width() / 2, paint)
                is Shape.Rect -> canvas.drawRect(shape.rect, paint)
                is Shape.Line -> canvas.drawLine(shape.startX, shape.startY, shape.endX, shape.endY, paint)
                is Shape.Curve -> canvas.drawPath(shape.path, paint)
            }
        }
        
        // Draw the path currently being drawn
        currentPath?.let {
            canvas.drawPath(it, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                when (currentShape) {
                    "curve" -> {
                        currentPath = Path().apply {
                            moveTo(x, y)
                        }
                    }
                    "line" -> {
                        startX = x
                        startY = y
                        isDrawing = true
                    }
                    else -> {
                        val size = 100f
                        val rect = RectF(x - size / 2, y - size / 2, x + size / 2, y + size / 2)
                        val shape = if (currentShape == "circle") Shape.Circle(rect) else Shape.Rect(rect)
                        shapes.add(shape)
                    }
                }
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                if (currentShape == "curve") {
                    currentPath?.lineTo(x, y)
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                when (currentShape) {
                    "curve" -> {
                        currentPath?.let {
                            it.lineTo(x, y)
                            shapes.add(Shape.Curve(it))
                        }
                        currentPath = null
                    }
                    "line" -> {
                        if (isDrawing) {
                            shapes.add(Shape.Line(startX, startY, x, y))
                            isDrawing = false
                        }
                    }
                }
                invalidate()
            }
        }
        return true
    }
}
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
    private var currentShape = ShapeType.CURVE
    private var startX = 0f
    private var startY = 0f
    private var currentPath: Path? = null
    private var isDrawing = false

    fun setCurrentShape(shape: ShapeType) {
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
                    ShapeType.CURVE -> {
                        currentPath = Path().apply {
                            moveTo(x, y)
                        }
                    }
                    ShapeType.LINE -> {
                        startX = x
                        startY = y
                        isDrawing = true
                    }
                    else -> {
                        val size = 100f
                        val rect = RectF(x - size / 2, y - size / 2, x + size / 2, y + size / 2)
                        val shape = if (currentShape == ShapeType.CIRCLE) Shape.Circle(rect) else Shape.Rect(rect)
                        shapes.add(shape)
                    }
                }
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                if (currentShape == ShapeType.CURVE) {
                    currentPath?.lineTo(x, y)
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                when (currentShape) {
                    ShapeType.CURVE -> {
                        currentPath?.let {
                            it.lineTo(x, y)
                            shapes.add(Shape.Curve(it))
                        }
                        currentPath = null
                    }
                    ShapeType.LINE -> {
                        if (isDrawing) {
                            shapes.add(Shape.Line(startX, startY, x, y))
                            isDrawing = false
                        }
                    }
                    else -> {}
                }
                invalidate()
            }
        }
        return true
    }
}
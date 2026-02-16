package com.example.draw

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var drawingView: DrawingView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawingView)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        drawingView = findViewById(R.id.drawingView)

        findViewById<Button>(R.id.btnCurve).setOnClickListener {
            drawingView.setShape("curve")
        }
        findViewById<Button>(R.id.btnCircle).setOnClickListener {
            drawingView.setShape("circle")
        }
        findViewById<Button>(R.id.btnRect).setOnClickListener {
            drawingView.setShape("rect")
        }
        findViewById<Button>(R.id.btnLine).setOnClickListener {
            drawingView.setShape("line")
        }
        findViewById<Button>(R.id.btnClear).setOnClickListener {
            drawingView.clear()
        }
    }
}
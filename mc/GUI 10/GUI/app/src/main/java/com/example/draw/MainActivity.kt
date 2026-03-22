package com.example.draw

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private var fontSize = 24f
    private val colors = arrayOf(
        Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, 
        Color.CYAN, Color.YELLOW, Color.BLACK, Color.parseColor("#FF00FF")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val helloText = findViewById<TextView>(R.id.helloText)
        val btnFontSize = findViewById<Button>(R.id.btnFontSize)
        val btnColor = findViewById<Button>(R.id.btnColor)

        btnFontSize.setOnClickListener {
            fontSize += 4f
            if (fontSize > 60f) fontSize = 24f
            helloText.textSize = fontSize
        }

        btnColor.setOnClickListener {
            val randomColor = colors[Random.nextInt(colors.size)]
            helloText.setTextColor(randomColor)
        }
    }
}
package com.example.kotlincool

import android.graphics.Color
import android.graphics.Paint

// paint of everthing
object SnakeGamePaint {

    val snakeBodyPaint = Paint().apply {
        isDither = true
        isAntiAlias = true
        style = Paint.Style.FILL
        color = Color.GREEN
    }


    val snakeHeaderPaint = Paint().apply {
        isDither = true
        isAntiAlias = true
        style = Paint.Style.FILL
        color = Color.BLUE
    }

    val foodPaint = Paint().apply {
        isDither = true
        isAntiAlias = true
        style = Paint.Style.FILL
        color = Color.RED
    }


    val wallPaint = Paint().apply {
        isDither = true
        isAntiAlias = true
        style = Paint.Style.FILL
        color = Color.BLACK
    }
}
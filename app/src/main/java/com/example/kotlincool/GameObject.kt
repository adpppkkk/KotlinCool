package com.example.kotlincool

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.ViewDebug
/**
 * father class of everthign
 * */
open class GameObject(var row: Int, var column: Int) {

    /**
     * @param canvas
     * @param paint
     * */
    open fun draw(canvas: Canvas, x: Float, y: Float, paint: Paint) {
        canvas.drawRect(x, y, x + SnakeGameConfiguration.GRID_WIDTH, y + SnakeGameConfiguration.GRID_HEIGHT, paint)
    }
}
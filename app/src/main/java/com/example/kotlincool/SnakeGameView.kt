package com.example.kotlincool

import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.os.SystemClock
import android.util.AttributeSet
import android.view.View
import com.example.kotlincool.callbacks.OnCrashListener
import com.example.kotlincool.callbacks.OnEatenFoodListener
import java.util.*
import kotlin.concurrent.thread


class SnakeGameView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    /**
     * Square make up snake
     * */
    private val snake = mutableListOf<SnakeBlock>()

    /**
     * food
     * */
    private lateinit var food: Food

    /**
     * grid list
     * */
    private lateinit var gridList: MutableList<MutableList<PointF>>

    /**
     * when hit the wall
     * */
    var crashListener: OnCrashListener? = null

    /**
     * when hit the food
     * */
    var eatenListener: OnEatenFoodListener? = null

    /**
     * get/set the direction of snake
     * */
    var direction = DIRECTION.DIRECTION_RIGHT
        set(value) {
            when (value) {
                DIRECTION.DIRECTION_UP -> {
                    if (field != DIRECTION.DIRECTION_DOWN) {
                        field = value
                    }
                }
                DIRECTION.DIRECTION_DOWN -> {
                    if (field != DIRECTION.DIRECTION_UP) {
                        field = value
                    }
                }
                DIRECTION.DIRECTION_LEFT -> {
                    if (field != DIRECTION.DIRECTION_RIGHT) {
                        field = value
                    }
                }
                DIRECTION.DIRECTION_RIGHT -> {
                    if (field != DIRECTION.DIRECTION_LEFT) {
                        field = value
                    }
                }
            }
        }

    private var frequency: Long = 800//speed

    /**
     * Sta
     * */
    var isStarted = false
        private set

    private val random = Random()

    var isRunning = true

    /**
     * canvas
     * */
    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        if (!isStarted)
            return

        drawSnake(canvas!!)
        drawFood(canvas)
    }

    /**
     * draw the snake
     * */
    private fun drawSnake(canvas: Canvas) {
        snake.forEach {
            val pointF = this.gridList[it.row][it.column]
            if (it.isHead) {
                it.draw(canvas, pointF.x, pointF.y, SnakeGamePaint.snakeHeaderPaint)
            } else {
                it.draw(canvas, pointF.x, pointF.y, SnakeGamePaint.snakeBodyPaint)
            }
        }
    }

    /**
     * draw food
     * */
    private fun drawFood(canvas: Canvas) {
        val pointF = this.gridList[food.row][food.column]

        food.draw(canvas, pointF.x, pointF.y, SnakeGamePaint.foodPaint)
    }

    /**
     * move the snake
     * */
    private fun moveTo() {
        //where snake is going to be
        var newHeadRow = snake[0].row
        var newHeadColumn = snake[0].column
        when (this.direction) {
            DIRECTION.DIRECTION_UP -> {
                newHeadRow -= 1
            }
            DIRECTION.DIRECTION_DOWN -> {
                newHeadRow += 1
            }
            DIRECTION.DIRECTION_LEFT -> {
                newHeadColumn -= 1
            }
            DIRECTION.DIRECTION_RIGHT -> {
                newHeadColumn += 1
            }
        }

        //if it is food
        if (food.row == newHeadRow && food.column == newHeadColumn) {
            //if food, not move snake, but let position become head of snake
            snake[0].isHead = false

            val newHead = SnakeBlock(newHeadRow, newHeadColumn, true)
            snake.add(0, newHead)

            if (this.eatenListener != null) {
                this.eatenListener!!.onEaten()
            }
            //accelarate the snake
            if (frequency > 500) {
                frequency -= 50
            }
            //food generate
            generateFoodInRandom()
        } else {
            //collition detection

            for (i in this.snake.size - 1 downTo 1) {
                val previous = this.snake[i - 1]
                val current = this.snake[i]
                current.row = previous.row
                current.column = previous.column

            }
            //move head
            val head = snake[0]
            head.row = newHeadRow
            head.column = newHeadColumn

            //hit the wall
            if (head.row < 0
                || head.row > SnakeGameConfiguration.GAME_ROW_COUNT - 1
                || head.column < 0
                || head.column > SnakeGameConfiguration.GAME_COLUMN_COUNT - 1
            ) {
                isStarted = false
                if (this.crashListener != null) {
                    /*"Out of the border".e()
                    "head row ${head.row}".e()
                    "head column ${head.column}".e()*/
                    crashListener!!.onCrash()
                }
            }
            //hit snake itself
            else if (snake.firstOrNull { it.isHead == false && it.row == head.row && it.column == head.column } != null) {
                isStarted = false
                if (this.crashListener != null) {
                    /*"Catch itself".e()
                    "head row ${head.row}".e()
                    "head column ${head.column}".e()*/
                    crashListener!!.onCrash()
                }
            }

        }

        //draw again
        this.invalidate()
    }


    private fun measureGameMap() {
        val w = this.width
        val h = this.height
        SnakeGameConfiguration.GRID_HEIGHT = (h / SnakeGameConfiguration.GAME_ROW_COUNT).toFloat()
        SnakeGameConfiguration.GRID_WIDTH = (w / SnakeGameConfiguration.GAME_COLUMN_COUNT).toFloat()
    }

    private fun generateGird() {
        this.gridList = mutableListOf()

        for (i in 0 until SnakeGameConfiguration.GAME_ROW_COUNT) {
            val tempList = mutableListOf<PointF>()
            for (j in 0 until SnakeGameConfiguration.GAME_COLUMN_COUNT) {
                val point = PointF(j * SnakeGameConfiguration.GRID_WIDTH, i * SnakeGameConfiguration.GRID_HEIGHT)
                tempList.add(point)
            }
            this.gridList.add(tempList)
        }
    }


    private fun generateFoodInRandom() {
        var row = this.random.nextInt(SnakeGameConfiguration.GAME_ROW_COUNT)
        var column = this.random.nextInt(SnakeGameConfiguration.GAME_COLUMN_COUNT)
        while (true) {
            //not generate food at same position with snake
            if (this.snake.firstOrNull { it.row == row && it.column == column } == null)
                break
            row = this.random.nextInt(SnakeGameConfiguration.GAME_ROW_COUNT)
            column = this.random.nextInt(SnakeGameConfiguration.GAME_COLUMN_COUNT)
        }
        this.food = Food(row, column)
    }

    /**
     * initiallize the snake
     * */
    private fun generateSnake() {
        this.snake.clear()
        this.snake.add(SnakeBlock(0, 2, true))
        this.snake.add(SnakeBlock(0, 1, false))
        this.snake.add(SnakeBlock(0, 0, false))
    }

    /**
     * start game
     * */
    fun start() {
        //initiallize map
        this.measureGameMap()

        this.generateGird()
        this.generateFoodInRandom()
        this.generateSnake()

        isStarted = true
        this.invalidate()
        //start moving snake
        thread {
            while (isRunning) {
                if (isStarted) {
                    this.post {
                        moveTo()
                    }
                    SystemClock.sleep(this.frequency)
                }
            }
        }
    }

    /**
     * restart
     * */
    fun restart() {
        this.generateGird()
        this.generateFoodInRandom()
        this.generateSnake()
        this.direction = DIRECTION.DIRECTION_RIGHT
        isStarted = true
        invalidate()
    }

    /**
     * moving direction
     * */
    object DIRECTION {
        val DIRECTION_UP = 0
        val DIRECTION_DOWN = 1
        val DIRECTION_LEFT = 2
        val DIRECTION_RIGHT = 3
    }
}
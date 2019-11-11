package com.example.kotlincool

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.kotlincool.callbacks.OnCrashListener
import com.example.kotlincool.callbacks.OnEatenFoodListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    //score
    var point = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //listen the eat of food
        snake.eatenListener = object : OnEatenFoodListener {
            override fun onEaten() {
                point += 1
                tvPoint.text = point.toString()
            }
        }

        snake.crashListener = object : OnCrashListener {
            override fun onCrash() {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("The snake has dead!")
                    .setMessage("Which are you want to do?")
                    .setPositiveButton("Restart", DialogInterface.OnClickListener { dialog, which ->
                        snake.restart()
                        dialog.dismiss()
                    })
                    .setNegativeButton("Exit game", DialogInterface.OnClickListener { dialog, which ->
                        dialog.dismiss()
                        finish()
                    }).show()
            }

        }
    }

    fun start(view: View) {
        if (!snake.isStarted) {
            snake.start()
        }
    }

    fun move(view: View) {
        if (!snake.isStarted) {
            Toast.makeText(this, "Please click start button!", Toast.LENGTH_SHORT).show()
            return
        }

        when (view.id) {
            R.id.btnUp -> snake.direction = SnakeGameView.DIRECTION.DIRECTION_UP
            R.id.btnDown -> snake.direction = SnakeGameView.DIRECTION.DIRECTION_DOWN
            R.id.btnLeft -> snake.direction = SnakeGameView.DIRECTION.DIRECTION_LEFT
            R.id.btnRight -> snake.direction = SnakeGameView.DIRECTION.DIRECTION_RIGHT
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        snake.isRunning = false
    }
}
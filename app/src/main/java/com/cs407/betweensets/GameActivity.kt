package com.cs407.betweensets

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import kotlin.random.Random

class GameActivity : AppCompatActivity() {

    private lateinit var timerTextView: TextView
    private lateinit var scoreTextView: TextView
    private lateinit var levelTextView: TextView
    private lateinit var feedbackTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var doneButton: Button
    private lateinit var tracerView: View
    private lateinit var gameLayout: FrameLayout

    private var score: Int = 0
    private var level: Int = 1
    private var cooldownTime: Long = 30000L
    private var gameStarted = false
    private var objectGenerationTimer: CountDownTimer? = null
    private var gameEndTimer: CountDownTimer? = null
    private val activeObjects = mutableListOf<View>()
    private var currentSet = 1 // Get current set from intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // Initialize views
        timerTextView = findViewById(R.id.timerTextView)
        scoreTextView = findViewById(R.id.scoreTextView)
        levelTextView = findViewById(R.id.levelTextView)
        feedbackTextView = findViewById(R.id.feedbackTextView)
        progressBar = findViewById(R.id.progressBar)
        doneButton = findViewById(R.id.doneButton)
        gameLayout = findViewById(R.id.gameLayout)

        // Get current set from intent
        currentSet = intent.getIntExtra("currentSet", 1)

        // Create a tracer view for the "cursor"
        tracerView = View(this).apply {
            layoutParams = FrameLayout.LayoutParams(80, 80)
            background = getDrawable(R.drawable.tracer_circle)
            visibility = View.GONE // Initially hidden
        }
        gameLayout.addView(tracerView)

        // Handle 'Done' button click
        doneButton.setOnClickListener {
            finishGame()
        }

        // Show the initial prompt
        feedbackTextView.text = "Press down to start the game. Don't let go!"
    }

    private fun startGame() {
        if (gameStarted) return
        gameStarted = true

        feedbackTextView.text = ""
        tracerView.visibility = View.VISIBLE
        activeObjects.clear()

        updateLevel(1)
        progressBar.max = cooldownTime.toInt()
        startObjectGeneration()
        startGameEndTimer(cooldownTime)
    }

    private fun updateLevel(newLevel: Int) {
        level = newLevel
        levelTextView.text = "Level: $level"
    }

    private fun startObjectGeneration() {
        objectGenerationTimer = object : CountDownTimer(cooldownTime, 100L) {
            override fun onTick(millisUntilFinished: Long) {
                if (Random.nextInt(100) < 20) {
                    launchFallingObjects()
                }

                if ((cooldownTime - millisUntilFinished) > 5000L * level) {
                    updateLevel(level + 1)
                }
            }

            override fun onFinish() {}
        }.start()
    }

    private fun startGameEndTimer(duration: Long) {
        gameEndTimer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerTextView.text = "Time: ${millisUntilFinished / 1000}"
                progressBar.progress = (duration - millisUntilFinished).toInt()
            }

            override fun onFinish() {
                finishGame()
            }
        }.start()
    }

    private fun finishGame() {
        objectGenerationTimer?.cancel()
        gameEndTimer?.cancel()

        // Pass the final score back to WorkoutInProgressActivity
        val resultIntent = Intent()
        resultIntent.putExtra("finalScore", score)
        resultIntent.putExtra("currentSet", currentSet)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun launchFallingObjects() {
        val type = getRandomObjectType()
        val imageView = createFallingObject(type)
        imageView.tag = type
        activeObjects.add(imageView)
    }

    private fun getRandomObjectType(): String {
        val randomValue = Random.nextInt(100)
        return when {
            randomValue < 60 -> "asteroid"
            randomValue < 90 -> "apple"
            else -> "orange"
        }
    }

    private fun createFallingObject(type: String): ImageView {
        val imageView = ImageView(this)
        imageView.setImageResource(
            when (type) {
                "apple" -> R.drawable.apple
                "orange" -> R.drawable.orange
                "asteroid" -> R.drawable.asteroid
                else -> R.drawable.default_image
            }
        )

        val size = when (type) {
            "apple" -> 200
            "orange" -> 150
            "asteroid" -> 250
            else -> 150
        }

        val params = FrameLayout.LayoutParams(size, size)
        imageView.layoutParams = params
        gameLayout.addView(imageView)

        val layoutWidth = gameLayout.width
        val xPosition = if (layoutWidth > size) {
            Random.nextInt(0, layoutWidth - size).toFloat()
        } else {
            (layoutWidth / 2).toFloat()
        }

        imageView.x = xPosition
        imageView.y = 0f

        val fallDuration = Random.nextLong(800, 2000)
        ObjectAnimator.ofFloat(imageView, "translationY", gameLayout.height.toFloat()).apply {
            duration = fallDuration
            start()
            doOnEnd {
                gameLayout.removeView(imageView)
                activeObjects.remove(imageView)
            }
        }
        return imageView
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val layoutPosition = IntArray(2)
        gameLayout.getLocationOnScreen(layoutPosition)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startGame()
            }
            MotionEvent.ACTION_MOVE -> {
                val relativeX = event.rawX - layoutPosition[0]
                val relativeY = event.rawY - layoutPosition[1]

                tracerView.x = relativeX - tracerView.width / 2
                tracerView.y = relativeY - tracerView.height / 2

                checkCollisions()
            }
        }
        return true
    }

    private fun checkCollisions() {
        val iterator = activeObjects.iterator()
        while (iterator.hasNext()) {
            val obj = iterator.next()
            if (isCollision(tracerView, obj)) {
                val type = obj.tag as String
                val parent = obj.parent as FrameLayout
                when (type) {
                    "apple" -> {
                        score += 10
                        feedbackTextView.text = "Nice!"
                    }
                    "orange" -> {
                        score += 20
                        feedbackTextView.text = "Great!"
                    }
                    "asteroid" -> {
                        score -= 15
                        feedbackTextView.text = "Ouch! Avoid asteroids!"
                    }
                }
                parent.removeView(obj)
                iterator.remove()
                scoreTextView.text = "Score: $score"
            }
        }
    }

    private fun isCollision(view1: View, view2: View): Boolean {
        val rect1 = android.graphics.Rect()
        val rect2 = android.graphics.Rect()
        view1.getHitRect(rect1)
        view2.getHitRect(rect2)
        return rect1.intersect(rect2)
    }
}

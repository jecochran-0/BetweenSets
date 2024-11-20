package com.cs407.betweensets

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MotionEvent
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class GameActivity : AppCompatActivity() {

    private lateinit var timerTextView: TextView
    private lateinit var scoreTextView: TextView
    private lateinit var doneButton: TextView
    private var score: Int = 0
    private var weight: Int = 1
    private var reps: Int = 1
    private var cooldownTime: Long = 30000L // Default cooldown (30 seconds)

    private var objectGenerationInterval: Long = 2000L // Interval between each object generation (e.g., 1 second)
    private var objectGenerationTimer: CountDownTimer? = null
    private var gameEndTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // Initialize views
        timerTextView = findViewById(R.id.timerTextView)
        scoreTextView = findViewById(R.id.scoreTextView)
        doneButton = findViewById(R.id.doneButton)

        // Get data from the previous screen
        weight = intent.getIntExtra("weight", 1)
        reps = intent.getIntExtra("reps", 1)
        cooldownTime = intent.getLongExtra("cooldownTime", 30000L)

        // Set multiplier
        val multiplier = weight * reps

        // Start the game
        startGame(multiplier)

        // Handle 'done' button click
        doneButton.setOnClickListener {
            // Navigate back to WorkoutInProgressActivity
            finishGame()
        }
    }

    private fun startGame(multiplier: Int) {
        val gameDuration = getGameDuration() // Retrieve game duration
        startObjectGeneration(multiplier, gameDuration) // Start generating objects with the retrieved duration
        startGameEndTimer(gameDuration) // Start the game duration timer
    }

    private fun startObjectGeneration(multiplier: Int, gameDuration: Long) {
        objectGenerationTimer = object : CountDownTimer(gameDuration, objectGenerationInterval) {
            override fun onTick(millisUntilFinished: Long) {
                launchFallingObjects(multiplier)
            }

            override fun onFinish() {
                // Stop generating objects once the game duration is over
            }
        }.start()
    }

    private fun startGameEndTimer(gameDuration: Long) {
        gameEndTimer = object : CountDownTimer(gameDuration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerTextView.text = "Time: ${millisUntilFinished / 1000}"
            }

            override fun onFinish() {
                finishGame()
            }
        }.start()
    }

    private fun finishGame() {
        // Stop all timers
        objectGenerationTimer?.cancel()
        gameEndTimer?.cancel()

        timerTextView.text = "Time's Up!"
        // Navigate back to WorkoutInProgressActivity
        val intent = Intent(this, WorkoutInProgressActivity::class.java)
        startActivity(intent)

        // End the GameActivity
        finish()
    }

    private fun launchFallingObjects(multiplier: Int) {
        val gameLayout = findViewById<FrameLayout>(R.id.gameLayout)
        val existingPositions = mutableListOf<Pair<Float, Int>>() // To prevent overlap

        val globalLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                for (i in 0 until 10) {
                    val delay = Random.nextLong(0, 3000) // Random delay up to 3 seconds

                    gameLayout.postDelayed({
                        // Determine the object type based on weighted probability
                        val objectType = getRandomObjectType()
                        val imageView = createFallingObject(objectType, gameLayout, existingPositions)
                        imageView.setOnTouchListener { _, event ->
                            if (event.action == MotionEvent.ACTION_DOWN) {
                                handleObjectTap(imageView, objectType, multiplier)
                            }
                            true
                        }
                    }, delay)
                }
                gameLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        }

        gameLayout.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
    }

    private fun getRandomObjectType(): String {
        val randomValue = Random.nextInt(100) // Generates a random number between 0 and 99
        return when {
            randomValue < 60 -> "asteroid" // 60% chance
            randomValue < 90 -> "apple"    // 30% chance (from 60 to 89)
            else -> "orange"               // 10% chance (from 90 to 99)
        }
    }

    private fun createFallingObject(type: String, gameLayout: FrameLayout, existingPositions: MutableList<Pair<Float, Int>>): ImageView {
        val imageView = ImageView(this)

        // Set the image resource based on type
        imageView.setImageResource(
            when (type) {
                "apple" -> R.drawable.apple
                "orange" -> R.drawable.orange
                "asteroid" -> R.drawable.asteroid
                else -> R.drawable.default_image // Optional: fallback image
            }
        )

        // Adjust sizes for each type
        val size = when (type) {
            "apple" -> 250 // Larger size for apples
            "asteroid" -> Random.nextInt(150, 250) // Asteroids have a random size between 150 and 250
            "orange" -> 150 // Fixed size for oranges
            else -> 150 // Default size for any other type (if added in the future)
        }

        // Set layout parameters with the calculated size
        val params = FrameLayout.LayoutParams(size, size)
        imageView.layoutParams = params

        // Add the ImageView to the layout
        gameLayout.addView(imageView)

        // Set random horizontal starting position within the layout bounds
        val maxWidth = gameLayout.width
        val xPosition = if (maxWidth > size) Random.nextInt(0, maxWidth - size).toFloat() else 0f

        imageView.x = xPosition
        imageView.y = 0f // Start from the top

        // Randomize falling duration to create asynchronous effect
        val fallDuration = Random.nextLong(3000, 6000) // Random duration between 3-6 seconds
        imageView.animate()
            .translationY(gameLayout.height.toFloat()) // Animate to the bottom
            .setDuration(fallDuration) // Set randomized duration for the falling effect
            .withEndAction {
                gameLayout.removeView(imageView) // Remove the object after it reaches the bottom
            }

        return imageView
    }

    private fun handleObjectTap(view: ImageView, type: String, multiplier: Int) {
        when (type) {
            "apple" -> score += 1 * multiplier // User gains points for apples
            "orange" -> score += 2 * multiplier // User gains more points for oranges
            "asteroid" -> score -= 5 // User loses points for tapping asteroids
        }
        scoreTextView.text = "Score: $score"
        findViewById<FrameLayout>(R.id.gameLayout).removeView(view) // Remove the tapped object
    }

    private fun getGameDuration(): Long {
        return cooldownTime
    }
}

package com.cs407.betweensets

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
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

        // Set multiplier and start timer
        val multiplier = weight * reps
        startTimer(cooldownTime)

        // Start the game
        startGame(multiplier)

        // Handle 'done' button click
        doneButton.setOnClickListener {
            finishGame()
        }
    }

    private fun startTimer(timeInMillis: Long) {
        object : CountDownTimer(timeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerTextView.text = "Time: ${millisUntilFinished / 1000}"
            }

            override fun onFinish() {
                finishGame()
            }
        }.start()
    }

    private fun startGame(multiplier: Int) {
        launchFallingObjects(multiplier)
    }

    private fun launchFallingObjects(multiplier: Int) {
        val objectTypes = listOf("apple", "orange", "asteroid")
        val gameLayout = findViewById<FrameLayout>(R.id.gameLayout)

        // Define the OnGlobalLayoutListener separately
        val globalLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Launch multiple falling objects
                for (i in 0 until 10) {
                    val objectType = objectTypes[Random.nextInt(objectTypes.size)]
                    val imageView = createFallingObject(objectType, gameLayout)
                    imageView.setOnTouchListener { _, event ->
                        if (event.action == MotionEvent.ACTION_DOWN) {
                            handleObjectTap(imageView, objectType, multiplier)
                        }
                        true
                    }
                }
                // Remove this listener after it’s first triggered to avoid re-launching objects
                gameLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        }

        // Add the listener to the gameLayout's ViewTreeObserver
        gameLayout.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
    }


    private fun createFallingObject(type: String, gameLayout: FrameLayout): ImageView {
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

        // Set fixed width and height for visibility
        val size = 100 // Adjust size as needed
        val params = FrameLayout.LayoutParams(size, size)
        imageView.layoutParams = params

        // Add the ImageView to the layout
        gameLayout.addView(imageView)

        // Set random horizontal starting position within the layout bounds
        val maxWidth = gameLayout.width
        val xPosition = if (maxWidth > size) Random.nextInt(0, maxWidth - size) else 0
        imageView.x = xPosition.toFloat()
        imageView.y = 0f // Start from the top

        // Start the falling animation to the bottom of gameLayout
        imageView.animate()
            .translationY(gameLayout.height.toFloat()) // Animate to the bottom
            .setDuration(5000) // Set duration for the falling effect
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

    private fun finishGame() {
        timerTextView.text = "Time's Up!"
        // Any other end game logic, such as showing the final score or navigating to another screen
    }
}

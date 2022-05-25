package it.uninsubia.curiosityapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import it.uninsubia.curiosityapp.R

class CuriosityPlus : AppCompatActivity() {
    private lateinit var layoutContainer: LinearLayout
    private lateinit var buttonStart: ImageView
    private lateinit var layoutStart: FrameLayout
    private lateinit var layoutProgressbar: FrameLayout
    private lateinit var layoutGame: LinearLayout
    private lateinit var progressBar: ProgressBar

    private lateinit var countdown : CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_curiosity_plus)

        layoutContainer = findViewById(R.id.game_container_layout)
        buttonStart = findViewById(R.id.imageView_play)
        layoutStart = findViewById(R.id.start_layout)
        layoutGame = findViewById(R.id.game_internal_container)
        layoutProgressbar = findViewById(R.id.progress_bar_start)
        progressBar = findViewById(R.id.progress_bar_item)

        buttonStart.setOnClickListener {
            layoutStart.visibility = View.GONE
            layoutProgressbar.visibility = View.VISIBLE
            countdown = object: CountDownTimer(1000, 20){
                override fun onTick(p0: Long) {
                    if(progressBar.progress < progressBar.max)
                        progressBar.progress +=2
                }

                override fun onFinish() {
                    layoutProgressbar.visibility = View.GONE
                    layoutGame.visibility = View.VISIBLE
                    layoutContainer.requestLayout()
                }
            }.start()
        }
    }
}
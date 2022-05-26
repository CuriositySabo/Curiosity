package it.uninsubia.curiosityapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import it.uninsubia.curiosityapp.databinding.ActivityCuriosityPlusBinding
import it.uninsubia.curiosityapp.nav_drawer

class CuriosityPlus : AppCompatActivity() {
    private lateinit var binding: ActivityCuriosityPlusBinding
    private lateinit var countdown: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCuriosityPlusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageViewPlay.setOnClickListener {
            //if play_imageView is clicked then progress bar starts
            binding.startLayout.visibility = View.GONE
            binding.progressBarLayout.visibility = View.VISIBLE
            countdown = object : CountDownTimer(1000, 20) {
                override fun onTick(p0: Long) {//progress bar fills up
                    binding.progressBar.progress += 2
                }

                override fun onFinish() { //progress bar is no longer needed
                    binding.progressBarLayout.visibility = View.GONE
                    binding.gameContainer.visibility = View.VISIBLE
                }
            }.start()
        }
        binding.buttonExit.setOnClickListener {
            //return to home fragment
            startActivity(
                Intent(this, nav_drawer::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        }
        binding.buttonSapevo.setOnClickListener {
            //set known curiosity
        }
        binding.buttonNonSapevo.setOnClickListener {
            //set unknown curiosity
        }
    }
    private fun fetchNextCuriosity()
    {
        //fetch a random curiosity from firebase
    }
}

/* Items IDs
    * layout generale: game_general_layout
    * title: game_title
    * cardView container: game_container_card
    * frame layout start: start_layout
    * imageView clickable: imageView_play
    * progressBar layout: progress_bar_layout
    * progress bar: progress_bar
    * game layout: game_container
    * image view: iv_game
    * textview: tv_game
    * layout buttons: container_buttons
    * green button: button_sapevo
    * red button: button_non_sapevo
    * exit button: button_exit*/
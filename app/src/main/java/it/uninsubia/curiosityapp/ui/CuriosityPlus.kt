package it.uninsubia.curiosityapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.core.content.ContextCompat
import it.uninsubia.curiosityapp.R
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
                    binding.gameProgressBar.visibility = View.VISIBLE
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
            binding.gameProgressBar.setIndicatorColor(
                ContextCompat.getColor(
                    this,
                    R.color.teal_200
                )
            )
            binding.gameProgressBar.incrementProgressBy(1)
            if (binding.gameProgressBar.progress == 10) {
                binding.gameProgressBar.progress = 0

                Thread{
                    val colorDefault = binding.gameTitle.textColors
                    binding.gameTitle.text = getRandomString()
                    binding.gameTitle.setTextColor(ContextCompat.getColor(
                        this,
                        R.color.primary_light
                    ))
                    Thread.sleep(2000)
                    binding.gameTitle.setTextColor(colorDefault)
                    binding.gameTitle.text = resources.getString(R.string.game_title)
                }.start()
            }

            //save on local file

            //fetch next curiosity
        }

        binding.buttonNonSapevo.setOnClickListener {
            //set unknown curiosity
            if (binding.gameProgressBar.progress > 0) {
                binding.gameProgressBar.setIndicatorColor(
                    ContextCompat.getColor(
                        this,
                        android.R.color.holo_red_light
                    )
                )
                binding.gameProgressBar.progress -= 1
            }

            //save on local file

            //fetch next curiosity
        }
    }

    private fun fetchNextCuriosity() {
        //fetch a random curiosity from firebase
    }
    private fun saveOnFile(known: Boolean) {
        //save curiosity known or unknown on local file
    }
    private fun getRandomString():String{
        val stringList = listOf("Bravo!", "Genio!", "Ne sai eh!", "Continua così!", "Vai!","Wow!")
        return stringList[(stringList.indices).random()]
    }
}

/* Items IDs
    * layout generale: game_general_layout
    * title: game_title
    * cardView container: game_container_card
    * frame layout start: start_layout
    * imageView clickable: imageView_play
    * progressBar layout: progress_bar_layout
    * wait progress bar: progress_bar
    * game layout: game_container
    * image view: iv_game
    * textview: tv_game
    * game progress bar: game_progress_bar
    * layout buttons: container_buttons
    * green button: button_sapevo
    * red button: button_non_sapevo
    * exit button: button_exit*/